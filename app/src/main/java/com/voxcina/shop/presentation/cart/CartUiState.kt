package com.voxcina.shop.presentation.cart

import com.voxcina.shop.domain.model.Cart
import com.voxcina.shop.domain.model.Discount

/**
 * Sealed class representing all possible UI states for the cart screen.
 *
 * Requirements: 2.8, 10.1, 10.2
 */
sealed class CartUiState {

    /**
     * Initial loading state when the cart screen first loads.
     * Displays skeleton/shimmer placeholders for cart items.
     *
     * Requirements: 10.1
     */
    data object Loading : CartUiState()

    /**
     * Success state containing cart data with items.
     *
     * Requirements: 2.8
     */
    data class Success(
        val cart: Cart,
        val discountState: DiscountState = DiscountState.Idle,
        val isUpdating: Boolean = false,
        val updatingItemId: String? = null
    ) : CartUiState() {

        /**
         * Returns the total number of items in the cart.
         */
        val itemCount: Int
            get() = cart.items.sumOf { it.quantity }

        /**
         * Returns true if a specific item is being updated.
         */
        fun isItemUpdating(productId: String, variantSku: String): Boolean {
            return isUpdating && updatingItemId == "$productId-$variantSku"
        }

        /**
         * Returns the applied discount if any.
         */
        val appliedDiscount: Discount?
            get() = (discountState as? DiscountState.Applied)?.discount
    }

    /**
     * Empty state when the cart has no items.
     *
     * Requirements: 2.8, 9.4
     */
    data class Empty(
        val message: String = "سبد خرید شما خالی است"
    ) : CartUiState()

    /**
     * Error state when the cart fails to load.
     *
     * Requirements: 10.2
     */
    data class Error(
        val message: String,
        val canRetry: Boolean = true
    ) : CartUiState()
}

/**
 * Sealed class representing the state of discount code validation.
 *
 * Requirements: 5.2, 5.4, 5.5
 */
sealed class DiscountState {

    /**
     * Initial state - no discount code entered or validated.
     */
    data object Idle : DiscountState()

    /**
     * Loading state while validating discount code.
     */
    data object Loading : DiscountState()

    /**
     * Success state when discount code is valid and applied.
     *
     * Requirements: 5.3, 5.4
     */
    data class Applied(val discount: Discount) : DiscountState()

    /**
     * Error state when discount code validation fails.
     *
     * Requirements: 5.5, 5.6
     */
    data class Error(val message: String) : DiscountState()
}

/**
 * Events that can be triggered from the cart screen UI.
 */
sealed class CartEvent {

    /**
     * User triggered refresh/reload of cart.
     */
    data object Refresh : CartEvent()

    /**
     * User tapped retry on error state.
     */
    data object Retry : CartEvent()

    /**
     * User changed quantity of an item.
     *
     * Requirements: 3.2, 3.3
     */
    data class UpdateQuantity(
        val productId: String,
        val variantSku: String,
        val newQuantity: Int
    ) : CartEvent()

    /**
     * User tapped remove on an item.
     *
     * Requirements: 3.4
     */
    data class RemoveItem(
        val productId: String,
        val variantSku: String
    ) : CartEvent()

    /**
     * User tapped clear cart button.
     *
     * Requirements: 1.5
     */
    data object ClearCart : CartEvent()

    /**
     * User submitted a discount code.
     *
     * Requirements: 5.2
     */
    data class ApplyDiscount(val code: String) : CartEvent()

    /**
     * User removed applied discount.
     */
    data object RemoveDiscount : CartEvent()

    /**
     * User tapped save for later on an item.
     *
     * Requirements: 4.2
     */
    data class SaveForLater(
        val productId: String,
        val variantSku: String
    ) : CartEvent()

    /**
     * User tapped checkout button.
     *
     * Requirements: 7.5
     */
    data object Checkout : CartEvent()

    /**
     * User tapped start shopping button (empty state).
     *
     * Requirements: 9.3
     */
    data object StartShopping : CartEvent()

    /**
     * User tapped back button.
     *
     * Requirements: 1.4
     */
    data object NavigateBack : CartEvent()
}
