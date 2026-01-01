package com.voxcina.shop.domain.repository

import com.voxcina.shop.domain.model.ProductDetail
import com.voxcina.shop.domain.model.ProductReview
import com.voxcina.shop.util.Result

/**
 * Repository interface for product detail operations.
 * Abstracts the data layer from the domain layer.
 */
interface ProductRepository {

    /**
     * Get full product details by ID.
     * @param productId Product ObjectID
     * @return Result<ProductDetail> containing full product details with all color variants
     *         Returns ProductError.ProductNotFound if product doesn't exist
     *         Returns ProductError.InvalidProductId if ID format is invalid
     *         Returns ProductError.ProductLoadFailed for other errors
     */
    suspend fun getProductDetail(productId: String): Result<ProductDetail>

    /**
     * Get reviews for a product.
     * @param productId Product ObjectID
     * @return Result<List<ProductReview>> containing approved reviews sorted by newest first
     */
    suspend fun getProductReviews(productId: String): Result<List<ProductReview>>

    /**
     * Add a review for a product (requires authentication).
     * @param productId Product ObjectID
     * @param rating Rating 1-5
     * @param comment Review comment
     * @param isRecommended Whether user recommends the product
     * @return Result<ProductReview> containing the created review
     */
    suspend fun addReview(
        productId: String,
        rating: Int,
        comment: String,
        isRecommended: Boolean
    ): Result<ProductReview>
}
