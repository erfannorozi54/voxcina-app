package com.voxcina.shop.domain.repository

import com.voxcina.shop.domain.model.Order
import com.voxcina.shop.util.Result

/**
 * Repository interface for orders operations.
 */
interface OrderRepository {

    /**
     * Get user orders with pagination.
     *
     * @param page Page number
     * @param limit Items per page
     * @return Result containing list of orders and hasOrders flag
     */
    suspend fun getOrders(page: Int = 1, limit: Int = 10): Result<OrdersResult>

    /**
     * Get single order by ID.
     *
     * @param orderId Order ID
     * @return Result containing the order
     */
    suspend fun getOrderById(orderId: String): Result<Order>
}

/**
 * Result wrapper for orders list.
 */
data class OrdersResult(
    val hasOrders: Boolean,
    val orders: List<Order>
)
