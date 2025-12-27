package com.voxcina.shop.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Request DTO for login with phone and password.
 * POST /api/users/login
 */
data class LoginRequest(
    @SerializedName("phone") val phone: String,
    @SerializedName("password") val password: String
)

/**
 * Request DTO for checking if phone exists.
 * POST /api/users/check-phone
 */
data class CheckPhoneRequest(
    @SerializedName("phone") val phone: String
)

/**
 * Request DTO for sending OTP for login.
 * POST /api/auth/send-otp
 */
data class SendOtpRequest(
    @SerializedName("phone") val phone: String
)

/**
 * Request DTO for sending OTP during signup.
 * POST /api/auth/signup/send-otp
 */
data class SignupSendOtpRequest(
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("phone") val phone: String
)

/**
 * Request DTO for verifying OTP during login.
 * POST /api/auth/check-otp
 */
data class VerifyOtpRequest(
    @SerializedName("phone") val phone: String,
    @SerializedName("code") val code: String
)

/**
 * Request DTO for verifying OTP and completing signup.
 * POST /api/auth/signup/verify-otp
 */
data class SignupVerifyOtpRequest(
    @SerializedName("phone") val phone: String,
    @SerializedName("code") val code: String,
    @SerializedName("password") val password: String,
    @SerializedName("confirmPassword") val confirmPassword: String
)

/**
 * Request DTO for resetting password after OTP verification.
 * POST /api/auth/forgot-password/reset
 */
data class ResetPasswordRequest(
    @SerializedName("phone") val phone: String,
    @SerializedName("code") val code: String,
    @SerializedName("password") val password: String,
    @SerializedName("confirmPassword") val confirmPassword: String
)

/**
 * Request DTO for resending OTP during signup.
 * POST /api/auth/signup/resend-otp
 */
data class ResendOtpRequest(
    @SerializedName("phone") val phone: String
)

/**
 * Request DTO for login via SMS (after OTP verification).
 * POST /api/users/login-sms
 */
data class LoginSmsRequest(
    @SerializedName("phone") val phone: String
)

/**
 * Request DTO for refreshing access token.
 * POST /api/users/refresh
 */
data class RefreshTokenRequest(
    @SerializedName("refreshToken") val refreshToken: String
)
