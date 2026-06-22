package com.voxcina.shop.data.repository

import com.voxcina.shop.data.remote.PaymentApi
import com.voxcina.shop.data.remote.dto.*
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
 * Handles API calls to payment gateway endpoints.
 */
@Singleton
class PaymentRepositoryImpl @Inject constructor(
    private val paymentApi: PaymentApi
) : PaymentRepository {

    override suspend fun requestPayment(
        orderId: String,
        gateway: String
    ): Result<PaymentResponse> = safeApiCall {
        val request = PaymentRequestDto(
            orderId = orderId,
            gateway = gateway
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
                        payUrl = body.payUrl,
                        gateway = body.gateway
                    )
                )
            } else {
                Result.Error(AppError.ServerError(response.code(), body?.message ?: "Invalid response"))
            }
        } else {
            Result.Error(AppError.ServerError(response.code(), "Payment request failed"))
        }
    }

    override suspend fun verifyPayment(
        trackId: String,
        gateway: String
    ): Result<VerifyPaymentResponse> = safeApiCall {
        val request = VerifyPaymentDto(trackId = trackId, gateway = gateway)
        
        val response = paymentApi.verifyPayment(request)
        
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                Result.Success(
                    VerifyPaymentResponse(
                        success = body.success,
                        message = body.message,
                        amount = body.amount,
                        refNumber = body.refNumber,
                        canRetry = body.canRetry,
                        orderId = body.orderId,
                        paymentStatus = body.paymentStatus,
                        statusText = body.statusText
                    )
                )
            } else {
                Result.Error(AppError.ServerError(response.code(), "Empty response body"))
            }
        } else {
            Result.Error(AppError.ServerError(response.code(), "Payment verification failed"))
        }
    }

    override suspend fun retryPayment(
        orderId: String,
        gateway: String
    ): Result<PaymentResponse> = safeApiCall {
        val request = RetryPaymentRequestDto(orderId = orderId, gateway = gateway)
        
        val response = paymentApi.retryPayment(request)
        
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null && body.result == 100 && body.trackId != null && body.payUrl != null) {
                Result.Success(
                    PaymentResponse(
                        result = body.result,
                        message = body.message,
                        trackId = body.trackId,
                        payUrl = body.payUrl,
                        gateway = body.gateway
                    )
                )
            } else {
                Result.Error(AppError.ServerError(response.code(), body?.message ?: "Invalid response"))
            }
        } else {
            Result.Error(AppError.ServerError(response.code(), "Retry payment failed"))
        }
    }

    override suspend fun inquiryPayment(
        trackId: String,
        gateway: String
    ): Result<VerifyPaymentResponse> = safeApiCall {
        val request = PaymentInquiryDto(trackId = trackId, gateway = gateway)
        val response = paymentApi.inquiryPayment(request)
        
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                Result.Success(
                    VerifyPaymentResponse(
                        success = body.success,
                        message = body.message,
                        amount = body.amount,
                        refNumber = body.refNumber,
                        canRetry = body.canRetry,
                        orderId = body.orderId,
                        paymentStatus = body.paymentStatus,
                        statusText = body.statusText
                    )
                )
            } else {
                Result.Error(AppError.ServerError(response.code(), "Empty response body"))
            }
        } else {
            Result.Error(AppError.ServerError(response.code(), "Payment inquiry failed"))
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
