package com.voxcina.shop.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Response DTO for discount validation endpoint.
 * GET /api/discounts/code/{code}
 *
 * Mirrors the backend models.Discount JSON tags (snake_case).
 */
data class DiscountResponse(
    @SerializedName("id") val id: String = "",
    @SerializedName("code") val code: String = "",
    @SerializedName("type") val type: String = "",
    @SerializedName("value") val value: Int = 0,
    @SerializedName("min_order_amount") val minOrderAmount: Long = 0,
    @SerializedName("valid_from") val validFrom: String = "",
    @SerializedName("valid_to") val validTo: String = "",
    @SerializedName("max_uses") val maxUses: Int = 0,
    @SerializedName("used_count") val usedCount: Int = 0,
    @SerializedName("description") val description: String? = null
)

/**
 * Request DTO for a single cart item used to validate negotiated coupons.
 * POST /api/coupons/apply
 */
data class CouponCartItemRequest(
    @SerializedName("product_id") val productId: String,
    @SerializedName("color") val color: String? = null,
    @SerializedName("color_name") val colorName: String? = null
)

/**
 * Request DTO for negotiated coupon validation against the current cart.
 * POST /api/coupons/apply
 */
data class NegotiatedCouponRequest(
    @SerializedName("code") val code: String,
    @SerializedName("cart_items") val cartItems: List<CouponCartItemRequest>
)

/**
 * Response DTO for a required product/color of a negotiated coupon.
 */
data class RequiredColorResponse(
    @SerializedName("product_id") val productId: String = "",
    @SerializedName("color") val color: String? = null,
    @SerializedName("color_name") val colorName: String? = null
)

/**
 * Discount payload returned by POST /api/coupons/apply.
 */
data class NegotiatedDiscountResponse(
    @SerializedName("code") val code: String = "",
    @SerializedName("type") val type: String = "",
    @SerializedName("value") val value: Int = 0,
    @SerializedName("discountPercentage") val discountPercentage: Double = 0.0,
    @SerializedName("min_order_amount") val minOrderAmount: Long = 0,
    @SerializedName("valid_to") val validTo: String = "",
    @SerializedName("description") val description: String? = null,
    @SerializedName("product_ids") val productIds: List<String>? = null,
    @SerializedName("required_products") val requiredProducts: List<RequiredColorResponse>? = null,
    @SerializedName("source") val source: String? = null
)

/**
 * Response DTO for negotiated coupon validation.
 */
data class NegotiatedCouponResponse(
    @SerializedName("valid") val valid: Boolean = false,
    @SerializedName("discount") val discount: NegotiatedDiscountResponse? = null
)

/**
 * Request DTO for activating/deactivating a voucher usage counter.
 * POST /api/discounts/activate | POST /api/discounts/deactivate
 */
data class DiscountCodeRequest(
    @SerializedName("code") val code: String
)
