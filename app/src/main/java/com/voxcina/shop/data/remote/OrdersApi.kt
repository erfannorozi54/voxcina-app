package com.voxcina.shop.data.remote

import com.voxcina.shop.data.remote.dto.OrderResponseDto
import com.voxcina.shop.data.remote.dto.OrdersListResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit API interface for orders-related endpoints.
 */
interface OrdersApi {

    /**
     * Get user orders with pagination.
     * GET /api/orders
     *
     * @param page Page number (default: 1)
     * @param limit Items per page (default: 10)
     * @return Paginated list of orders
     */
    @GET("orders")
    suspend fun getOrders(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10
    ): Response<OrdersListResponseDto>

    /**
     * Get single order by ID.
     * GET /api/orders/{orderId}
     *
     * @param orderId Order ObjectId
     * @return Order details
     */
    @GET("orders/{orderId}")
    suspend fun getOrderById(
        @Path("orderId") orderId: String
    ): Response<OrderResponseDto>
}
