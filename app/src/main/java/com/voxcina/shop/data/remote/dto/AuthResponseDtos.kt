package com.voxcina.shop.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Response DTO for successful authentication (login/signup).
 * Contains user info and tokens.
 */
data class AuthResponse(
    @SerializedName("_id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("token") val token: String,
    @SerializedName("refreshToken") val refreshToken: String
)

/**
 * Response DTO for OTP send operations.
 * Returned when OTP is successfully sent.
 */
data class OtpResponse(
    @SerializedName("message") val message: String,
    @SerializedName("expiresIn") val expiresIn: Int,
    @SerializedName("phone") val phone: String
)

/**
 * Response DTO for OTP verification.
 * Indicates whether the OTP code is valid.
 */
data class OtpVerifyResponse(
    @SerializedName("message") val message: String,
    @SerializedName("valid") val valid: Boolean
)

/**
 * Response DTO for phone existence check.
 * Indicates whether the phone number is already registered.
 */
data class CheckPhoneResponse(
    @SerializedName("exists") val exists: Boolean
)

/**
 * Response DTO for password reset success.
 */
data class ResetPasswordResponse(
    @SerializedName("message") val message: String
)

/**
 * Response DTO for token refresh.
 */
data class RefreshTokenResponse(
    @SerializedName("accessToken") val accessToken: String
)

/**
 * Generic error response from the API.
 */
data class ApiErrorResponse(
    @SerializedName("message") val message: String?,
    @SerializedName("error") val error: String?,
    @SerializedName("retryAfter") val retryAfter: Int? = null
)
