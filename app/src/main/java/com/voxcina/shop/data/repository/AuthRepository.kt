package com.voxcina.shop.data.repository

import com.voxcina.shop.data.remote.dto.AuthResponse
import com.voxcina.shop.data.remote.dto.OtpResponse
import com.voxcina.shop.data.remote.dto.OtpVerifyResponse
import com.voxcina.shop.util.Result

/**
 * Repository interface for authentication operations.
 * Abstracts the data layer from the domain layer.
 */
interface AuthRepository {

    /**
     * Check if a phone number is already registered.
     * @param phone The phone number to check (format: 09xxxxxxxxx)
     * @return Result<Boolean> - true if phone exists, false otherwise
     */
    suspend fun checkPhoneExists(phone: String): Result<Boolean>

    /**
     * Login with phone and password.
     * @param phone The user's phone number
     * @param password The user's password
     * @return Result<AuthResponse> containing user info and tokens
     */
    suspend fun login(phone: String, password: String): Result<AuthResponse>

    /**
     * Login via SMS after OTP verification.
     * @param phone The user's phone number
     * @return Result<AuthResponse> containing user info and tokens
     */
    suspend fun loginWithSms(phone: String): Result<AuthResponse>

    /**
     * Send OTP for login.
     * @param phone The user's phone number
     * @return Result<OtpResponse> with OTP details
     */
    suspend fun sendOtp(phone: String): Result<OtpResponse>

    /**
     * Verify OTP code for login.
     * @param phone The user's phone number
     * @param code The OTP code to verify
     * @return Result<OtpVerifyResponse> with validation result
     */
    suspend fun verifyOtp(phone: String, code: String): Result<OtpVerifyResponse>

    /**
     * Send OTP for signup.
     * @param phone The user's phone number
     * @param firstName The user's first name
     * @param lastName The user's last name
     * @return Result<OtpResponse> with OTP details
     */
    suspend fun sendSignupOtp(
        phone: String,
        firstName: String,
        lastName: String
    ): Result<OtpResponse>

    /**
     * Verify OTP and complete signup.
     * @param phone The user's phone number
     * @param code The OTP code
     * @param password The new password
     * @param confirmPassword Password confirmation
     * @return Result<AuthResponse> containing user info and tokens
     */
    suspend fun verifySignupOtp(
        phone: String,
        code: String,
        password: String,
        confirmPassword: String
    ): Result<AuthResponse>

    /**
     * Resend OTP during signup.
     * @param phone The user's phone number
     * @return Result<OtpResponse> with OTP details
     */
    suspend fun resendSignupOtp(phone: String): Result<OtpResponse>

    /**
     * Send OTP for forgot password flow.
     * @param phone The user's phone number
     * @return Result<OtpResponse> with OTP details
     */
    suspend fun sendForgotPasswordOtp(phone: String): Result<OtpResponse>

    /**
     * Reset password after OTP verification.
     * @param phone The user's phone number
     * @param code The OTP code
     * @param password The new password
     * @param confirmPassword Password confirmation
     * @return Result<Unit> on success
     */
    suspend fun resetPassword(
        phone: String,
        code: String,
        password: String,
        confirmPassword: String
    ): Result<Unit>
}
