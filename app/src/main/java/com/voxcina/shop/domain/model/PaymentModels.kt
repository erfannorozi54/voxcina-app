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
    val statusText: String,
    val canRetry: Boolean = false,
    val orderNumber: String? = null
) {
    /** Returns true if payment was abandoned (user pressed back) */
    val isAbandoned: Boolean get() = paymentStatus == "abandoned"
    
    /** Returns true if payment was cancelled by user */
    val isCancelled: Boolean get() = paymentStatus == "cancelled"
    
    /** Returns true if payment was successful */
    val isSuccess: Boolean get() = paymentStatus == "paid"
    
    /** Returns true if payment failed */
    val isFailed: Boolean get() = paymentStatus == "failed"
}
