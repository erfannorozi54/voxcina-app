package com.voxcina.shop.data.remote.dto

import com.google.gson.annotations.SerializedName

data class TrackActivityRequestDto(
    @SerializedName("activityType") val activityType: String,
    @SerializedName("productId") val productId: String? = null,
    @SerializedName("productName") val productName: String? = null,
    @SerializedName("metadata") val metadata: Map<String, Any>? = null
)

data class RecentlyViewedResponseDto(
    @SerializedName("success") val success: Boolean,
    @SerializedName("products") val products: List<RecentlyViewedProductDto>
)

data class RecentlyViewedProductDto(
    @SerializedName("productId") val productId: String,
    @SerializedName("productName") val productName: String,
    @SerializedName("productImage") val productImage: String,
    @SerializedName("price") val price: Long,
    @SerializedName("viewedAt") val viewedAt: String
)
