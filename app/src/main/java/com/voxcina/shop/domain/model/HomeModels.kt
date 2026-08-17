package com.voxcina.shop.domain.model

import androidx.compose.ui.graphics.Color
import com.voxcina.shop.data.remote.dto.*

/**
 * Domain model for a hero image displayed in the carousel.
 *
 * @param content Authored hero content (text, buttons, colors). Absent on
 *   legacy records — the renderer falls back to [defaultHeroContent].
 */
data class HeroImage(
    val id: String,
    val imageUrl: String,
    val gradient: String?,
    val noGradient: Boolean,
    val displayOrder: Int,
    val content: HeroContent? = null
)

// ============================================================================
// Hero content model
//
// Colors are stored as hex strings and resolved at render time so legacy and
// authored records always render. Tokens that describe sizes/placement are
// resolved through literal maps in the UI layer.
// ============================================================================

enum class HeroElementType { BADGE, HEADING, PARAGRAPH, BUTTON }

/** Text fill: a flat color, or a clipped gradient (the "وکسینا" treatment). */
data class HeroColorStyle(
    val mode: String,
    val color: String,
    val from: String,
    val via: String,
    val to: String,
    val direction: String,
    val opacity: Int
)

data class HeroBadgeStyle(
    val showDot: Boolean,
    val dotColor: String,
    val pulseDot: Boolean,
    val background: String,
    val backgroundOpacity: Int,
    val borderColor: String,
    val borderOpacity: Int,
    val blur: Boolean
)

data class HeroButtonStyle(
    val href: String,
    val variant: String,
    val from: String,
    val to: String,
    val backgroundOpacity: Int,
    val textColor: String,
    val borderColor: String,
    val borderOpacity: Int,
    val blur: Boolean,
    val rounded: String,
    val size: String,
    val icon: String,
    val iconPosition: String,
    val iconColor: String,
    val fullWidthMobile: Boolean
)

/** One inline-styled run of text within a heading/paragraph element. */
data class HeroTextSegment(
    val id: String,
    val text: String,
    val color: HeroColorStyle
)

data class HeroElement(
    val id: String,
    val type: HeroElementType,
    val text: String,
    val visible: Boolean,
    val size: String,
    val weight: String,
    val align: String,
    val spacing: String,
    val maxWidth: String,
    val animation: String,
    val color: HeroColorStyle,
    val headingLevel: String?,
    val badge: HeroBadgeStyle?,
    val button: HeroButtonStyle?,
    val segments: List<HeroTextSegment>?
)

/** Section background gradient behind the hero image. */
data class HeroBackgroundStyle(
    val from: String,
    val via: String,
    val to: String,
    val direction: String
)

/** Tinted gradient layered on top of the hero image. */
data class HeroOverlayStyle(
    val enabled: Boolean,
    val from: String,
    val via: String,
    val to: String,
    val direction: String,
    val opacity: Int
)

data class HeroContent(
    val enabled: Boolean,
    val elements: List<HeroElement>,
    val verticalPosition: String,
    val horizontalPosition: String,
    val textAlign: String,
    val maxWidth: String,
    val offsetX: Int,
    val offsetY: Int,
    val showDecorations: Boolean,
    val background: HeroBackgroundStyle,
    val overlay: HeroOverlayStyle,
    val imageOpacity: Int
)

/**
 * Domain model for a product category.
 */
data class Category(
    val id: String,
    val name: String,
    val slug: String,
    val imageUrl: String?,
    val avatarUrl: String?,
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
    val description: String?,
    val price: Long,
    val originalPrice: Long?,
    val brand: String,
    val brandId: String?,
    val categoryIds: List<String>?,
    val collection: String?,
    val isFlashSale: Boolean,
    val inStock: Boolean,
    val totalInventory: Int,
    val colorVariant: ColorVariant,
    val averageRating: Float?,
    val reviewCount: Int?,
    val createdAt: String?
)

/**
 * Domain model for a color variant of a product.
 */
