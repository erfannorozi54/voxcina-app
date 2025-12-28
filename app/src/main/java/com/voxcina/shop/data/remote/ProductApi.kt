package com.voxcina.shop.data.remote

import com.voxcina.shop.data.remote.dto.ProductDetailDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Retrofit API interface for product detail endpoints.
 * All endpoints follow the Voxcina backend API specification.
 */
interface ProductApi {

    /**
     * Get full product details by ID.
     * GET /api/products/{id}
     *
     * @param id Product ObjectID
     * @return Response containing full product details with all color variants
     */
    @GET("products/{id}")
    suspend fun getProductDetail(
        @Path("id") id: String
    ): Response<ProductDetailDto>
}
