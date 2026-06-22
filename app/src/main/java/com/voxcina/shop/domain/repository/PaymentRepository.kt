package com.voxcina.shop.domain.repository

import com.voxcina.shop.domain.model.PaymentResponse
import com.voxcina.shop.domain.model.VerifyPaymentResponse
import com.voxcina.shop.util.Result

/**
 * Repository interface for payment operations.
 */
interface PaymentRepository {

    /**
     * Request payment from a gateway.
     */
    suspend fun requestPayment(
        orderId: String,
        gateway: String
    ): Result<PaymentResponse>

    /**
     * Verify payment after callback.
     */
    suspend fun verifyPayment(
        trackId: String,
        gateway: String
    ): Result<VerifyPaymentResponse>

    /**
     * Retry payment for a pending/failed order.
     */
    suspend fun retryPayment(
        orderId: String,
        gateway: String
    ): Result<PaymentResponse>

    /**
     * Inquiry about a payment status.
     */
    suspend fun inquiryPayment(
        trackId: String,
        gateway: String
    ): Result<VerifyPaymentResponse>
}
