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
    val variantId: String = "",
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
 * Domain model for a discount code / voucher.
 */
data class Discount(
    val id: String = "",
    val code: String = "",
    val type: DiscountType = DiscountType.PERCENTAGE,
    val value: Int = 0,
    val minOrderAmount: Long = 0,
    val validFrom: String = "",
    val validTo: String = "",
    val maxUses: Int = 0,
    val usedCount: Int = 0,
    val description: String? = null,
    val source: VoucherSource = VoucherSource.ADMIN,
    val productIds: List<String> = emptyList(),
    val requiredColors: List<RequiredColor> = emptyList()
)

/**
 * Enum for discount types.
 */
enum class DiscountType {
    PERCENTAGE,
    FIXED
}

/**
 * Enum describing where a voucher came from, which drives how it is
 * validated against the cart (mirrors the backend's coupon.source):
 *
 * - ADMIN: regular discount code; applies cart-wide, needs min order.
 * - NEGOTIATED: seller-negotiated coupon; requires ALL product ids
 *   (and negotiated colors) to remain in the cart.
 * - CART_RECOVERY: abandoned-cart coupon; survives as long as ANY one of
 *   the required color variants is still in the cart.
 */
enum class VoucherSource {
    ADMIN,
    NEGOTIATED,
    CART_RECOVERY
}

/**
 * A product (and optionally a specific color variant) required for a
 * negotiated / cart-recovery voucher to stay valid.
 */
data class RequiredColor(
    val productId: String,
    val color: String? = null,
    val colorName: String? = null
)

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
    variantId = variantId ?: "",
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
    type = if (type.equals("fixed", ignoreCase = true)) DiscountType.FIXED else DiscountType.PERCENTAGE,
    value = value,
    minOrderAmount = minOrderAmount,
    validFrom = validFrom,
    validTo = validTo,
    maxUses = maxUses,
    usedCount = usedCount,
    description = description,
    source = VoucherSource.ADMIN
)

/**
 * Maps NegotiatedDiscountResponse DTO to domain Discount.
 */
fun NegotiatedDiscountResponse.toDomain(): Discount = Discount(
    id = "",
    code = code,
    type = if (type.equals("fixed", ignoreCase = true)) DiscountType.FIXED else DiscountType.PERCENTAGE,
    value = if (discountPercentage > 0) discountPercentage.toInt() else value,
    minOrderAmount = minOrderAmount,
    validFrom = "",
    validTo = validTo,
    maxUses = 0,
    usedCount = 0,
    description = description ?: "کد تخفیف اختصاصی شما",
    source = when (source) {
        "cart_recovery" -> VoucherSource.CART_RECOVERY
        else -> VoucherSource.NEGOTIATED
    },
    productIds = productIds ?: emptyList(),
    requiredColors = requiredProducts?.map {
        RequiredColor(
            productId = it.productId,
            color = it.color,
            colorName = it.colorName
        )
    } ?: emptyList()
)

// ============ Voucher Survival Logic ============

/**
 * Returns the distinct trimmed lookup values of a (color, colorName) pair.
 * Mirrors the backend's variantLookupValues: an item's color matches a
 * required color if either the raw color or the display name overlaps.
 */
private fun variantLookupValues(color: String?, colorName: String?): List<String> {
    val values = mutableListOf<String>()
    val seen = mutableSetOf<String>()
    for (value in listOf(color, colorName)) {
        val cleaned = value?.trim().orEmpty()
        if (cleaned.isEmpty() || !seen.add(cleaned)) continue
        values.add(cleaned)
    }
    return values
}

/**
 * Reports whether two (color, colorName) pairs refer to the same color
 * variant, comparing both raw color and display-name values.
 */
fun colorsOverlap(
    color1: String?, colorName1: String?,
    color2: String?, colorName2: String?
): Boolean {
    val values1 = variantLookupValues(color1, colorName1)
    val values2 = variantLookupValues(color2, colorName2)
    if (values1.isEmpty() || values2.isEmpty()) return false
    return values1.any { values2.contains(it) }
}

