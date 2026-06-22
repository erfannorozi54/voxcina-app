package com.voxcina.shop.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Request DTO for payment request.
 * POST /api/payment/request
 */
data class PaymentRequestDto(
    @SerializedName("orderId") val orderId: String,
    @SerializedName("gateway") val gateway: String
)

/**
 * Response DTO for payment request.
 */
data class PaymentResponseDto(
    @SerializedName("result") val result: Int,
    @SerializedName("message") val message: String,
    @SerializedName("trackId") val trackId: String? = null,
    @SerializedName("payUrl") val payUrl: String? = null,
    @SerializedName("gateway") val gateway: String? = null
)

/**
 * Request DTO for payment verification.
 * POST /api/payment/verify
 */
data class VerifyPaymentDto(
    @SerializedName("trackId") val trackId: String,
    @SerializedName("gateway") val gateway: String
)

/**
 * Request DTO for payment inquiry.
 * POST /api/payment/inquiry
 */
data class PaymentInquiryDto(
    @SerializedName("trackId") val trackId: String,
    @SerializedName("gateway") val gateway: String
)

/**
 * Request DTO for payment retry.
 * POST /api/payment/retry
 */
data class RetryPaymentRequestDto(
    @SerializedName("orderId") val orderId: String,
    @SerializedName("gateway") val gateway: String
)
