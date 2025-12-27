package com.voxcina.shop.domain.usecase

import com.voxcina.shop.data.local.TokenManager
import com.voxcina.shop.data.repository.AuthRepository
import com.voxcina.shop.domain.model.User
import com.voxcina.shop.util.Result
import javax.inject.Inject

/**
 * Use case for completing signup with OTP verification.
 * Verifies OTP and creates user account with password.
 * Stores tokens securely on successful registration.
 *
 * Requirements: 5.7, 5.8
 */
class SignupWithOtpUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager
) {
    /**
     * Completes signup by verifying OTP and setting password.
     *
     * @param phone The user's phone number
     * @param otp The OTP code received via SMS
     * @param password The new password
     * @param confirmPassword Password confirmation
     * @return Result<User> containing user info on success, or error on failure
     */
    suspend operator fun invoke(
        phone: String,
        otp: String,
        password: String,
        confirmPassword: String
    ): Result<User> {
        // Verify OTP and complete signup (Requirement 5.7)
        return when (val result = authRepository.verifySignupOtp(
            phone = phone,
            code = otp,
            password = password,
            confirmPassword = confirmPassword
        )) {
            is Result.Success -> {
                // Store tokens and user name securely
                tokenManager.saveTokens(
                    accessToken = result.data.token,
                    refreshToken = result.data.refreshToken
                )
                tokenManager.saveUserName(result.data.name)
                Result.Success(
                    User(
                        id = result.data.id,
                        name = result.data.name,
                        phone = result.data.phone
                    )
                )
            }
            is Result.Error -> result
        }
    }
}
