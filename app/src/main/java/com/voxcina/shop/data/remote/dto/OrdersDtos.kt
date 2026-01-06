package com.voxcina.shop.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Response DTO for orders list endpoint.
 * GET /api/orders
 */
data class OrdersListResponseDto(
    @SerializedName("has_orders") val hasOrders: Boolean,
    @SerializedName("orders_data") val ordersData: List<OrderResponseDto>,
    @SerializedName("message") val message: String? = null,
    @SerializedName("link_text") val linkText: String? = null,
    @SerializedName("link_url") val linkUrl: String? = null
)
