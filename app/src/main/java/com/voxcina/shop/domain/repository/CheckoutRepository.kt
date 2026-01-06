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
     * @param totalAmount Total amount to charge (including shipping, tax, discounts)
     * @param shippingAddress Delivery address for the order
     * @return Result containing the created order
     */
    suspend fun createOrder(
        items: List<CartItem>,
        totalAmount: Long,
        shippingAddress: UserAddress
    ): Result<Order>
}