data class ColorVariant(
    val variantId: String?,
    val color: String,
    val colorName: String,
    val swatchImage: String?,
    val images: List<String>,
    val tryOnImage: String?,
    val tryOnGarmentType: String?,
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
    displayOrder = displayOrder,
    content = content?.toDomain()
)

/**
 * Maps list of HeroImageDto to domain models.
 */
fun List<HeroImageDto>.toHeroImages(): List<HeroImage> = map { it.toDomain() }

// ============ Hero Content Mappers ============

private const val DEFAULT_MODE = "solid"
private const val DEFAULT_TEXT_COLOR = "#ffffff"
private const val DEFAULT_GRADIENT_FROM = "#22d3ee"
private const val DEFAULT_GRADIENT_VIA = "#60a5fa"
private const val DEFAULT_GRADIENT_TO = "#c084fc"
private const val DEFAULT_GRADIENT_DIRECTION = "to-r"

fun HeroColorStyleDto.toDomain(): HeroColorStyle = HeroColorStyle(
    mode = mode ?: DEFAULT_MODE,
    color = color ?: DEFAULT_TEXT_COLOR,
    from = from ?: DEFAULT_GRADIENT_FROM,
    via = via ?: DEFAULT_GRADIENT_VIA,
    to = to ?: DEFAULT_GRADIENT_TO,
    direction = direction ?: DEFAULT_GRADIENT_DIRECTION,
    opacity = opacity ?: 100
)

fun HeroBadgeStyleDto.toDomain(): HeroBadgeStyle = HeroBadgeStyle(
    showDot = showDot ?: true,
    dotColor = dotColor ?: "#4ade80",
    pulseDot = pulseDot ?: true,
    background = background ?: DEFAULT_TEXT_COLOR,
    backgroundOpacity = backgroundOpacity ?: 10,
    borderColor = borderColor ?: DEFAULT_TEXT_COLOR,
    borderOpacity = borderOpacity ?: 20,
    blur = blur ?: true
)

fun HeroButtonStyleDto.toDomain(): HeroButtonStyle = HeroButtonStyle(
    href = href ?: "/",
    variant = variant ?: "gradient",
    from = from ?: "#06b6d4",
    to = to ?: "#2563eb",
    backgroundOpacity = backgroundOpacity ?: 100,
    textColor = textColor ?: DEFAULT_TEXT_COLOR,
    borderColor = borderColor ?: DEFAULT_TEXT_COLOR,
    borderOpacity = borderOpacity ?: 30,
    blur = blur ?: false,
    rounded = rounded ?: "full",
    size = size ?: "lg",
    icon = icon ?: "none",
    iconPosition = iconPosition ?: "end",
    iconColor = iconColor ?: DEFAULT_TEXT_COLOR,
    fullWidthMobile = fullWidthMobile ?: true
)

fun HeroTextSegmentDto.toDomain(): HeroTextSegment = HeroTextSegment(
    id = id ?: "",
    text = text ?: "",
    color = color?.toDomain() ?: HeroColorStyleDto().toDomain()
)

fun HeroElementDto.toDomain(): HeroElement = HeroElement(
    id = id ?: "",
    type = parseElementType(type),
    text = text ?: "",
    visible = visible ?: true,
    size = size ?: "md",
    weight = weight ?: "normal",
    align = align ?: "inherit",
    spacing = spacing ?: "md",
    maxWidth = maxWidth ?: "auto",
    animation = animation ?: "none",
    color = color?.toDomain() ?: HeroColorStyleDto().toDomain(),
    headingLevel = headingLevel,
    badge = if (parseElementType(type) == HeroElementType.BADGE) badge?.toDomain() else null,
    button = if (parseElementType(type) == HeroElementType.BUTTON) button?.toDomain() else null,
    segments = segments
        ?.takeIf { parseElementType(type) == HeroElementType.HEADING || parseElementType(type) == HeroElementType.PARAGRAPH }
        ?.map { it.toDomain() }
        ?.takeIf { it.isNotEmpty() }
)

