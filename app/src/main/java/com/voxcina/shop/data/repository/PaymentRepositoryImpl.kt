package com.voxcina.shop.data.repository

import com.voxcina.shop.data.remote.PaymentApi
import com.voxcina.shop.data.remote.dto.PaymentRequestDto
import com.voxcina.shop.data.remote.dto.VerifyPaymentDto
import com.voxcina.shop.domain.model.PaymentResponse
import com.voxcina.shop.domain.model.VerifyPaymentResponse
import com.voxcina.shop.domain.repository.PaymentRepository
import com.voxcina.shop.util.AppError
import com.voxcina.shop.util.Result
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of PaymentRepository.
 * Handles API calls to Zibal payment endpoints.
 */
@Singleton
class PaymentRepositoryImpl @Inject constructor(
    private val paymentApi: PaymentApi
) : PaymentRepository {

    override suspend fun requestPayment(
        orderId: String,
        amount: Long,
        description: String?,
        mobile: String?
    ): Result<PaymentResponse> = safeApiCall {
        val request = PaymentRequestDto(
            orderId = orderId,
            amount = amount,
            description = description,
            mobile = mobile
        )
        
        val response = paymentApi.requestPayment(request)
        
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null && body.result == 100 && body.trackId != null && body.payUrl != null) {
                Result.Success(
                    PaymentResponse(
                        result = body.result,
                        message = body.message,
                        trackId = body.trackId,
                        payUrl = body.payUrl
                    )
                )
            } else {
                Result.Error(AppError.ServerError(response.code(), body?.message ?: "Invalid response"))
            }
        } else {
            Result.Error(AppError.ServerError(response.code(), "Payment request failed"))
        }
    }

    override suspend fun verifyPayment(trackId: Long): Result<VerifyPaymentResponse> = safeApiCall {
        val request = VerifyPaymentDto(trackId = trackId)
        
        val response = paymentApi.verifyPayment(request)
        
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null && body.result == 100) {
                Result.Success(
                    VerifyPaymentResponse(
                        result = body.result,
                        message = body.message,
                        status = body.status,
                        amount = body.amount,
                        refNumber = body.refNumber,
                        cardNumber = body.cardNumber,
                        paidAt = body.paidAt,
                        description = body.description,
                        orderId = body.orderId,
                        paymentStatus = body.paymentStatus,
                        statusText = body.statusText
                    )
                )
            } else {
                Result.Error(AppError.ServerError(response.code(), body?.message ?: "Verification failed"))
            }
        } else {
            Result.Error(AppError.ServerError(response.code(), "Payment verification failed"))
        }
    }

    private inline fun <T> safeApiCall(call: () -> Result<T>): Result<T> = try {
        call()
    } catch (e: IOException) {
        Result.Error(AppError.NetworkError())
    } catch (e: Exception) {
        Result.Error(AppError.UnknownError(e.message ?: "Unknown error"))
    }
}
