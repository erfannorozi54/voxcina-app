package com.voxcina.shop.domain.model

import com.voxcina.shop.data.remote.dto.ProductAttributeDto
import com.voxcina.shop.data.remote.dto.ProductDetailDto

/**
 * Domain model for full product details.
 */
data class ProductDetail(
    val id: String,
    val name: String,
    val description: String,
    val price: Long,
    val originalPrice: Long?,
    val mainImages: List<String>,
    val colorVariants: List<ColorVariant>,
    val brand: String,
    val brandId: String,
    val categoryIds: List<String>,
    val collection: String?,
    val attributes: List<ProductAttribute>,
    val isFlashSale: Boolean,
    val inStock: Boolean,
    val averageRating: Float?,
    val reviewCount: Int
)

/**
 * Domain model for product attribute (specifications).
 */
data class ProductAttribute(
    val name: String,
    val value: String
)

/**
 * Domain model for a product review.
 */
data class ProductReview(
    val id: String,
    val userId: String,
    val userName: String,
    val userAvatar: String?,
    val rating: Int,
    val comment: String,
    val isRecommended: Boolean = false,
    val createdAt: String
)

// ============ Mapper Extension Functions ============

/**
 * Maps ProductAttributeDto to domain ProductAttribute.
 */
fun ProductAttributeDto.toDomain(): ProductAttribute = ProductAttribute(
    name = name,
    value = value
)

/**
 * Maps ProductDetailDto to domain ProductDetail.
 */
fun ProductDetailDto.toDomain(): ProductDetail = ProductDetail(
    id = id,
    name = name,
    description = description,
    price = price,
    originalPrice = originalPrice,
    mainImages = mainImages,
    colorVariants = colorVariants.map { it.toDomain() },
    brand = brand,
    brandId = brandId,
    categoryIds = categoryIds,
    collection = collection,
    attributes = attributes.map { it.toDomain() },
    isFlashSale = isFlashSale,
    inStock = inStock,
    averageRating = averageRating,
    reviewCount = reviewCount
)