fun HeroBackgroundStyleDto.toDomain(): HeroBackgroundStyle = HeroBackgroundStyle(
    from = from ?: "#111827",
    via = via ?: "#1e3a8a",
    to = to ?: "#111827",
    direction = direction ?: "to-br"
)

fun HeroOverlayStyleDto.toDomain(): HeroOverlayStyle = HeroOverlayStyle(
    enabled = enabled ?: true,
    from = from ?: "#2563eb",
    via = via ?: "#9333ea",
    to = to ?: "#db2777",
    direction = direction ?: "to-br",
    opacity = opacity ?: 10
)

fun HeroContentDto.toDomain(): HeroContent = HeroContent(
    enabled = enabled ?: true,
    elements = elements?.map { it.toDomain() } ?: emptyList(),
    verticalPosition = verticalPosition ?: "center",
    horizontalPosition = horizontalPosition ?: "center",
    textAlign = textAlign ?: "center",
    maxWidth = maxWidth ?: "xl",
    offsetX = offsetX ?: 0,
    offsetY = offsetY ?: 0,
    showDecorations = showDecorations ?: true,
    background = background?.toDomain() ?: HeroBackgroundStyleDto().toDomain(),
    overlay = overlay?.toDomain() ?: HeroOverlayStyleDto().toDomain(),
    imageOpacity = imageOpacity ?: 30
)

private fun parseElementType(type: String?): HeroElementType = when (type) {
    "badge" -> HeroElementType.BADGE
    "heading" -> HeroElementType.HEADING
    "button" -> HeroElementType.BUTTON
    else -> HeroElementType.PARAGRAPH
}

// ============ Hero Content Defaults ============

/** The fallback content for hero records saved before content authoring existed. */
fun defaultHeroContent(): HeroContent = HeroContent(
    enabled = true,
    elements = emptyList(),
    verticalPosition = "center",
    horizontalPosition = "center",
    textAlign = "center",
    maxWidth = "xl",
    offsetX = 0,
    offsetY = 0,
    showDecorations = true,
    background = HeroBackgroundStyle(from = "#111827", via = "#1e3a8a", to = "#111827", direction = "to-br"),
    overlay = HeroOverlayStyle(enabled = true, from = "#2563eb", via = "#9333ea", to = "#db2777", direction = "to-br", opacity = 10),
    imageOpacity = 30
)

/** Fills in anything a stored record is missing so legacy documents always render. */
fun HeroContent.normalized(): HeroContent = HeroContent(
    enabled = enabled,
    elements = elements,
    verticalPosition = verticalPosition,
    horizontalPosition = horizontalPosition,
    textAlign = textAlign,
    maxWidth = maxWidth,
    offsetX = offsetX,
    offsetY = offsetY,
    showDecorations = showDecorations,
    background = HeroBackgroundStyle(
        from = background.from,
        via = background.via,
        to = background.to,
        direction = background.direction
    ),
    overlay = HeroOverlayStyle(
        enabled = overlay.enabled,
        from = overlay.from,
        via = overlay.via,
        to = overlay.to,
        direction = overlay.direction,
        opacity = overlay.opacity.coerceIn(0, 100)
    ),
    imageOpacity = imageOpacity.coerceIn(0, 100)
)

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
        avatarUrl = avatar,
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
    variantId = variantId,
    color = color,
    colorName = colorName,
    swatchImage = swatchImage,
    images = images,
    tryOnImage = tryOnImage,
    tryOnGarmentType = tryOnGarmentType,
    sizes = sizes.map { it.toDomain() }
)

/**
 * Maps ColorVariantListItemDto to domain Product.
 */
fun ColorVariantListItemDto.toDomain(): Product = Product(
    productId = productId,
    name = name,
    description = description,
    price = price,
    originalPrice = originalPrice,
    brand = brand,
    brandId = brandId,
    categoryIds = categoryIds,
    collection = collection,
    isFlashSale = isFlashSale,
    inStock = inStock,
    totalInventory = totalInventory,
    colorVariant = colorVariant.toDomain(),
    averageRating = averageRating,
    reviewCount = reviewCount,
    createdAt = createdAt
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
