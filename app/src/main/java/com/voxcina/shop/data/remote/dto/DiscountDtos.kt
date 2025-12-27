package com.voxcina.shop.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Response DTO for discount validation endpoint.
 * GET /api/discounts/code/{code}
 */
data class DiscountResponse(
    @SerializedName("id") val id: String,
    @SerializedName("code") val code: String,
    @SerializedName("type") val type: String,
    @SerializedName("value") val value: Int,
    @SerializedName("minOrderAmount") val minOrderAmount: Long,
    @SerializedName("validFrom") val validFrom: String,
    @SerializedName("validTo") val validTo: String,
    @SerializedName("maxUses") val maxUses: Int,
    @SerializedName("usedCount") val usedCount: Int
)
