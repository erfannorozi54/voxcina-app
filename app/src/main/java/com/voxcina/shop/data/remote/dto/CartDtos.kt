package com.voxcina.shop.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Response DTO for cart endpoint.
 * GET /api/cart
 */
data class CartResponse(
    @SerializedName("id") val id: String,
    @SerializedName("userId") val userId: String,
    @SerializedName("items") val items: List<CartItemDto>?,
    @SerializedName("summary") val summary: CartSummaryDto,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String
)

/**
 * DTO for a single cart item.
 */
data class CartItemDto(
    @SerializedName("product") val product: CartProductDto,
    @SerializedName("variant") val variant: CartVariantDto,
    @SerializedName("quantity") val quantity: Int
)

/**
 * DTO for product info within a cart item.
 */
data class CartProductDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("price") val price: Long,
    @SerializedName("originalPrice") val originalPrice: Long?,
    @SerializedName("mainImages") val mainImages: List<String>,
    @SerializedName("colorVariants") val colorVariants: List<CartColorVariantDto>?,
    @SerializedName("brand") val brand: String,
    @SerializedName("inStock") val inStock: Boolean
)

/**
 * DTO for color variant within cart product.
 */
data class CartColorVariantDto(
    @SerializedName("color") val color: String,
    @SerializedName("images") val images: List<String>
)

/**
 * DTO for variant info within a cart item.
 */
data class CartVariantDto(
    @SerializedName("variantId") val variantId: String?,
    @SerializedName("size") val size: String,
    @SerializedName("color") val color: String,
    @SerializedName("colorName") val colorName: String,
    @SerializedName("sku") val sku: String
)

/**
 * DTO for cart summary with pricing breakdown.
 */
data class CartSummaryDto(
    @SerializedName("subtotal") val subtotal: Long,
    @SerializedName("shipping") val shipping: Long,
    @SerializedName("tax") val tax: Long,
    @SerializedName("discount") val discount: Long,
    @SerializedName("total") val total: Long
)

// Request DTOs

/**
 * Request DTO for adding an item to cart.
 * POST /api/cart/item
 */
data class AddCartItemRequest(
    @SerializedName("productId") val productId: String,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("variant") val variant: CartVariantRequest
)

/**
 * Request DTO for updating cart item quantity.
 * PUT /api/cart/item
 */
data class UpdateCartItemRequest(
    @SerializedName("productId") val productId: String,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("variant") val variant: CartVariantRequest
)

/**
 * Request DTO for variant selection in cart operations.
 * The backend validates the variant by its stable variantId (see
 * validateVariantStock), so it is mandatory for add/update requests.
 */
data class CartVariantRequest(
    @SerializedName("variantId") val variantId: String,
    @SerializedName("size") val size: String,
    @SerializedName("color") val color: String
)

/**
 * Request DTO for syncing cart on login.
 * POST /api/cart
 */
data class SyncCartRequest(
    @SerializedName("items") val items: List<SyncCartItemRequest>
)

/**
 * Request DTO for a single item in cart sync.
 */
data class SyncCartItemRequest(
    @SerializedName("productId") val productId: String,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("variant") val variant: CartVariantRequest
)
