package com.voxcina.shop.domain.model

import com.voxcina.shop.data.remote.dto.*

/**
 * Domain model for the shopping cart.
 */
data class Cart(
    val id: String,
    val userId: String,
    val items: List<CartItem>,
    val summary: CartSummary,
    val createdAt: String,
    val updatedAt: String
)

/**
 * Domain model for a single cart item.
 */
data class CartItem(
    val product: CartProduct,
    val variant: CartVariant,
    val quantity: Int
)

/**
 * Domain model for product info within a cart item.
 */
data class CartProduct(
    val id: String,
    val name: String,
    val price: Long,
    val originalPrice: Long?,
    val mainImages: List<String>,
    val colorVariants: List<CartColorVariant>,
    val brand: String,
    val inStock: Boolean
)

/**
 * Domain model for color variant within cart product.
 */
data class CartColorVariant(
    val color: String,
    val images: List<String>
)

/**
 * Domain model for variant info within a cart item.
 */
data class CartVariant(
    val size: String,
    val color: String,
    val colorName: String,
    val sku: String
)

/**
 * Domain model for cart summary with pricing breakdown.
 */
data class CartSummary(
    val subtotal: Long,
    val shipping: Long,
    val tax: Long,
    val discount: Long,
    val total: Long
)


/**
 * Domain model for a discount code.
 */
data class Discount(
    val id: String,
    val code: String,
    val type: DiscountType,
    val value: Int,
    val minOrderAmount: Long,
    val validFrom: String,
    val validTo: String,
    val maxUses: Int,
    val usedCount: Int
)

/**
 * Enum for discount types.
 */
enum class DiscountType {
    PERCENTAGE,
    FIXED
}

// ============ Mapper Extension Functions ============

/**
 * Maps CartResponse DTO to domain Cart.
 */
fun CartResponse.toDomain(): Cart = Cart(
    id = id,
    userId = userId,
    items = items?.map { it.toDomain() } ?: emptyList(),
    summary = summary.toDomain(),
    createdAt = createdAt,
    updatedAt = updatedAt
)

/**
 * Maps CartItemDto to domain CartItem.
 */
fun CartItemDto.toDomain(): CartItem = CartItem(
    product = product.toDomain(),
    variant = variant.toDomain(),
    quantity = quantity
)

/**
 * Maps CartProductDto to domain CartProduct.
 */
fun CartProductDto.toDomain(): CartProduct = CartProduct(
    id = id,
    name = name,
    price = price,
    originalPrice = originalPrice,
    mainImages = mainImages,
    colorVariants = colorVariants?.map { it.toDomain() } ?: emptyList(),
    brand = brand,
    inStock = inStock
)

/**
 * Maps CartColorVariantDto to domain CartColorVariant.
 */
fun CartColorVariantDto.toDomain(): CartColorVariant = CartColorVariant(
    color = color,
    images = images
)

/**
 * Maps CartVariantDto to domain CartVariant.
 */
fun CartVariantDto.toDomain(): CartVariant = CartVariant(
    size = size,
    color = color,
    colorName = colorName,
    sku = sku
)

/**
 * Maps CartSummaryDto to domain CartSummary.
 */
fun CartSummaryDto.toDomain(): CartSummary = CartSummary(
    subtotal = subtotal,
    shipping = shipping,
    tax = tax,
    discount = discount,
    total = total
)

/**
 * Maps DiscountResponse DTO to domain Discount.
 */
fun DiscountResponse.toDomain(): Discount = Discount(
    id = id,
    code = code,
    type = when (type.lowercase()) {
        "percentage" -> DiscountType.PERCENTAGE
        "fixed" -> DiscountType.FIXED
        else -> DiscountType.PERCENTAGE
    },
    value = value,
    minOrderAmount = minOrderAmount,
    validFrom = validFrom,
    validTo = validTo,
    maxUses = maxUses,
    usedCount = usedCount
)
