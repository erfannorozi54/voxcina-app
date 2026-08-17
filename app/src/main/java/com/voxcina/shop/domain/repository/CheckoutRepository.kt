package com.voxcina.shop.domain.repository

import com.voxcina.shop.domain.model.CartItem
import com.voxcina.shop.domain.model.Order
import com.voxcina.shop.domain.model.UserAddress
import com.voxcina.shop.util.Result

/**
 * Repository interface for checkout operations.
 * Abstracts the data layer from the domain layer.
 */
interface CheckoutRepository {

    /**
     * Create a new order from cart items.
     *
     * @param items List of cart items to include in the order
     * @param totalAmount Total amount to charge, must equal
     *        `subtotal + shippingCost - discountAmount` (backend validates this)
     * @param shippingAddress Delivery address for the order
     * @param shippingCost Selected shipping method cost (must match what the
     *        user was shown; the backend validates the total against it)
     * @param discountAmount Applied discount amount (recomputed server-side,
     *        sent so the payload mirrors the web front-end)
     * @param promoCode Optional applied voucher code, re-validated by the backend
     * @return Result containing the created order
     */
    suspend fun createOrder(
        items: List<CartItem>,
        totalAmount: Long,
        shippingAddress: UserAddress,
        shippingCost: Long = 0,
        discountAmount: Long = 0,
        promoCode: String? = null
    ): Result<Order>
}
