package com.voxcina.shop.presentation.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.voxcina.shop.domain.model.Cart
import com.voxcina.shop.domain.model.CartItem
import com.voxcina.shop.domain.model.CartVariant
import com.voxcina.shop.domain.model.Discount
import com.voxcina.shop.domain.model.DiscountType
import com.voxcina.shop.domain.model.VoucherSource
import com.voxcina.shop.domain.model.colorsOverlap
import com.voxcina.shop.domain.model.discountAmountFor
import com.voxcina.shop.domain.model.willVoucherSurviveRemovalOf
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
            // Preserve the applied voucher across reloads (e.g. pull-to-refresh)
            // so it is not silently dropped from the UI while the backend still
            // counts it as active.
            val previousDiscount = (uiState.value as? CartUiState.Success)?.appliedDiscount
            _uiState.value = CartUiState.Loading

            when (val result = cartRepository.getCart()) {
                is Result.Success -> {
                    val cart = result.data
                    if (cart.items.isEmpty()) {
                        _uiState.value = CartUiState.Empty()
                    } else {
                        _uiState.value = CartUiState.Success(
                            cart = cart,
                            discountState = if (previousDiscount != null) {
                                DiscountState.Applied(previousDiscount)
                            } else {
                                DiscountState.Idle
                            }
                        )
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
                        // Re-validate an applied admin voucher's minimum order
                        // after a quantity change; drop + deactivate it if the
                        // subtotal no longer qualifies.
                        revalidateAppliedVoucher(cart)
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
     * Re-validates the applied voucher against the (possibly changed) cart.
     * Admin codes are dropped and deactivated when the subtotal falls below
     * minOrderAmount. Negotiated / cart-recovery coupons are re-checked
     * against their required products.
     */
    private fun revalidateAppliedVoucher(cart: Cart) {
        val currentState = _uiState.value as? CartUiState.Success ?: return
        val discount = currentState.appliedDiscount ?: return

        val stillValid = when (discount.source) {
            VoucherSource.ADMIN ->
                cart.summary.subtotal >= discount.minOrderAmount
            else -> {
                // Every required product must still be present in the cart.
                if (discount.requiredColors.isNotEmpty()) {
                    discount.requiredColors.all { required ->
                        cart.items.any { item ->
                            item.product.id == required.productId &&
                                (required.color.isNullOrBlank() && required.colorName.isNullOrBlank() ||
                                    colorsOverlap(
                                        required.color, required.colorName,
                                        item.variant.color, item.variant.colorName
                                    ))
                        }
                    }
                } else if (discount.productIds.isNotEmpty()) {
                    discount.productIds.all { pid -> cart.items.any { it.product.id == pid } }
                } else {
                    true
                }
            }
        }

        if (!stillValid) {
            viewModelScope.launch {
                cartRepository.deactivateVoucher(discount.code)
            }
            _uiState.update { state ->
                if (state is CartUiState.Success) {
                    state.copy(discountState = DiscountState.Idle)
                } else state
            }
            AppliedVoucherTransfer.clear()
        }
    }

    /**
     * Removes an item from the cart.
     * If the applied voucher would no longer be valid after this removal
     * (e.g. the item was required by a negotiated coupon, or the remaining
     * subtotal drops below the code's minimum), the voucher is deactivated
     * on the backend first — mirroring the web front-end's
     * ConfirmRemoveModal flow.
     *
     * Requirements: 3.4
     */
    fun removeItem(productId: String, variantSku: String) {
        val currentState = _uiState.value
        if (currentState !is CartUiState.Success) return

        // Find the item to remove
        val item = findCartItem(currentState.cart, productId, variantSku) ?: return
        val appliedDiscount = currentState.appliedDiscount

        // Removing this item invalidates the applied voucher
        val willInvalidateVoucher = appliedDiscount != null &&
            !currentState.cart.willVoucherSurviveRemovalOf(appliedDiscount, item)

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
            if (willInvalidateVoucher) {
                // Deactivate the voucher first (fire-and-forget semantics
                // matching the web front-end removePromoCode call).
                cartRepository.deactivateVoucher(appliedDiscount.code)
            }

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
                                    updatingItemId = null,
                                    discountState = if (willInvalidateVoucher) {
                                        DiscountState.Idle
                                    } else {
                                        state.discountState
                                    }
                                )
                            } else CartUiState.Success(cart = cart)
                        }
                    }
                }
                is Result.Error -> {
                    _uiState.update { state ->
                        if (state is CartUiState.Success) {
                            state.copy(
                                isUpdating = false,
                                updatingItemId = null,
                                // The voucher was already deactivated above;
                                // keep the UI consistent even if the removal
                                // itself failed.
                                discountState = if (willInvalidateVoucher) {
                                    DiscountState.Idle
                                } else {
                                    state.discountState
                                }
                            )
                        } else state
                    }
                    if (willInvalidateVoucher) {
                        AppliedVoucherTransfer.clear()
                    }
                    _snackbarMessage.emit(mapErrorToMessage(result.error))
                }
            }
        }
    }

    /**
     * Clears all items from the cart.
     * Deactivates the applied voucher first so its usage counter is not left
     * hanging (a negotiated coupon would otherwise stay marked as used and
     * could never be re-applied).
     *
     * Requirements: 1.5
     */
    fun clearCart() {
        val currentState = _uiState.value
        if (currentState !is CartUiState.Success) return
        val appliedDiscount = currentState.appliedDiscount

        _uiState.update { state ->
            if (state is CartUiState.Success) {
                state.copy(isUpdating = true)
            } else state
        }

        viewModelScope.launch {
            if (appliedDiscount != null) {
                cartRepository.deactivateVoucher(appliedDiscount.code)
            }

            when (val result = cartRepository.clearCart()) {
                is Result.Success -> {
                    AppliedVoucherTransfer.clear()
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
     * Applies a discount code / voucher to the cart.
     * Validates the code against the backend (admin code or negotiated /
     * cart-recovery coupon) and checks minimum order requirements.
     * After a successful apply, the voucher is marked as used on the backend.
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
            val cart = (uiState.value as? CartUiState.Success)?.cart ?: return@launch

            when (val result = cartRepository.applyVoucher(trimmedCode, cart)) {
                is Result.Success -> {
                    val discount = result.data
                    val updatedCart = (uiState.value as? CartUiState.Success)?.cart ?: cart

                    // Check minimum order amount (admin codes only; negotiated
                    // and cart-recovery coupons are validated server-side)
                    if (updatedCart.summary.subtotal < discount.minOrderAmount) {
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
                    // Hand the voucher to the checkout screen so the user
                    // doesn't have to re-apply it there.
                    AppliedVoucherTransfer.set(discount)

                    // Mark the voucher as used on the backend (fire-and-forget,
                    // mirroring the web front-end /discounts/activate call).
                    // The backend guards the increment against the usage cap
                    // and treats `used` as "applied to cart", so activation is
                    // safe for every voucher type.
                    cartRepository.activateVoucher(discount.code)
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
     * Removes the applied discount and deactivates the voucher on the backend.
     */
    fun removeDiscount() {
        val appliedDiscount = (uiState.value as? CartUiState.Success)?.appliedDiscount

        _uiState.update { state ->
            if (state is CartUiState.Success) {
                state.copy(discountState = DiscountState.Idle)
            } else state
        }
        AppliedVoucherTransfer.clear()

        if (appliedDiscount != null) {
            viewModelScope.launch {
                cartRepository.deactivateVoucher(appliedDiscount.code)
            }
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
     * Negotiated/cart-recovery vouchers discount only their required-products
     * base (Cart.discountAmountFor), matching the backend's calculation.
     */
    fun calculateDiscountAmount(discount: Discount, subtotal: Long): Long {
        val cart = (uiState.value as? CartUiState.Success)?.cart
            ?: return when (discount.type) {
                DiscountType.PERCENTAGE -> (subtotal * discount.value) / 100
                DiscountType.FIXED -> discount.value.toLong()
            }
        return cart.discountAmountFor(discount)
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
