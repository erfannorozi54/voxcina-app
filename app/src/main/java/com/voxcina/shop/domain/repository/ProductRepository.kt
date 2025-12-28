package com.voxcina.shop.domain.repository

import com.voxcina.shop.domain.model.ProductDetail
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
}