/**
 * Predicts whether the applied [discount] stays valid after removing [item]
 * from this cart. Ported from the web front-end's willVoucherSurvive() and
 * aligned with the backend's per-source validation semantics:
 *
 * - NEGOTIATED: invalid only if the removed line was needed to satisfy a
 *   required (product, color) pair (or, for legacy coupons without colors,
 *   if the removed item's product is one of the coupon's product ids) AND
 *   no remaining line covers it — the backend requires ALL requirements to
 *   stay satisfiable (AND).
 * - CART_RECOVERY: invalid only if the removed item matched a required
 *   color AND no other cart line still matches any required color.
 * - ADMIN: invalid if the remaining subtotal drops below minOrderAmount.
 */
fun Cart.willVoucherSurviveRemovalOf(discount: Discount, item: CartItem): Boolean {
    when (discount.source) {
        VoucherSource.NEGOTIATED -> {
            if (discount.requiredColors.isNotEmpty()) {
                // AND semantics: every required (product, color) pair must
                // still be satisfiable by a remaining line. Removing a
                // variant that matches no requirement never invalidates.
                return discount.requiredColors.all { required ->
                    if (itemSatisfies(required, item)) {
                        items.any { other ->
                            (other.product.id != item.product.id || other.variant.sku != item.variant.sku) &&
                                itemSatisfies(required, other)
                        }
                    } else {
                        true
                    }
                }
            }
            if (discount.productIds.isNotEmpty()) {
                return !discount.productIds.contains(item.product.id)
            }
            return true
        }

        VoucherSource.CART_RECOVERY -> {
            if (discount.requiredColors.isEmpty()) return true

            val isRequiredMatch = discount.requiredColors.any { required ->
                itemSatisfies(required, item)
            }
            if (!isRequiredMatch) return true

            // The coupon survives as long as at least one OTHER cart line
            // still matches one of the required variants.
            return items.any { other ->
                (other.product.id != item.product.id || other.variant.sku != item.variant.sku) &&
                    discount.requiredColors.any { required -> itemSatisfies(required, other) }
            }
        }

        VoucherSource.ADMIN -> {
            val remainingSubtotal = summary.subtotal - item.product.price * item.quantity
            if (discount.minOrderAmount > 0 && remainingSubtotal < discount.minOrderAmount) {
                return false
            }
            return true
        }
    }
}

/**
 * Returns true if [item] satisfies a required (product, color) pair.
 */
private fun itemSatisfies(required: RequiredColor, item: CartItem): Boolean {
    if (required.productId != item.product.id) return false
    if (required.color.isNullOrBlank() && required.colorName.isNullOrBlank()) return true
    return colorsOverlap(
        required.color, required.colorName,
        item.variant.color, item.variant.colorName
    )
}

/**
 * Computes the discount amount [discount] grants on this cart, mirroring the
 * backend's calculateCheckoutDiscount and the web cart-store:
 *
 * - Admin codes discount the whole cart.
 * - Negotiated / cart-recovery coupons discount ONLY the items whose
 *   (product, color) matches one of the coupon's required colors (any
 *   quantity of that color), not the whole cart.
 *
 * The result is capped at the subtotal.
 */
fun Cart.discountAmountFor(discount: Discount): Long {
    val subtotal = summary.subtotal
    var base = subtotal

    if ((discount.source == VoucherSource.NEGOTIATED ||
            discount.source == VoucherSource.CART_RECOVERY) &&
        discount.requiredColors.isNotEmpty()
    ) {
        base = items.sumOf { item ->
            val matches = discount.requiredColors.any { required -> itemSatisfies(required, item) }
            if (matches) item.product.price * item.quantity else 0L
        }
    }

    val discountValue = when (discount.type) {
        DiscountType.PERCENTAGE -> base * discount.value / 100
        DiscountType.FIXED -> discount.value.toLong()
    }
    return discountValue.coerceIn(0L, subtotal)
}
