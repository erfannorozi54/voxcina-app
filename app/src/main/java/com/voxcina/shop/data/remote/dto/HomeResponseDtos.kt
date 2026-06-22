package com.voxcina.shop.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Response DTO for hero images endpoint.
 * GET /api/hero-images
 */
data class HeroImagesResponse(
    @SerializedName("heroImages") val heroImages: List<HeroImageDto>
)

/**
 * DTO for a single hero image.
 */
data class HeroImageDto(
    @SerializedName("id") val id: String,
    @SerializedName("image") val image: String,
    @SerializedName("deviceType") val deviceType: String,
    @SerializedName("isActive") val isActive: Boolean,
    @SerializedName("gradient") val gradient: String?,
    @SerializedName("noGradient") val noGradient: Boolean,
    @SerializedName("displayOrder") val displayOrder: Int
)

/**
 * DTO for a category.
 * GET /api/categories
 */
data class CategoryDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("slug") val slug: String,
    @SerializedName("description") val description: String?,
    @SerializedName("image") val image: String?,
    @SerializedName("parent_id") val parentId: String?,
    @SerializedName("is_active") val isActive: Boolean,
    @SerializedName("show_in_header") val showInHeader: Boolean
)

/**
 * Response DTO for products list endpoint.
 * GET /api/products
 */
data class ProductListResponse(
    @SerializedName("data") val data: List<ColorVariantListItemDto>,
    @SerializedName("pagination") val pagination: PaginationDto
)

/**
 * DTO for a product color variant list item.
 * Each color of a product appears as a separate item in the list.
 */
data class ColorVariantListItemDto(
    @SerializedName("productId") val productId: String,
    @SerializedName("colorVariant") val colorVariant: ColorVariantDto,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("price") val price: Long,
    @SerializedName("originalPrice") val originalPrice: Long?,
    @SerializedName("brand") val brand: String,
    @SerializedName("brand_id") val brandId: String?,
    @SerializedName("category_ids") val categoryIds: List<String>?,
    @SerializedName("collection") val collection: String?,
    @SerializedName("is_flash_sale") val isFlashSale: Boolean,
    @SerializedName("average_rating") val averageRating: Float?,
    @SerializedName("review_count") val reviewCount: Int?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("inStock") val inStock: Boolean,
    @SerializedName("totalInventory") val totalInventory: Int
)

/**
 * DTO for a color variant with its images and sizes.
 */
data class ColorVariantDto(
    @SerializedName("color") val color: String,
    @SerializedName("colorName") val colorName: String,
    @SerializedName("swatchImage") val swatchImage: String?,
    @SerializedName("images") val images: List<String>,
    @SerializedName("tryOnImage") val tryOnImage: String?,
    @SerializedName("tryOnGarmentType") val tryOnGarmentType: String?,
    @SerializedName("sizes") val sizes: List<SizeVariantDto>
)

/**
 * DTO for a size variant with inventory info.
 */
data class SizeVariantDto(
    @SerializedName("size") val size: String,
    @SerializedName("sku") val sku: String,
    @SerializedName("quantity") val quantity: Int
)

/**
 * DTO for pagination info in list responses.
 */
data class PaginationDto(
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("currentPage") val currentPage: Int,
    @SerializedName("nextPage") val nextPage: Int?,
    @SerializedName("totalItems") val totalItems: Int
)

/**
 * DTO for a brand.
 * GET /api/brands
 */
data class BrandDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("slug") val slug: String,
    @SerializedName("logo") val logo: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("productsCount") val productsCount: Int,
    @SerializedName("featuredProduct") val featuredProduct: String?
)
