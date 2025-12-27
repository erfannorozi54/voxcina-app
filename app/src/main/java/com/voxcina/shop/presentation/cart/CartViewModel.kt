package com.voxcina.shop.presentation.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.voxcina.shop.domain.model.Cart
import com.voxcina.shop.domain.model.CartItem
import com.voxcina.shop.domain.model.CartVariant
import com.voxcina.shop.domain.model.Discount
import com.voxcina.shop.domain.model.DiscountType
import com.voxcina.shop.domain.repository.CartRepository
import com.voxcina.shop.util.AppError
import com.voxcina.shop.util.CartError
import com.voxcina.shop.util.DiscountCalculator
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
 * ViewModel for the Cart screen.
 * Manages cart state, quantity updates, discount validation, and checkout flow.
 *
 * Requirements: 2.1, 3.2, 3.5, 5.2, 5.3, 5.6
 */
@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CartUiState>(CartUiState.Loading)
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage: SharedFlow<String> = _snackbarMessage.asSharedFlow()

    init {
        loadCart()
    }

    /**
     * Handles UI events from the cart screen.
     */
    fun onEvent(event: CartEvent) {
        when (event) {
            is CartEvent.Refresh -> loadCart()
            is CartEvent.Retry -> loadCart()
            is CartEvent.UpdateQuantity -> updateQuantity(
                event.productId,
                event.variantSku,
                event.newQuantity
            )
            is CartEvent.RemoveItem -> removeItem(event.productId, event.variantSku)
            is CartEvent.ClearCart -> clearCart()
            is CartEvent.ApplyDiscount -> applyDiscount(event.code)
            is CartEvent.RemoveDiscount -> removeDiscount()
            is CartEvent.SaveForLater -> saveForLater(event.productId, event.variantSku)
            is CartEvent.Checkout -> { /* Navigation handled by UI */ }
            is CartEvent.StartShopping -> { /* Navigation handled by UI */ }
            is CartEvent.NavigateBack -> { /* Navigation handled by UI */ }
        }
    }

    /**
     * Loads the cart from the repository.
     *
     * Requirements: 2.1
     */
    fun loadCart() {
        viewModelScope.launch {
            _uiState.value = CartUiState.Loading

            when (val result = cartRepository.getCart()) {
                is Result.Success -> {
                    val cart = result.data
                    if (cart.items.isEmpty()) {
                        _uiState.value = CartUiState.Empty()
                    } else {
                        _uiState.value = CartUiState.Success(cart = cart)
                    }
                }
                is Result.Error -> {
                    _uiState.value = CartUiState.Error(
                        message = mapErrorToMessage(result.error),
                        canRetry = true
                    )
                }
            }
        }
    }

    /**
     * Updates the quantity of a cart item.
     * If quantity is 0 or less, removes the item.
     *
     * Requirements: 3.2, 3.3, 3.5
     */
    fun updateQuantity(productId: String, variantSku: String, newQuantity: Int) {
        val currentState = _uiState.value
        if (currentState !is CartUiState.Success) return

        // Find the item to update
        val item = findCartItem(currentState.cart, productId, variantSku) ?: return

        // If quantity is 0 or less, remove the item
        if (newQuantity <= 0) {
            removeItem(productId, variantSku)
            return
        }

        // Set updating state
        _uiState.update { state ->
            if (state is CartUiState.Success) {
                state.copy(
                    isUpdating = true,
                    updatingItemId = "$productId-$variantSku"
                )
            } else state
        }

        viewModelScope.launch {
            val result = cartRepository.updateItemQuantity(
                productId = productId,
                quantity = newQuantity,
                variant = item.variant
            )

            when (result) {
                is Result.Success -> {
                    val cart = result.data
                    if (cart.items.isEmpty()) {
                        _uiState.value = CartUiState.Empty()
                    } else {
                        _uiState.update { state ->
                            if (state is CartUiState.Success) {
                                state.copy(
                                    cart = cart,
                                    isUpdating = false,
                                    updatingItemId = null
                                )
                            } else CartUiState.Success(cart = cart)
                        }
                    }
                }
                is Result.Error -> {
                    _uiState.update { state ->
                        if (state is CartUiState.Success) {
                            state.copy(isUpdating = false, updatingItemId = null)
                        } else state
                    }
                    _snackbarMessage.emit(mapErrorToMessage(result.error))
                }
            }
        }
    }

    /**
     * Removes an item from the cart.
     *
     * Requirements: 3.4
     */
    fun removeItem(productId: String, variantSku: String) {
        val currentState = _uiState.value
        if (currentState !is CartUiState.Success) return

        // Find the item to remove
        val item = findCartItem(currentState.cart, productId, variantSku) ?: return

        // Set updating state
        _uiState.update { state ->
            if (state is CartUiState.Success) {
                state.copy(
                    isUpdating = true,
                    updatingItemId = "$productId-$variantSku"
                )
            } else state
        }

        viewModelScope.launch {
            val result = cartRepository.removeItem(
                productId = productId,
                variant = item.variant
            )

            when (result) {
                is Result.Success -> {
                    val cart = result.data
                    if (cart.items.isEmpty()) {
                        _uiState.value = CartUiState.Empty()
                    } else {
                        _uiState.update { state ->
                            if (state is CartUiState.Success) {
                                state.copy(
                                    cart = cart,
                                    isUpdating = false,
                                    updatingItemId = null
                                )
                            } else CartUiState.Success(cart = cart)
                        }
                    }
                }
                is Result.Error -> {
                    _uiState.update { state ->
                        if (state is CartUiState.Success) {
                            state.copy(isUpdating = false, updatingItemId = null)
                        } else state
                    }
                    _snackbarMessage.emit(mapErrorToMessage(result.error))
                }
            }
        }
    }

    /**
     * Clears all items from the cart.
     *
     * Requirements: 1.5
     */
    fun clearCart() {
        val currentState = _uiState.value
        if (currentState !is CartUiState.Success) return

        _uiState.update { state ->
            if (state is CartUiState.Success) {
                state.copy(isUpdating = true)
            } else state
        }

        viewModelScope.launch {
            when (val result = cartRepository.clearCart()) {
                is Result.Success -> {
                    _uiState.value = CartUiState.Empty()
                }
                is Result.Error -> {
                    _uiState.update { state ->
                        if (state is CartUiState.Success) {
                            state.copy(isUpdating = false)
                        } else state
                    }
                    _snackbarMessage.emit(mapErrorToMessage(result.error))
                }
            }
        }
    }

    /**
     * Applies a discount code to the cart.
     * Validates the code and checks minimum order requirements.
     *
     * Requirements: 5.2, 5.3, 5.6
     */
    fun applyDiscount(code: String) {
        val currentState = _uiState.value
        if (currentState !is CartUiState.Success) return

        // Trim and validate code
        val trimmedCode = code.trim()
        if (trimmedCode.isBlank()) return

        // Set discount loading state
        _uiState.update { state ->
            if (state is CartUiState.Success) {
                state.copy(discountState = DiscountState.Loading)
            } else state
        }

        viewModelScope.launch {
            when (val result = cartRepository.validateDiscountCode(trimmedCode)) {
                is Result.Success -> {
                    val discount = result.data
                    val cart = (uiState.value as? CartUiState.Success)?.cart ?: return@launch

                    // Check minimum order amount
                    if (cart.summary.subtotal < discount.minOrderAmount) {
                        _uiState.update { state ->
                            if (state is CartUiState.Success) {
                                state.copy(
                                    discountState = DiscountState.Error(
                                        "حداقل مبلغ سفارش ${formatPrice(discount.minOrderAmount)} تومان است"
                                    )
                                )
                            } else state
                        }
                        return@launch
                    }

                    // Apply discount
                    _uiState.update { state ->
                        if (state is CartUiState.Success) {
                            state.copy(discountState = DiscountState.Applied(discount))
                        } else state
                    }
                }
                is Result.Error -> {
                    _uiState.update { state ->
                        if (state is CartUiState.Success) {
                            state.copy(
                                discountState = DiscountState.Error(mapErrorToMessage(result.error))
                            )
                        } else state
                    }
                }
            }
        }
    }

    /**
     * Removes the applied discount.
     */
    fun removeDiscount() {
        _uiState.update { state ->
            if (state is CartUiState.Success) {
                state.copy(discountState = DiscountState.Idle)
            } else state
        }
    }

    /**
     * Saves an item for later (moves to wishlist).
     * Note: This is a placeholder - actual implementation depends on wishlist API.
     *
     * Requirements: 4.2
     */
    private fun saveForLater(productId: String, variantSku: String) {
        viewModelScope.launch {
            // TODO: Implement save for later when wishlist API is available
            // For now, just remove from cart
            removeItem(productId, variantSku)
            _snackbarMessage.emit("محصول ذخیره شد")
        }
    }

    /**
     * Calculates the discount amount based on discount type.
     */
    fun calculateDiscountAmount(discount: Discount, subtotal: Long): Long {
        return when (discount.type) {
            DiscountType.PERCENTAGE -> (subtotal * discount.value) / 100
            DiscountType.FIXED -> discount.value.toLong()
        }
    }

    /**
     * Gets the discount percentage for display.
     * Uses DiscountCalculator for percentage-based discounts.
     */
    fun getDiscountPercentage(discount: Discount): Int? {
        return when (discount.type) {
            DiscountType.PERCENTAGE -> discount.value
            DiscountType.FIXED -> null
        }
    }

    /**
     * Finds a cart item by product ID and variant SKU.
     */
    private fun findCartItem(cart: Cart, productId: String, variantSku: String): CartItem? {
        return cart.items.find { 
            it.product.id == productId && it.variant.sku == variantSku 
        }
    }

    /**
     * Maps AppError to user-friendly Persian error messages.
     */
    private fun mapErrorToMessage(error: AppError): String {
        return when (error) {
            is AppError.NetworkError -> "خطا در اتصال به سرور"
            is CartError.CartLoadFailed -> "خطا در بارگذاری سبد خرید"
            is CartError.ItemUpdateFailed -> "خطا در بروزرسانی تعداد"
            is CartError.ItemRemoveFailed -> "خطا در حذف محصول"
            is CartError.ClearCartFailed -> "خطا در خالی کردن سبد خرید"
            is CartError.InsufficientStock -> "موجودی کافی نیست"
            is CartError.DiscountInvalid -> error.message
            is CartError.DiscountExpired -> "کد تخفیف منقضی شده است"
            is CartError.DiscountMinOrderNotMet -> 
                "حداقل مبلغ سفارش ${formatPrice(error.minAmount)} تومان است"
            is AppError.ServerError -> error.message
            is AppError.UnknownError -> error.message
            else -> error.message
        }
    }

    /**
     * Formats price with thousand separators.
     */
    private fun formatPrice(price: Long): String {
        return String.format("%,d", price)
    }
}
