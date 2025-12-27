package com.voxcina.shop.data.remote

import com.voxcina.shop.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Retrofit API interface for authentication endpoints.
 * All endpoints follow the Voxcina backend API specification.
 */
interface AuthApi {

    /**
     * Check if a phone number is already registered.
     * POST /api/users/check-phone
     * 
     * @return 200 with exists=true if phone exists, 404 if not found
     */
    @POST("users/check-phone")
    suspend fun checkPhone(
        @Body request: CheckPhoneRequest
    ): Response<CheckPhoneResponse>

    /**
     * Login with phone and password.
     * POST /api/users/login
     * 
     * @return 200 with user info and tokens on success
     * @throws 401 for invalid credentials
     */
    @POST("users/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>

    /**
     * Login via SMS after OTP verification.
     * POST /api/users/login-sms
     * 
     * @return 200 with user info and tokens on success
     * @throws 404 if user not found
     */
    @POST("users/login-sms")
    suspend fun loginWithSms(
        @Body request: LoginSmsRequest
    ): Response<AuthResponse>

    /**
     * Send OTP for login.
     * POST /api/auth/send-otp
     * 
     * @return 200 with OTP details on success
     * @throws 404 if user not found, 429 if rate limited
     */
    @POST("auth/send-otp")
    suspend fun sendOtp(
        @Body request: SendOtpRequest
    ): Response<OtpResponse>

    /**
     * Verify OTP code for login.
     * POST /api/auth/check-otp
     * 
     * @return 200 with validation result
     * @throws 429 if max attempts exceeded
     */
    @POST("auth/check-otp")
    suspend fun verifyOtp(
        @Body request: VerifyOtpRequest
    ): Response<OtpVerifyResponse>

    /**
     * Send OTP for signup.
     * POST /api/auth/signup/send-otp
     * 
     * @return 200 with OTP details on success
     * @throws 409 if phone already exists, 429 if rate limited
     */
    @POST("auth/signup/send-otp")
    suspend fun sendSignupOtp(
        @Body request: SignupSendOtpRequest
    ): Response<OtpResponse>

    /**
     * Verify OTP and complete signup.
     * POST /api/auth/signup/verify-otp
     * 
     * @return 201 with user info and tokens on success
     * @throws 400 for invalid OTP/password, 409 if phone exists, 429 if max attempts
     */
    @POST("auth/signup/verify-otp")
    suspend fun verifySignupOtp(
        @Body request: SignupVerifyOtpRequest
    ): Response<AuthResponse>

    /**
     * Resend OTP during signup.
     * POST /api/auth/signup/resend-otp
     * 
     * @return 200 on success
     * @throws 429 if rate limited
     */
    @POST("auth/signup/resend-otp")
    suspend fun resendSignupOtp(
        @Body request: ResendOtpRequest
    ): Response<OtpResponse>

    /**
     * Send OTP for forgot password flow.
     * POST /api/auth/forgot-password/send-otp
     * 
     * @return 200 with OTP details on success
     * @throws 404 if user not found, 429 if rate limited
     */
    @POST("auth/forgot-password/send-otp")
    suspend fun sendForgotPasswordOtp(
        @Body request: SendOtpRequest
    ): Response<OtpResponse>

    /**
     * Reset password after OTP verification.
     * POST /api/auth/forgot-password/reset
     * 
     * @return 200 on success
     * @throws 400 for invalid OTP/password, 404 if user not found, 429 if max attempts
     */
    @POST("auth/forgot-password/reset")
    suspend fun resetPassword(
        @Body request: ResetPasswordRequest
    ): Response<ResetPasswordResponse>

    /**
     * Refresh access token.
     * POST /api/users/refresh
     * 
     * @return 200 with new access token
     * @throws 401 if refresh token is invalid/expired
     */
    @POST("users/refresh")
    suspend fun refreshToken(
        @Body request: RefreshTokenRequest
    ): Response<RefreshTokenResponse>
}
