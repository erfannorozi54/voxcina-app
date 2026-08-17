package com.voxcina.shop.presentation.cart

import com.voxcina.shop.domain.model.Discount

/**
 * In-memory hand-off of the applied voucher from the Cart screen to the
 * Checkout screen (both screens have their own ViewModel instances).
 *
 * The CartViewModel writes here whenever a voucher is applied or removed;
 * the CheckoutViewModel seeds its discount state from here on load so the
 * user does not have to re-apply (and re-activate) the code at checkout —
 * re-activating would double-count usage on the backend.
 */
object AppliedVoucherTransfer {

    @Volatile
    var discount: Discount? = null

    fun set(value: Discount?) {
        discount = value
    }

    fun clear() {
        discount = null
    }
}
