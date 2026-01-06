package com.voxcina.shop.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Request DTO for payment request.
 * POST /api/payment/request
 */
data class PaymentRequestDto(
    @SerializedName("orderId") val orderId: String,
    @SerializedName("amount") val amount: Long,
    @SerializedName("description") val description: String? = null,
    @SerializedName("mobile") val mobile: String? = null
)

/**
 * Response DTO for payment request.
 */
data class PaymentResponseDto(
    @SerializedName("result") val result: Int,
    @SerializedName("message") val message: String,
    @SerializedName("trackId") val trackId: Long? = null,
    @SerializedName("payUrl") val payUrl: String? = null
)

/**
 * Request DTO for payment verification.
 * POST /api/payment/verify
 */
data class VerifyPaymentDto(
    @SerializedName("trackId") val trackId: Long
)
