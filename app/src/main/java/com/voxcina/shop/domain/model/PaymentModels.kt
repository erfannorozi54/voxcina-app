package com.voxcina.shop.domain.model

/**
 * Domain model for payment request response.
 */
data class PaymentResponse(
    val result: Int,
    val message: String,
    val trackId: Long,
    val payUrl: String
)

/**
 * Domain model for payment verification response.
 */
data class VerifyPaymentResponse(
    val result: Int,
    val message: String,
    val status: Int,
    val amount: Long,
    val refNumber: String? = null,
    val cardNumber: String? = null,
    val paidAt: String? = null,
    val description: String? = null,
    val orderId: String? = null,
    val paymentStatus: String,
    val statusText: String
)
