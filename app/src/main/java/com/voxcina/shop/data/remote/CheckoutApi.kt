package com.voxcina.shop.data.remote

import com.voxcina.shop.data.remote.dto.CreateOrderRequestDto
import com.voxcina.shop.data.remote.dto.OrderResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Retrofit API interface for checkout-related endpoints.
 */
interface CheckoutApi {

    /**
     * Create a new order (checkout).
     * POST /api/checkout
     *
     * @param request Contains items, totalAmount, and shippingAddress
     * @return Created order details
     */
    @POST("checkout")
    suspend fun createOrder(
        @Body request: CreateOrderRequestDto
    ): Response<OrderResponseDto>
}
