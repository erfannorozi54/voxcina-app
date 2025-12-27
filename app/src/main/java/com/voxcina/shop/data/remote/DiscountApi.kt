package com.voxcina.shop.data.remote

import com.voxcina.shop.data.remote.dto.DiscountResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Retrofit API interface for discount endpoints.
 */
interface DiscountApi {

    /**
     * Validate and get discount details by code.
     * GET /api/discounts/code/{code}
     *
     * @param code Discount code to validate
     * @return Response containing discount details if valid
     */
    @GET("discounts/code/{code}")
    suspend fun validateDiscount(
        @Path("code") code: String
    ): Response<DiscountResponse>
}
