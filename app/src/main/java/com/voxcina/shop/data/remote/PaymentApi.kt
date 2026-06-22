package com.voxcina.shop.data.remote

import com.voxcina.shop.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Retrofit API interface for payment gateway integration.
 */
interface PaymentApi {

    /**
     * Request payment from a gateway (zibal | digipay).
     * POST /api/payment/request
     */
    @POST("payment/request")
    suspend fun requestPayment(
        @Body request: PaymentRequestDto
    ): Response<PaymentResponseDto>

    /**
     * Verify payment after callback.
     * POST /api/payment/verify
     */
    @POST("payment/verify")
    suspend fun verifyPayment(
        @Body request: VerifyPaymentDto
    ): Response<VerifyPaymentResponseDto>

    /**
     * Retry payment for a pending/failed order.
     * POST /api/payment/retry
     */
    @POST("payment/retry")
    suspend fun retryPayment(
        @Body request: RetryPaymentRequestDto
    ): Response<PaymentResponseDto>

    /**
     * Inquiry about a payment status.
     * POST /api/payment/inquiry
     */
    @POST("payment/inquiry")
    suspend fun inquiryPayment(
        @Body request: PaymentInquiryDto
    ): Response<VerifyPaymentResponseDto>
}

data class VerifyPaymentResponseDto(
    val result: Int,
    val message: String? = null,
    val success: Boolean,
    val refNumber: String? = null,
    val amount: Long,
    val canRetry: Boolean = false,
    val orderId: String? = null,
    val paymentStatus: String? = null,
    val statusText: String? = null
)
