package com.voxcina.shop.data.remote

import com.voxcina.shop.data.remote.dto.ShippingQuotesRequestDto
import com.voxcina.shop.data.remote.dto.ShippingQuotesResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Retrofit API interface for shipping-related endpoints.
 * These endpoints are handled by NextJS and proxy to Postex API.
 */
interface ShippingApi {

    /**
     * Get shipping quotes for a destination city.
     * POST /api/postex/shipping/quotes
     *
     * @param request Contains toCityCode, itemCount, and totalValue
     * @return List of available shipping methods with prices
     */
    @POST("postex/shipping/quotes")
    suspend fun getShippingQuotes(
        @Body request: ShippingQuotesRequestDto
    ): Response<ShippingQuotesResponseDto>
}
