package com.voxcina.shop.domain.usecase

import com.voxcina.shop.data.repository.AuthRepository
import com.voxcina.shop.util.Result
import javax.inject.Inject

/**
 * Use case for resetting password after OTP verification.
 * Used in the forgot password flow.
 *
 * Requirements: 7.4, 7.5
 */
class ResetPasswordUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    /**
     * Resets the user's password after OTP verification.
     *
     * @param phone The user's phone number
     * @param otp The OTP code received via SMS
     * @param password The new password
     * @param confirmPassword Password confirmation
     * @return Result<Unit> on success, or error on failure
     */
    suspend operator fun invoke(
        phone: String,
        otp: String,
        password: String,
        confirmPassword: String
    ): Result<Unit> {
        // Reset password (Requirement 7.4)
        return authRepository.resetPassword(
            phone = phone,
            code = otp,
            password = password,
            confirmPassword = confirmPassword
        )
    }
}
