package com.voxcina.shop.domain.repository

import com.voxcina.shop.domain.model.PaymentResponse
import com.voxcina.shop.domain.model.VerifyPaymentResponse
import com.voxcina.shop.util.Result

/**
 * Repository interface for payment operations.
 */
interface PaymentRepository {

    /**
     * Request payment from Zibal gateway.
     */
    suspend fun requestPayment(
        orderId: String,
        amount: Long,
        description: String? = null,
        mobile: String? = null
    ): Result<PaymentResponse>

    /**
     * Verify payment after callback.
     */
    suspend fun verifyPayment(trackId: Long): Result<VerifyPaymentResponse>
}
