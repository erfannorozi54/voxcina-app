package com.voxcina.shop.domain.model

import androidx.compose.ui.graphics.Color
import com.voxcina.shop.data.remote.dto.*

/**
 * Domain model for a hero image displayed in the carousel.
 */
data class HeroImage(
    val id: String,
    val imageUrl: String,
    val gradient: String?,
    val noGradient: Boolean,
    val displayOrder: Int
)

/**
 * Domain model for a product category.
 */
data class Category(
    val id: String,
    val name: String,
    val slug: String,
    val imageUrl: String?,
    val iconName: String?,
    val iconColor: Color,
    val parentId: String?,
    val isActive: Boolean
)

/**
 * Domain model for a product in the listing.
 */
data class Product(
    val productId: String,
    val name: String,
    val price: Long,
    val originalPrice: Long?,
    val brand: String,
    val inStock: Boolean,
    val totalInventory: Int,
    val colorVariant: ColorVariant,
    val averageRating: Float?,
    val reviewCount: Int?
)

/**
 * Domain model for a color variant of a product.
 */
data class ColorVariant(
    val color: String,
    val colorName: String,
    val images: List<String>,
    val tryOnImage: String?,
    val sizes: List<SizeVariant>
)

/**
 * Domain model for a size variant with inventory.
 */
data class SizeVariant(
    val size: String,
    val sku: String,
    val quantity: Int
)

/**
 * Domain model for a brand.
 */
data class Brand(
    val id: String,
    val name: String,
    val slug: String,
    val logoUrl: String?,
    val productsCount: Int
)

/**
 * Domain model for a recently viewed product.
 */
data class RecentlyViewedProduct(
    val productId: String,
    val name: String,
    val price: Long,
    val imageUrl: String,
    val colorHex: String,
    val viewedAt: Long
)

/**
 * Result wrapper for paginated product list.
 */
data class ProductListResult(
    val products: List<Product>,
    val totalPages: Int,
    val currentPage: Int,
    val nextPage: Int?,
    val totalItems: Int
)

// ============ Mapper Extension Functions ============

/**
 * Maps HeroImageDto to domain HeroImage.
 */
fun HeroImageDto.toDomain(): HeroImage = HeroImage(
    id = id,
    imageUrl = image,
    gradient = gradient,
    noGradient = noGradient,
    displayOrder = displayOrder
)

/**
 * Maps list of HeroImageDto to domain models.
 */
fun List<HeroImageDto>.toHeroImages(): List<HeroImage> = map { it.toDomain() }

/**
 * Maps CategoryDto to domain Category.
 * Generates a color based on category id for icon background.
 */
fun CategoryDto.toDomain(): Category {
    // Generate a consistent color based on category id hash
    val colorIndex = id.hashCode().let { kotlin.math.abs(it) % CATEGORY_COLORS.size }
    return Category(
        id = id,
        name = name,
        slug = slug,
        imageUrl = image,
        iconName = null, // Icon name can be mapped from backend if available
        iconColor = CATEGORY_COLORS[colorIndex],
        parentId = parentId,
        isActive = isActive
    )
}

/**
 * Maps list of CategoryDto to domain models.
 */
fun List<CategoryDto>.toCategories(): List<Category> = map { it.toDomain() }

/**
 * Maps SizeVariantDto to domain SizeVariant.
 */
fun SizeVariantDto.toDomain(): SizeVariant = SizeVariant(
    size = size,
    sku = sku,
    quantity = quantity
)

/**
 * Maps ColorVariantDto to domain ColorVariant.
 */
fun ColorVariantDto.toDomain(): ColorVariant = ColorVariant(
    color = color,
    colorName = colorName,
    images = images,
    tryOnImage = tryOnImage,
    sizes = sizes.map { it.toDomain() }
)

/**
 * Maps ColorVariantListItemDto to domain Product.
 */
fun ColorVariantListItemDto.toDomain(): Product = Product(
    productId = productId,
    name = name,
    price = price,
    originalPrice = originalPrice,
    brand = brand,
    inStock = inStock,
    totalInventory = totalInventory,
    colorVariant = colorVariant.toDomain(),
    averageRating = null, // Not available in list response
    reviewCount = null
)

/**
 * Maps ProductListResponse to domain ProductListResult.
 */
fun ProductListResponse.toDomain(): ProductListResult = ProductListResult(
    products = data.map { it.toDomain() },
    totalPages = pagination.totalPages,
    currentPage = pagination.currentPage,
    nextPage = pagination.nextPage,
    totalItems = pagination.totalItems
)

/**
 * Maps BrandDto to domain Brand.
 */
fun BrandDto.toDomain(): Brand = Brand(
    id = id,
    name = name,
    slug = slug,
    logoUrl = logo,
    productsCount = productsCount
)

/**
 * Maps list of BrandDto to domain models.
 */
fun List<BrandDto>.toBrands(): List<Brand> = map { it.toDomain() }

// Predefined colors for category icons
private val CATEGORY_COLORS = listOf(
    Color(0xFF1A3C69), // Primary
    Color(0xFF10B981), // Success/Green
    Color(0xFFF59E0B), // Warning/Amber
    Color(0xFFEF4444), // Destructive/Red
    Color(0xFF8B5CF6), // Purple
    Color(0xFF06B6D4), // Cyan
    Color(0xFFEC4899), // Pink
    Color(0xFF6366F1)  // Indigo
)
