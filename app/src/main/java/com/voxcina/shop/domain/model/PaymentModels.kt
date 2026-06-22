package com.voxcina.shop.domain.model

/**
 * Domain model for payment request response.
 */
data class PaymentResponse(
    val result: Int,
    val message: String,
    val trackId: String,
    val payUrl: String,
    val gateway: String? = null
)

/**
 * Domain model for payment verification response.
 */
data class VerifyPaymentResponse(
    val success: Boolean,
    val message: String? = null,
    val amount: Long,
    val refNumber: String? = null,
    val canRetry: Boolean = false,
    val orderId: String? = null,
    val paymentStatus: String? = null,
    val statusText: String? = null
) {
    /** Returns true if payment was successful */
    val isSuccess: Boolean get() = success || paymentStatus == "paid"
    
    /** Returns true if payment was abandoned (user pressed back) */
    val isAbandoned: Boolean get() = paymentStatus == "abandoned"
    
    /** Returns true if payment was cancelled by user */
    val isCancelled: Boolean get() = paymentStatus == "cancelled"
    
    /** Returns true if payment failed */
    val isFailed: Boolean get() = paymentStatus == "failed"
}
