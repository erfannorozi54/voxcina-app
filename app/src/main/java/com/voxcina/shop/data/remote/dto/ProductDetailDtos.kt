package com.voxcina.shop.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO for full product details response.
 * GET /api/products/{id}
 */
data class ProductDetailDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("price") val price: Long,
    @SerializedName("originalPrice") val originalPrice: Long?,
    @SerializedName("mainImages") val mainImages: List<String>,
    @SerializedName("colorVariants") val colorVariants: List<ColorVariantDto>,
    @SerializedName("brand") val brand: String,
    @SerializedName("brand_id") val brandId: String,
    @SerializedName("category_ids") val categoryIds: List<String>,
    @SerializedName("collection") val collection: String?,
    @SerializedName("attributes") val attributes: List<ProductAttributeDto>,
    @SerializedName("is_flash_sale") val isFlashSale: Boolean,
    @SerializedName("is_active") val isActive: Boolean,
    @SerializedName("inStock") val inStock: Boolean,
    @SerializedName("average_rating") val averageRating: Float?,
    @SerializedName("review_count") val reviewCount: Int
)

/**
 * DTO for product attribute (specifications).
 */
data class ProductAttributeDto(
    @SerializedName("name") val name: String,
    @SerializedName("value") val value: String
)
