package com.voxcina.shop.domain.usecase

import com.voxcina.shop.domain.model.ProductDetail
import com.voxcina.shop.domain.repository.ProductRepository
import com.voxcina.shop.util.Result
import javax.inject.Inject

/**
 * Use case for fetching full product details by ID.
 * Abstracts the repository layer from the presentation layer.
 *
 * Requirements: 11.1, 11.2, 11.3
 */
class GetProductDetailUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    /**
     * Fetches product details for the given product ID.
     *
     * @param productId Product ObjectID
     * @return Result<ProductDetail> containing full product details with all color variants
     *         Returns ProductError.ProductNotFound if product doesn't exist
     *         Returns ProductError.InvalidProductId if ID format is invalid
     *         Returns ProductError.ProductLoadFailed for other errors
     */
    suspend operator fun invoke(productId: String): Result<ProductDetail> {
        return productRepository.getProductDetail(productId)
    }
}
