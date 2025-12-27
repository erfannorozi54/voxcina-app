package com.voxcina.shop.domain.usecase

import com.voxcina.shop.data.remote.dto.OtpResponse
import com.voxcina.shop.data.repository.AuthRepository
import com.voxcina.shop.util.Result
import javax.inject.Inject

/**
 * Enum representing different OTP flows in the app.
 */
enum class OtpFlow {
    LOGIN,
    SIGNUP,
    FORGOT_PASSWORD
}

/**
 * Use case for sending OTP codes.
 * Handles different OTP flows: login, signup, and forgot password.
 *
 * Requirements: 4.2, 5.2, 7.1
 */
class SendOtpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    /**
     * Sends an OTP code based on the specified flow.
     *
     * @param phone The user's phone number
     * @param flow The OTP flow type (LOGIN, SIGNUP, FORGOT_PASSWORD)
     * @param firstName Required for SIGNUP flow - user's first name
     * @param lastName Required for SIGNUP flow - user's last name
     * @return Result<OtpResponse> with OTP details on success
     */
    suspend operator fun invoke(
        phone: String,
        flow: OtpFlow,
        firstName: String? = null,
        lastName: String? = null
    ): Result<OtpResponse> {
        return when (flow) {
            OtpFlow.LOGIN -> {
                // Requirement 4.2: Send OTP for login
                authRepository.sendOtp(phone)
            }
            OtpFlow.SIGNUP -> {
                // Requirement 5.2: Send OTP for signup
                requireNotNull(firstName) { "firstName is required for SIGNUP flow" }
                requireNotNull(lastName) { "lastName is required for SIGNUP flow" }
                authRepository.sendSignupOtp(phone, firstName, lastName)
            }
            OtpFlow.FORGOT_PASSWORD -> {
                // Requirement 7.1: Send OTP for forgot password
                authRepository.sendForgotPasswordOtp(phone)
            }
        }
    }
}
