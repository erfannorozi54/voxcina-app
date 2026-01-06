package com.voxcina.shop.presentation.checkout

import com.voxcina.shop.domain.model.Cart
import com.voxcina.shop.domain.model.CardDetails
import com.voxcina.shop.domain.model.Discount
import com.voxcina.shop.domain.model.PaymentMethod
import com.voxcina.shop.domain.model.ShippingMethod
import com.voxcina.shop.domain.model.UserAddress
import com.voxcina.shop.presentation.home.components.BottomNavDestination

/**
 * Sealed class representing all possible UI states for the checkout screen.
 *
 * Requirements: 9.1, 9.2
 */
sealed class CheckoutUiState {

    /**
     * Initial loading state when the checkout screen first loads.
     * Displays shimmer placeholders for checkout content.
     *
     * Requirements: 9.1
     */
    data object Loading : CheckoutUiState()

    /**
     * Success state containing all checkout data.
     *
     * Requirements: 9.1, 9.2
     */
    data class Success(
        val cart: Cart,
        val selectedAddress: UserAddress?,
        val shippingMethods: List<ShippingMethod>,
        val selectedShippingMethod: ShippingMethod?,
        val selectedPaymentMethod: PaymentMethod = PaymentMethod.BANK_CARD,
        val cardDetails: CardDetails = CardDetails(),
        val discountState: CheckoutDiscountState = CheckoutDiscountState.Idle,
        val isProcessing: Boolean = false,
        val isShippingLoading: Boolean = false,
        val isPaymentLoading: Boolean = false,
        val validationErrors: Map<String, String> = emptyMap()
    ) : CheckoutUiState() {

        /**
         * Returns the total number of items in the cart.
         */
        val itemCount: Int
            get() = cart.items.sumOf { it.quantity }

        /**
         * Returns true if the card details form should be visible.
         * Only visible when BANK_CARD payment method is selected.
         *
         * Requirements: 5.5
         */
        val showCardDetailsForm: Boolean
            get() = selectedPaymentMethod == PaymentMethod.BANK_CARD

        /**
         * Returns true if an address needs to be added.
         *
         * Requirements: 3.5
         */
        val needsAddress: Boolean
            get() = selectedAddress == null

        /**
         * Returns the applied discount if any.
         */
        val appliedDiscount: Discount?
            get() = (discountState as? CheckoutDiscountState.Applied)?.discount

        /**
         * Returns the shipping cost based on selected shipping method.
         */
        val shippingCost: Long
            get() = selectedShippingMethod?.price ?: 0L

        /**
         * Calculates the total amount including shipping and discount.
         */
        val totalAmount: Long
            get() {
                val subtotal = cart.summary.subtotal
                val tax = cart.summary.tax
                val discount = appliedDiscount?.let { calculateDiscountAmount(it, subtotal) } ?: 0L
                return subtotal + tax + shippingCost - discount
            }

        /**
         * Calculates the discount amount based on discount type.
         */
        private fun calculateDiscountAmount(discount: Discount, subtotal: Long): Long {
            return when (discount.type) {
                com.voxcina.shop.domain.model.DiscountType.PERCENTAGE -> {
                    (subtotal * discount.value / 100).coerceAtMost(subtotal)
                }
                com.voxcina.shop.domain.model.DiscountType.FIXED -> {
                    discount.value.toLong().coerceAtMost(subtotal)
                }
            }
        }

        /**
         * Validates if checkout can proceed.
         * Returns true if all required fields are valid.
         *
         * Requirements: 8.5, 8.6
         */
        val canProceedToPayment: Boolean
            get() {
                // Must have an address selected
                if (selectedAddress == null) return false
                
                // Must have a shipping method selected
                if (selectedShippingMethod == null) return false
                
                // If bank card is selected, card details must be valid
                if (selectedPaymentMethod == PaymentMethod.BANK_CARD && !cardDetails.isValid) {
                    return false
                }
                
                return true
            }

        /**
         * Returns validation error for a specific field.
         */
        fun getValidationError(field: String): String? = validationErrors[field]
    }

    /**
     * Error state when checkout data fails to load.
     *
     * Requirements: 9.2
     */
    data class Error(
        val message: String,
        val canRetry: Boolean = true
    ) : CheckoutUiState()
}

/**
 * Sealed class representing the state of discount code validation in checkout.
 *
 * Requirements: 7.2, 7.4, 7.5
 */
sealed class CheckoutDiscountState {

    /**
     * Initial state - no discount code entered or validated.
     */
    data object Idle : CheckoutDiscountState()

    /**
     * Loading state while validating discount code.
     *
     * Requirements: 7.5
     */
    data object Loading : CheckoutDiscountState()

    /**
     * Success state when discount code is valid and applied.
     *
     * Requirements: 7.3
     */
    data class Applied(val discount: Discount) : CheckoutDiscountState()

    /**
     * Error state when discount code validation fails.
     *
     * Requirements: 7.4
     */
    data class Error(val message: String) : CheckoutDiscountState()
}

/**
 * Validation field keys for checkout form.
 */
object CheckoutValidationFields {
    const val ADDRESS = "address"
    const val SHIPPING_METHOD = "shipping_method"
    const val CARD_NUMBER = "card_number"
    const val CARD_EXPIRY = "card_expiry"
    const val CARD_CVV = "card_cvv"
}
