package com.voxcina.shop.presentation.checkout

import com.voxcina.shop.domain.model.CardDetails
import com.voxcina.shop.domain.model.PaymentMethod
import com.voxcina.shop.domain.model.ShippingMethod
import com.voxcina.shop.presentation.home.components.BottomNavDestination

/**
 * Sealed class representing all user interaction events on the checkout screen.
 *
 * Requirements: 1.2, 3.4, 4.2, 5.3, 8.5
 */
sealed class CheckoutEvent {

    // ============ Navigation Events ============

    /**
     * User tapped the back button to return to cart.
     *
     * Requirements: 1.2
     */
    data object NavigateBack : CheckoutEvent()

    /**
     * User tapped to change/select shipping address.
     *
     * Requirements: 3.4
     */
    data object NavigateToAddresses : CheckoutEvent()

    /**
     * User tapped a bottom navigation item.
     *
     * Requirements: 11.4
     */
    data class BottomNavClick(val destination: BottomNavDestination) : CheckoutEvent()

    // ============ Data Loading Events ============

    /**
     * User triggered refresh/reload of checkout data.
     */
    data object Refresh : CheckoutEvent()

    /**
     * User tapped retry on error state.
     */
    data object Retry : CheckoutEvent()

    // ============ Selection Events ============

    /**
     * User selected a delivery/shipping method.
     *
     * Requirements: 4.2
     */
    data class SelectDeliveryMethod(val method: ShippingMethod) : CheckoutEvent()

    /**
     * User selected a payment method.
     *
     * Requirements: 5.3
     */
    data class SelectPaymentMethod(val method: PaymentMethod) : CheckoutEvent()

    // ============ Input Change Events ============

    /**
     * User updated card details (card number, expiry, CVV, or save card option).
     *
     * Requirements: 6.1
     */
    data class UpdateCardDetails(val cardDetails: CardDetails) : CheckoutEvent()

    /**
     * User changed the card number input.
     *
     * Requirements: 6.1, 6.2
     */
    data class UpdateCardNumber(val cardNumber: String) : CheckoutEvent()

    /**
     * User changed the expiry date input.
     *
     * Requirements: 6.1
     */
    data class UpdateExpiryDate(val expiryDate: String) : CheckoutEvent()

    /**
     * User changed the CVV input.
     *
     * Requirements: 6.1, 6.3
     */
    data class UpdateCvv(val cvv: String) : CheckoutEvent()

    /**
     * User toggled the save card checkbox.
     *
     * Requirements: 6.5
     */
    data class UpdateSaveCard(val saveCard: Boolean) : CheckoutEvent()

    // ============ Discount Events ============

    /**
     * User entered a discount code.
     */
    data class UpdateDiscountCode(val code: String) : CheckoutEvent()

    /**
     * User submitted a discount code for validation.
     *
     * Requirements: 7.2
     */
    data class ApplyDiscount(val code: String) : CheckoutEvent()

    /**
     * User removed the applied discount.
     */
    data object RemoveDiscount : CheckoutEvent()

    /**
     * User clicked on a disabled payment method.
     */
    data object PaymentMethodComingSoon : CheckoutEvent()

    // ============ Checkout Action Events ============

    /**
     * User tapped the checkout/pay button.
     *
     * Requirements: 8.5
     */
    data object ProcessCheckout : CheckoutEvent()

    /**
     * User tapped to proceed to payment screen.
     */
    data object ProceedToPayment : CheckoutEvent()

    /**
     * User tapped to expand order details.
     *
     * Requirements: 8.3
     */
    data object ExpandOrderDetails : CheckoutEvent()

    /**
     * User tapped to collapse order details.
     */
    data object CollapseOrderDetails : CheckoutEvent()
}
