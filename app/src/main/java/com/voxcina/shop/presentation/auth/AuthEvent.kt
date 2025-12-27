package com.voxcina.shop.presentation.auth

/**
 * Sealed class representing all user interaction events in the authentication flow.
 * Events are processed by AuthViewModel to update the UI state.
 *
 * Requirements: All user interaction requirements
 */
sealed class AuthEvent {
    
    // ==================== Phone Entry Events ====================
    
    /**
     * User changed the phone number input.
     * Requirements: 1.2, 1.3
     */
    data class PhoneChanged(val phone: String) : AuthEvent()
    
    /**
     * User submitted the phone number to check existence.
     * Requirements: 2.1, 2.2, 2.3
     */
    data object SubmitPhone : AuthEvent()
    
    // ==================== Login Events ====================
    
    /**
     * User changed the password input.
     * Requirements: 3.1
     */
    data class PasswordChanged(val password: String) : AuthEvent()
    
    /**
     * User toggled password visibility.
     * Requirements: 3.1
     */
    data object TogglePasswordVisibility : AuthEvent()
    
    /**
     * User submitted login credentials.
     * Requirements: 3.2, 3.4
     */
    data object SubmitLogin : AuthEvent()
    
    /**
     * User requested to login via OTP instead of password.
     * Requirements: 4.1, 4.2
     */
    data object RequestOtpLogin : AuthEvent()
    
    /**
     * User clicked forgot password link.
     * Requirements: 3.6, 7.1
     */
    data object ForgotPassword : AuthEvent()
    
    // ==================== Signup Events ====================
    
    /**
     * User changed the first name input.
     * Requirements: 5.1
     */
    data class FirstNameChanged(val firstName: String) : AuthEvent()
    
    /**
     * User changed the last name input.
     * Requirements: 5.1
     */
    data class LastNameChanged(val lastName: String) : AuthEvent()
    
    /**
     * User submitted signup info to send OTP.
     * Requirements: 5.2
     */
    data object SubmitSignup : AuthEvent()
    
    // ==================== OTP Events ====================
    
    /**
     * User changed the OTP input.
     * Requirements: 4.4
     */
    data class OtpChanged(val otp: String) : AuthEvent()
    
    /**
     * User submitted the OTP for verification.
     * Requirements: 4.5, 5.7
     */
    data object SubmitOtp : AuthEvent()
    
    /**
     * User requested to resend OTP.
     * Requirements: 6.8
     */
    data object ResendOtp : AuthEvent()
    
    /**
     * OTP was auto-filled from SMS.
     * Requirements: 6.2, 6.3
     */
    data class OtpAutoFilled(val otp: String) : AuthEvent()
    
    // ==================== Password Creation Events ====================
    
    /**
     * User changed the new password input.
     * Requirements: 5.4, 7.3
     */
    data class NewPasswordChanged(val password: String) : AuthEvent()
    
    /**
     * User changed the confirm password input.
     * Requirements: 5.6
     */
    data class ConfirmPasswordChanged(val password: String) : AuthEvent()
    
    /**
     * User toggled new password visibility.
     * Requirements: 5.4
     */
    data object ToggleNewPasswordVisibility : AuthEvent()
    
    /**
     * User submitted the new password.
     * Requirements: 5.7, 7.4
     */
    data object SubmitNewPassword : AuthEvent()
    
    // ==================== Navigation Events ====================
    
    /**
     * User pressed back button.
     */
    data object NavigateBack : AuthEvent()
    
    /**
     * Clear current error message.
     * Requirements: 10.6
     */
    data object ClearError : AuthEvent()
}
