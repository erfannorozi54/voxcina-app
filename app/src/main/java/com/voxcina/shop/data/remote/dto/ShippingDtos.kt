package com.voxcina.shop.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.voxcina.shop.domain.model.ShippingMethod

/**
 * Request DTO for getting shipping quotes.
 * POST /api/postex/shipping/quotes
 */
data class ShippingQuotesRequestDto(
    @SerializedName("toCityCode") val toCityCode: Int,
    @SerializedName("itemCount") val itemCount: Int,
    @SerializedName("totalValue") val totalValue: Long
)

/**
 * Response DTO for shipping quotes endpoint.
 */
data class ShippingQuotesResponseDto(
    @SerializedName("methods") val methods: List<ShippingMethodDto>
)

/**
 * DTO for a single shipping method.
 * Matches the Postex API response structure.
 */
data class ShippingMethodDto(
    @SerializedName("id") val id: String,
    @SerializedName("serviceName") val serviceName: String,
    @SerializedName("price") val price: Long,
    @SerializedName("slaDays") val slaDays: String,
    @SerializedName("courierName") val courierName: String,
    @SerializedName("courierLogo") val courierLogo: String? = null
)

/**
 * Maps ShippingMethodDto to ShippingMethod domain model.
 */
fun ShippingMethodDto.toDomain(): ShippingMethod {
    return ShippingMethod(
        id = id,
        name = serviceName,
        price = price,
        estimatedDays = slaDays,
        description = courierName,
        courierLogo = courierLogo
    )
}
