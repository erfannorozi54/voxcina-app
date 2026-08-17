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
    @SerializedName("displayOrder") val displayOrder: Int,
    @SerializedName("content") val content: HeroContentDto? = null
)

/**
 * Authored hero content (text elements, placement, colors).
 * Mirrors the `content` document the admin saves for each hero image.
 */
data class HeroContentDto(
    @SerializedName("enabled") val enabled: Boolean? = null,
    @SerializedName("elements") val elements: List<HeroElementDto>? = null,
    @SerializedName("verticalPosition") val verticalPosition: String? = null,
    @SerializedName("horizontalPosition") val horizontalPosition: String? = null,
    @SerializedName("textAlign") val textAlign: String? = null,
    @SerializedName("maxWidth") val maxWidth: String? = null,
    @SerializedName("offsetX") val offsetX: Int? = null,
    @SerializedName("offsetY") val offsetY: Int? = null,
    @SerializedName("showDecorations") val showDecorations: Boolean? = null,
    @SerializedName("background") val background: HeroBackgroundStyleDto? = null,
    @SerializedName("overlay") val overlay: HeroOverlayStyleDto? = null,
    @SerializedName("imageOpacity") val imageOpacity: Int? = null
)

data class HeroElementDto(
    @SerializedName("id") val id: String? = null,
    @SerializedName("type") val type: String? = null,
    @SerializedName("text") val text: String? = null,
    @SerializedName("visible") val visible: Boolean? = null,
    @SerializedName("size") val size: String? = null,
    @SerializedName("weight") val weight: String? = null,
    @SerializedName("align") val align: String? = null,
    @SerializedName("spacing") val spacing: String? = null,
    @SerializedName("maxWidth") val maxWidth: String? = null,
    @SerializedName("animation") val animation: String? = null,
    @SerializedName("color") val color: HeroColorStyleDto? = null,
    @SerializedName("headingLevel") val headingLevel: String? = null,
    @SerializedName("badge") val badge: HeroBadgeStyleDto? = null,
    @SerializedName("button") val button: HeroButtonStyleDto? = null,
    @SerializedName("segments") val segments: List<HeroTextSegmentDto>? = null
)

/** Text fill: a flat color, or a clipped gradient. */
data class HeroColorStyleDto(
    @SerializedName("mode") val mode: String? = null,
    @SerializedName("color") val color: String? = null,
    @SerializedName("from") val from: String? = null,
    @SerializedName("via") val via: String? = null,
    @SerializedName("to") val to: String? = null,
    @SerializedName("direction") val direction: String? = null,
    @SerializedName("opacity") val opacity: Int? = null
)

data class HeroBadgeStyleDto(
    @SerializedName("showDot") val showDot: Boolean? = null,
    @SerializedName("dotColor") val dotColor: String? = null,
    @SerializedName("pulseDot") val pulseDot: Boolean? = null,
    @SerializedName("background") val background: String? = null,
    @SerializedName("backgroundOpacity") val backgroundOpacity: Int? = null,
    @SerializedName("borderColor") val borderColor: String? = null,
    @SerializedName("borderOpacity") val borderOpacity: Int? = null,
    @SerializedName("blur") val blur: Boolean? = null
)

data class HeroButtonStyleDto(
    @SerializedName("href") val href: String? = null,
    @SerializedName("variant") val variant: String? = null,
    @SerializedName("from") val from: String? = null,
    @SerializedName("to") val to: String? = null,
    @SerializedName("backgroundOpacity") val backgroundOpacity: Int? = null,
    @SerializedName("textColor") val textColor: String? = null,
    @SerializedName("borderColor") val borderColor: String? = null,
    @SerializedName("borderOpacity") val borderOpacity: Int? = null,
    @SerializedName("blur") val blur: Boolean? = null,
    @SerializedName("rounded") val rounded: String? = null,
    @SerializedName("size") val size: String? = null,
    @SerializedName("icon") val icon: String? = null,
    @SerializedName("iconPosition") val iconPosition: String? = null,
    @SerializedName("iconColor") val iconColor: String? = null,
    @SerializedName("fullWidthMobile") val fullWidthMobile: Boolean? = null
)

/** One inline-styled run of text within a heading/paragraph element. */
data class HeroTextSegmentDto(
    @SerializedName("id") val id: String? = null,
    @SerializedName("text") val text: String? = null,
    @SerializedName("color") val color: HeroColorStyleDto? = null
)

/** Section background gradient behind the hero image. */
data class HeroBackgroundStyleDto(
    @SerializedName("from") val from: String? = null,
    @SerializedName("via") val via: String? = null,
    @SerializedName("to") val to: String? = null,
    @SerializedName("direction") val direction: String? = null
)

/** Tinted gradient layered on top of the hero image. */
data class HeroOverlayStyleDto(
    @SerializedName("enabled") val enabled: Boolean? = null,
    @SerializedName("from") val from: String? = null,
    @SerializedName("via") val via: String? = null,
    @SerializedName("to") val to: String? = null,
    @SerializedName("direction") val direction: String? = null,
    @SerializedName("opacity") val opacity: Int? = null
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
    @SerializedName("avatar") val avatar: String?,
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
    @SerializedName("variantId") val variantId: String?,
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
