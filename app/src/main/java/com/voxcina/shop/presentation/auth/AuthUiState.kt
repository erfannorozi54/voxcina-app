package com.voxcina.shop.presentation.auth

import com.voxcina.shop.domain.usecase.OtpFlow

/**
 * Sealed class representing all possible UI states for the authentication screen.
 * Each state contains the necessary fields for its corresponding screen.
 *
 * Requirements: 1.1, 3.1, 4.4, 5.1, 5.4
 */
sealed class AuthUiState {
    
    /**
     * Initial state for phone number entry.
     * Requirements: 1.1, 1.4, 1.5
     */
    data class PhoneEntry(
        val phone: String = "",
        val phoneError: String? = null,
        val isLoading: Boolean = false
    ) : AuthUiState()
    
    /**
     * Login state for existing users with password authentication.
     * Requirements: 3.1, 3.5, 3.6
     */
    data class Login(
        val phone: String,
        val password: String = "",
        val passwordVisible: Boolean = false,
        val error: String? = null,
        val isLoading: Boolean = false
    ) : AuthUiState()
    
    /**
     * Signup state for new users to enter their name.
     * Requirements: 5.1
     */
    data class Signup(
        val phone: String,
        val firstName: String = "",
        val lastName: String = "",
        val error: String? = null,
        val isLoading: Boolean = false
    ) : AuthUiState()
    
    /**
     * OTP verification state for all flows (login, signup, forgot password).
     * Requirements: 4.4, 6.4, 6.5, 6.6
     */
    data class OtpVerification(
        val phone: String,
        val otp: String = "",
        val flow: OtpFlow,
        val error: String? = null,
        val isLoading: Boolean = false,
        val resendCountdown: Int = 0,
        val firstName: String? = null,
        val lastName: String? = null
    ) : AuthUiState()
    
    /**
     * Password creation state for signup and password reset flows.
     * Requirements: 5.4, 5.5, 5.6, 7.3
     */
    data class PasswordCreation(
        val phone: String,
        val otp: String,
        val flow: PasswordFlow,
        val password: String = "",
        val confirmPassword: String = "",
        val passwordVisible: Boolean = false,
        val passwordError: String? = null,
        val error: String? = null,
        val isLoading: Boolean = false,
        val firstName: String? = null,
        val lastName: String? = null
    ) : AuthUiState()
    
    /**
     * Success state after authentication completes.
     */
    data class Success(
        val message: String = "ورود موفقیت‌آمیز"
    ) : AuthUiState()
}

/**
 * Enum representing password creation flows.
 */
enum class PasswordFlow {
    SIGNUP,
    RESET
}
