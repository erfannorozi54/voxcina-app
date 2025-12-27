package com.voxcina.shop.data.repository

import com.voxcina.shop.domain.model.*
import com.voxcina.shop.util.Result

/**
 * Repository interface for home screen data operations.
 * Abstracts the data layer from the domain layer.
 */
interface HomeRepository {

    /**
     * Get hero images for the carousel.
     * @return Result<List<HeroImage>> containing hero images sorted by display order
     */
    suspend fun getHeroImages(): Result<List<HeroImage>>

    /**
     * Get all active categories.
     * @return Result<List<Category>> containing categories
     */
    suspend fun getCategories(): Result<List<Category>>

    /**
     * Get products with pagination.
     * @param page Page number (default: 1)
     * @param limit Items per page (default: 20)
     * @return Result<ProductListResult> containing paginated products
     */
    suspend fun getProducts(page: Int = 1, limit: Int = 20): Result<ProductListResult>

    /**
     * Get flash sale products.
     * @return Result<List<Product>> containing flash sale products
     */
    suspend fun getFlashSaleProducts(): Result<List<Product>>

    /**
     * Get all brands.
     * @return Result<List<Brand>> containing brands
     */
    suspend fun getBrands(): Result<List<Brand>>
}
