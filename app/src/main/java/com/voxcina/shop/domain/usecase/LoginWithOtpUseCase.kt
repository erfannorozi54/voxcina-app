package com.voxcina.shop.domain.usecase

import com.voxcina.shop.data.local.TokenManager
import com.voxcina.shop.data.repository.AuthRepository
import com.voxcina.shop.domain.model.User
import com.voxcina.shop.util.Result
import javax.inject.Inject

/**
 * Use case for logging in via OTP verification.
 * First verifies the OTP, then completes login via SMS.
 * Stores tokens securely on successful authentication.
 *
 * Requirements: 4.5, 4.6, 4.7
 */
class LoginWithOtpUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager
) {
    /**
     * Attempts to login using OTP verification.
     * Verifies the OTP code first, then completes login via SMS.
     *
     * @param phone The user's phone number
     * @param otp The OTP code received via SMS
     * @return Result<User> containing user info on success, or error on failure
     */
    suspend operator fun invoke(phone: String, otp: String): Result<User> {
        // First verify the OTP (Requirement 4.5)
        val verifyResult = authRepository.verifyOtp(phone, otp)
        
        return when (verifyResult) {
            is Result.Success -> {
                if (!verifyResult.data.valid) {
                    return Result.Error(
                        com.voxcina.shop.util.AuthError.InvalidOtp
                    )
                }
                
                // OTP verified, now complete login via SMS
                when (val loginResult = authRepository.loginWithSms(phone)) {
                    is Result.Success -> {
                        // Store tokens securely (Requirement 4.6)
                        tokenManager.saveTokens(
                            accessToken = loginResult.data.token,
                            refreshToken = loginResult.data.refreshToken
                        )
                        // Map to domain model
                        Result.Success(
                            User(
                                id = loginResult.data.id,
                                name = loginResult.data.name,
                                phone = loginResult.data.phone
                            )
                        )
                    }
                    is Result.Error -> loginResult
                }
            }
            is Result.Error -> verifyResult
        }
    }
}
