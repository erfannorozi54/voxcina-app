package com.voxcina.shop.data.remote

import com.voxcina.shop.data.remote.dto.PaymentRequestDto
import com.voxcina.shop.data.remote.dto.PaymentResponseDto
import com.voxcina.shop.data.remote.dto.VerifyPaymentDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Retrofit API interface for Zibal payment endpoints.
 */
interface PaymentApi {

    /**
     * Request payment from Zibal gateway.
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
}

data class VerifyPaymentResponseDto(
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
