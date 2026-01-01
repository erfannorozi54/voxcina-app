package com.voxcina.shop.data.remote

import com.voxcina.shop.data.remote.dto.AddReviewRequest
import com.voxcina.shop.data.remote.dto.ProductDetailDto
import com.voxcina.shop.data.remote.dto.ReviewDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
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

    /**
     * Get reviews for a product.
     * GET /api/products/{productId}/reviews
     *
     * @param productId Product ObjectID
     * @return Response containing list of approved reviews
     */
    @GET("products/{productId}/reviews")
    suspend fun getProductReviews(
        @Path("productId") productId: String
    ): Response<List<ReviewDto>>

    /**
     * Add a review for a product (requires authentication).
     * POST /api/products/{productId}/reviews
     *
     * @param productId Product ObjectID
     * @param request Review data (rating, comment, isRecommended)
     * @return Response containing the created review
     */
    @POST("products/{productId}/reviews")
    suspend fun addReview(
        @Path("productId") productId: String,
        @Body request: AddReviewRequest
    ): Response<ReviewDto>
}
