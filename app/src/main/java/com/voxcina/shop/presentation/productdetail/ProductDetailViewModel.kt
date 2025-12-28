package com.voxcina.shop.presentation.productdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.voxcina.shop.domain.model.CartVariant
import com.voxcina.shop.domain.model.ColorVariant
import com.voxcina.shop.domain.model.ProductDetail
import com.voxcina.shop.domain.model.SizeVariant
import com.voxcina.shop.domain.repository.CartRepository
import com.voxcina.shop.domain.usecase.GetProductDetailUseCase
import com.voxcina.shop.util.AppError
import com.voxcina.shop.util.CartError
import com.voxcina.shop.util.ProductError
import com.voxcina.shop.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Product Details screen.
 * Manages product state, color/size selection, quantity changes, and add-to-cart flow.
 *
 * Requirements: 1.1, 5.2, 5.3, 5.4, 6.2, 6.4, 10.3, 10.4, 10.5, 10.6
 */
@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val getProductDetailUseCase: GetProductDetailUseCase,
    private val cartRepository: CartRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val productId: String = checkNotNull(savedStateHandle["productId"])
    private val initialColorHex: String? = savedStateHandle["colorHex"]

    private val _uiState = MutableStateFlow<ProductDetailUiState>(ProductDetailUiState.Loading)
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage: SharedFlow<String> = _snackbarMessage.asSharedFlow()

    private val _addToCartSuccess = MutableSharedFlow<Unit>()
    val addToCartSuccess: SharedFlow<Unit> = _addToCartSuccess.asSharedFlow()

    init {
        loadProduct()
    }

    /**
     * Handles UI events from the product details screen.
     */
    fun onEvent(event: ProductDetailEvent) {
        when (event) {
            is ProductDetailEvent.Retry -> loadProduct()
            is ProductDetailEvent.SelectColor -> onColorSelected(event.colorVariant)
            is ProductDetailEvent.SelectSize -> onSizeSelected(event.sizeVariant)
            is ProductDetailEvent.ChangeQuantity -> onQuantityChanged(event.quantity)
            is ProductDetailEvent.AddToCart -> onAddToCart()
            is ProductDetailEvent.ToggleFavorite -> onToggleFavorite()
            is ProductDetailEvent.ToggleDescription -> onToggleDescription()
            is ProductDetailEvent.ClearError -> clearAddToCartError()
            is ProductDetailEvent.Share -> { /* Handled by UI */ }
            is ProductDetailEvent.NavigateBack -> { /* Handled by UI */ }
            is ProductDetailEvent.ViewAllReviews -> { /* Handled by UI */ }
        }
    }


    /**
     * Loads the product details from the repository.
     *
     * Requirements: 11.1, 11.2, 11.3
     */
    fun loadProduct() {
        viewModelScope.launch {
            _uiState.value = ProductDetailUiState.Loading

            when (val result = getProductDetailUseCase(productId)) {
                is Result.Success -> {
                    val product = result.data
                    val initialColor = findInitialColorVariant(product)
                    val displayImages = composeDisplayImages(product.mainImages, initialColor)

                    _uiState.value = ProductDetailUiState.Success(
                        product = product,
                        selectedColorVariant = initialColor,
                        selectedSize = null,
                        quantity = 1,
                        isFavorite = false, // TODO: Load from favorites repository
                        isAddingToCart = false,
                        addToCartError = null,
                        displayImages = displayImages,
                        isDescriptionExpanded = false
                    )
                }
                is Result.Error -> {
                    _uiState.value = when (result.error) {
                        is ProductError.ProductNotFound -> ProductDetailUiState.NotFound
                        else -> ProductDetailUiState.Error(
                            message = mapErrorToMessage(result.error),
                            canRetry = true
                        )
                    }
                }
            }
        }
    }

    /**
     * Handles color variant selection.
     * Updates the gallery images and resets size selection.
     *
     * Requirements: 5.2, 5.3, 5.4
     */
    fun onColorSelected(colorVariant: ColorVariant) {
        val currentState = _uiState.value
        if (currentState !is ProductDetailUiState.Success) return

        // Don't update if same color is selected
        if (currentState.selectedColorVariant.color == colorVariant.color) return

        val displayImages = composeDisplayImages(
            currentState.product.mainImages,
            colorVariant
        )

        _uiState.update { state ->
            if (state is ProductDetailUiState.Success) {
                state.copy(
                    selectedColorVariant = colorVariant,
                    selectedSize = null, // Reset size when color changes
                    quantity = 1, // Reset quantity when color changes
                    displayImages = displayImages,
                    addToCartError = null
                )
            } else state
        }
    }

    /**
     * Handles size variant selection.
     * Updates the maximum quantity based on selected size's stock.
     *
     * Requirements: 6.2, 6.4
     */
    fun onSizeSelected(sizeVariant: SizeVariant) {
        val currentState = _uiState.value
        if (currentState !is ProductDetailUiState.Success) return

        // Don't allow selecting out-of-stock sizes
        if (sizeVariant.quantity <= 0) return

        // Adjust quantity if it exceeds new max
        val newQuantity = minOf(currentState.quantity, sizeVariant.quantity)

        _uiState.update { state ->
            if (state is ProductDetailUiState.Success) {
                state.copy(
                    selectedSize = sizeVariant,
                    quantity = maxOf(1, newQuantity),
                    addToCartError = null
                )
            } else state
        }
    }

    /**
     * Handles quantity changes.
     * Enforces bounds: minimum 1, maximum = selected size's quantity.
     *
     * Requirements: 10.3, 10.4
     */
    fun onQuantityChanged(newQuantity: Int) {
        val currentState = _uiState.value
        if (currentState !is ProductDetailUiState.Success) return

        val maxQuantity = currentState.selectedSize?.quantity ?: 1
        val boundedQuantity = newQuantity.coerceIn(1, maxQuantity)

        _uiState.update { state ->
            if (state is ProductDetailUiState.Success) {
                state.copy(quantity = boundedQuantity)
            } else state
        }
    }

    /**
     * Handles add to cart action.
     * Validates size selection and adds item to cart.
     *
     * Requirements: 10.5, 10.6
     */
    fun onAddToCart() {
        val currentState = _uiState.value
        if (currentState !is ProductDetailUiState.Success) return

        // Validate size selection
        val selectedSize = currentState.selectedSize
        if (selectedSize == null) {
            _uiState.update { state ->
                if (state is ProductDetailUiState.Success) {
                    state.copy(addToCartError = "لطفاً سایز را انتخاب کنید")
                } else state
            }
            return
        }

        // Validate stock
        if (selectedSize.quantity <= 0) {
            _uiState.update { state ->
                if (state is ProductDetailUiState.Success) {
                    state.copy(addToCartError = "محصول ناموجود است")
                } else state
            }
            return
        }

        // Set loading state
        _uiState.update { state ->
            if (state is ProductDetailUiState.Success) {
                state.copy(isAddingToCart = true, addToCartError = null)
            } else state
        }

        viewModelScope.launch {
            val variant = CartVariant(
                size = selectedSize.size,
                color = currentState.selectedColorVariant.color,
                colorName = currentState.selectedColorVariant.colorName,
                sku = selectedSize.sku
            )

            val result = cartRepository.addItem(
                productId = currentState.product.id,
                quantity = currentState.quantity,
                variant = variant
            )

            when (result) {
                is Result.Success -> {
                    _uiState.update { state ->
                        if (state is ProductDetailUiState.Success) {
                            state.copy(isAddingToCart = false)
                        } else state
                    }
                    _addToCartSuccess.emit(Unit)
                    _snackbarMessage.emit("محصول به سبد خرید اضافه شد")
                }
                is Result.Error -> {
                    _uiState.update { state ->
                        if (state is ProductDetailUiState.Success) {
                            state.copy(
                                isAddingToCart = false,
                                addToCartError = mapErrorToMessage(result.error)
                            )
                        } else state
                    }
                }
            }
        }
    }


    /**
     * Toggles the favorite status of the product.
     *
     * Requirements: 2.3
     */
    private fun onToggleFavorite() {
        val currentState = _uiState.value
        if (currentState !is ProductDetailUiState.Success) return

        // TODO: Implement actual favorite toggle with repository
        _uiState.update { state ->
            if (state is ProductDetailUiState.Success) {
                state.copy(isFavorite = !state.isFavorite)
            } else state
        }
    }

    /**
     * Toggles the description expanded state.
     *
     * Requirements: 8.3
     */
    private fun onToggleDescription() {
        _uiState.update { state ->
            if (state is ProductDetailUiState.Success) {
                state.copy(isDescriptionExpanded = !state.isDescriptionExpanded)
            } else state
        }
    }

    /**
     * Clears the add-to-cart error message.
     */
    private fun clearAddToCartError() {
        _uiState.update { state ->
            if (state is ProductDetailUiState.Success) {
                state.copy(addToCartError = null)
            } else state
        }
    }

    /**
     * Finds the initial color variant to display.
     * Uses initialColorHex if provided, otherwise defaults to first color.
     */
    private fun findInitialColorVariant(product: ProductDetail): ColorVariant {
        if (initialColorHex != null) {
            val matchingColor = product.colorVariants.find { 
                it.color.equals(initialColorHex, ignoreCase = true) 
            }
            if (matchingColor != null) return matchingColor
        }
        return product.colorVariants.first()
    }

    /**
     * Composes the display images list from main images and selected color variant images.
     * Property 1: Display Images Composition
     * displayImages = mainImages + colorVariant.images
     * 
     * Images are ordered: color variant images first (specific to selected color),
     * then main images (shared across all colors).
     * All relative URLs are converted to absolute URLs.
     *
     * Requirements: 1.1, 5.3
     */
    private fun composeDisplayImages(
        mainImages: List<String>,
        colorVariant: ColorVariant
    ): List<String> {
        // Color variant images first, then main images
        val allImages = colorVariant.images + mainImages
        // Convert relative URLs to absolute URLs
        return allImages.map { url ->
            if (url.startsWith("http")) url else "$BASE_IMAGE_URL$url"
        }
    }

    companion object {
        private const val BASE_IMAGE_URL = "https://voxcina.com"
    }

    /**
     * Maps AppError to user-friendly Persian error messages.
     */
    private fun mapErrorToMessage(error: AppError): String {
        return when (error) {
            is AppError.NetworkError -> "خطا در اتصال به سرور"
            is ProductError.ProductNotFound -> "محصول یافت نشد"
            is ProductError.ProductLoadFailed -> "خطا در بارگذاری محصول"
            is ProductError.InvalidProductId -> "شناسه محصول نامعتبر است"
            is CartError.InsufficientStock -> "موجودی کافی نیست"
            is AppError.ServerError -> error.message
            is AppError.UnknownError -> error.message
            else -> error.message
        }
    }
}
