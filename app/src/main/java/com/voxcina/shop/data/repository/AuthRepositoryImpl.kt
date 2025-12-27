package com.voxcina.shop.data.repository

import com.google.gson.Gson
import com.voxcina.shop.data.remote.AuthApi
import com.voxcina.shop.data.remote.dto.*
import com.voxcina.shop.util.AppError
import com.voxcina.shop.util.AuthError
import com.voxcina.shop.util.Result
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of AuthRepository.
 * Handles API calls and maps responses/errors to domain types.
 */
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val gson: Gson
) : AuthRepository {

    override suspend fun checkPhoneExists(phone: String): Result<Boolean> {
        return safeApiCall {
            val response = authApi.checkPhone(CheckPhoneRequest(phone))
            if (response.isSuccessful) {
                Result.Success(response.body()?.exists ?: false)
            } else {
                // 404 means phone doesn't exist
                if (response.code() == 404) {
                    Result.Success(false)
                } else {
                    Result.Error(mapHttpError(response))
                }
            }
        }
    }

    override suspend fun login(phone: String, password: String): Result<AuthResponse> {
        return safeApiCall {
            val response = authApi.login(LoginRequest(phone, password))
            handleResponse(response)
        }
    }

    override suspend fun loginWithSms(phone: String): Result<AuthResponse> {
        return safeApiCall {
            val response = authApi.loginWithSms(LoginSmsRequest(phone))
            handleResponse(response)
        }
    }

    override suspend fun sendOtp(phone: String): Result<OtpResponse> {
        return safeApiCall {
            val response = authApi.sendOtp(SendOtpRequest(phone))
            handleResponse(response)
        }
    }

    override suspend fun verifyOtp(phone: String, code: String): Result<OtpVerifyResponse> {
        return safeApiCall {
            val response = authApi.verifyOtp(VerifyOtpRequest(phone, code))
            handleResponse(response)
        }
    }

    override suspend fun sendSignupOtp(
        phone: String,
        firstName: String,
        lastName: String
    ): Result<OtpResponse> {
        return safeApiCall {
            val response = authApi.sendSignupOtp(
                SignupSendOtpRequest(firstName, lastName, phone)
            )
            handleResponse(response)
        }
    }

    override suspend fun verifySignupOtp(
        phone: String,
        code: String,
        password: String,
        confirmPassword: String
    ): Result<AuthResponse> {
        return safeApiCall {
            val response = authApi.verifySignupOtp(
                SignupVerifyOtpRequest(phone, code, password, confirmPassword)
            )
            handleResponse(response)
        }
    }

    override suspend fun resendSignupOtp(phone: String): Result<OtpResponse> {
        return safeApiCall {
            val response = authApi.resendSignupOtp(ResendOtpRequest(phone))
            handleResponse(response)
        }
    }

    override suspend fun sendForgotPasswordOtp(phone: String): Result<OtpResponse> {
        return safeApiCall {
            val response = authApi.sendForgotPasswordOtp(SendOtpRequest(phone))
            handleResponse(response)
        }
    }

    override suspend fun resetPassword(
        phone: String,
        code: String,
        password: String,
        confirmPassword: String
    ): Result<Unit> {
        return safeApiCall {
            val response = authApi.resetPassword(
                ResetPasswordRequest(phone, code, password, confirmPassword)
            )
            if (response.isSuccessful) {
                Result.Success(Unit)
            } else {
                Result.Error(mapHttpError(response))
            }
        }
    }


    /**
     * Wraps API calls with error handling for network exceptions.
     */
    private inline fun <T> safeApiCall(apiCall: () -> Result<T>): Result<T> {
        return try {
            apiCall()
        } catch (e: IOException) {
            Result.Error(AppError.NetworkError())
        } catch (e: Exception) {
            Result.Error(AppError.UnknownError(e.message ?: "خطای ناشناخته"))
        }
    }

    /**
     * Handles successful responses or maps errors.
     */
    private fun <T> handleResponse(response: Response<T>): Result<T> {
        return if (response.isSuccessful) {
            response.body()?.let {
                Result.Success(it)
            } ?: Result.Error(AppError.UnknownError("پاسخ خالی از سرور"))
        } else {
            Result.Error(mapHttpError(response))
        }
    }

    /**
     * Maps HTTP error codes to domain-specific AuthError types.
     */
    private fun <T> mapHttpError(response: Response<T>): AppError {
        val errorBody = response.errorBody()?.string()
        val apiError = parseErrorBody(errorBody)
        
        return when (response.code()) {
            400 -> mapBadRequestError(apiError)
            401 -> AuthError.InvalidCredentials
            404 -> AuthError.PhoneNotFound
            409 -> AuthError.PhoneAlreadyExists
            429 -> {
                val retryAfter = apiError?.retryAfter ?: 120
                AuthError.RateLimited(retryAfter)
            }
            in 500..599 -> AppError.ServerError(
                code = response.code(),
                message = apiError?.message ?: "خطای سرور"
            )
            else -> AppError.UnknownError(
                apiError?.message ?: "خطای ناشناخته"
            )
        }
    }

    /**
     * Maps 400 Bad Request errors to specific error types based on message.
     */
    private fun mapBadRequestError(apiError: ApiErrorResponse?): AppError {
        val message = apiError?.message?.lowercase() ?: apiError?.error?.lowercase() ?: ""
        
        return when {
            message.contains("otp") && message.contains("invalid") -> AuthError.InvalidOtp
            message.contains("otp") && message.contains("expire") -> AuthError.OtpExpired
            message.contains("کد") && message.contains("اشتباه") -> AuthError.InvalidOtp
            message.contains("کد") && message.contains("منقضی") -> AuthError.OtpExpired
            message.contains("password") && message.contains("weak") -> AuthError.WeakPassword
            message.contains("password") && message.contains("match") -> AuthError.PasswordMismatch
            message.contains("رمز") && message.contains("ضعیف") -> AuthError.WeakPassword
            message.contains("رمز") && message.contains("یکسان") -> AuthError.PasswordMismatch
            message.contains("phone") && message.contains("invalid") -> AuthError.InvalidPhone
            message.contains("شماره") && message.contains("نامعتبر") -> AuthError.InvalidPhone
            else -> AppError.ServerError(400, apiError?.message ?: "درخواست نامعتبر")
        }
    }

    /**
     * Parses error response body to ApiErrorResponse.
     */
    private fun parseErrorBody(errorBody: String?): ApiErrorResponse? {
        if (errorBody.isNullOrBlank()) return null
        return try {
            gson.fromJson(errorBody, ApiErrorResponse::class.java)
        } catch (e: Exception) {
            null
        }
    }
}
