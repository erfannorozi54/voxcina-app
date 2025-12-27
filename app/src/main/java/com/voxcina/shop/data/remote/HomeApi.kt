package com.voxcina.shop.data.remote

import com.voxcina.shop.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit API interface for home screen endpoints.
 * All endpoints follow the Voxcina backend API specification.
 */
interface HomeApi {

    /**
     * Get hero images for the carousel.
     * GET /api/hero-images
     *
     * @param device Device type filter ("mobile" or "desktop")
     * @return Response containing list of hero images
     */
    @GET("hero-images")
    suspend fun getHeroImages(
        @Query("device") device: String = "mobile"
    ): Response<HeroImagesResponse>

    /**
     * Get all categories.
     * GET /api/categories
     *
     * @return Response containing list of categories
     */
    @GET("categories")
    suspend fun getCategories(): Response<List<CategoryDto>>

    /**
     * Get products with pagination and filtering.
     * GET /api/products
     *
     * @param page Page number (default: 1)
     * @param limit Items per page (default: 20)
     * @param category Category ID or name filter
     * @param brandId Brand ObjectID filter
     * @param brand Brand name filter
     * @param search Text search on name/description
     * @param sort Sort order: "newest", "price-asc", "price-desc", "popular", "discount"
     * @param isFlashSale Filter flash sale items only
     * @param inStock Filter in-stock items only
     * @return Response containing paginated product list
     */
    @GET("products")
    suspend fun getProducts(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("category") category: String? = null,
        @Query("brandId") brandId: String? = null,
        @Query("brand") brand: String? = null,
        @Query("search") search: String? = null,
        @Query("sort") sort: String? = null,
        @Query("is_flash_sale") isFlashSale: Boolean? = null,
        @Query("in_stock") inStock: Boolean? = null
    ): Response<ProductListResponse>

    /**
     * Get all brands.
     * GET /api/brands
     *
     * @return Response containing list of brands
     */
    @GET("brands")
    suspend fun getBrands(): Response<List<BrandDto>>
}
