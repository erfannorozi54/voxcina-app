package com.voxcina.shop.domain.usecase

import com.voxcina.shop.data.local.TokenManager
import com.voxcina.shop.data.repository.AuthRepository
import com.voxcina.shop.domain.model.User
import com.voxcina.shop.util.Result
import javax.inject.Inject

/**
 * Use case for logging in with phone number and password.
 * Stores tokens securely on successful authentication.
 *
 * Requirements: 3.2, 3.3, 3.4, 3.5
 */
class LoginWithPasswordUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager
) {
    /**
     * Attempts to login with the provided credentials.
     *
     * @param phone The user's phone number
     * @param password The user's password
     * @return Result<User> containing user info on success, or error on failure
     */
    suspend operator fun invoke(phone: String, password: String): Result<User> {
        return when (val result = authRepository.login(phone, password)) {
            is Result.Success -> {
                // Store tokens and user info securely
                tokenManager.saveTokens(
                    accessToken = result.data.token,
                    refreshToken = result.data.refreshToken
                )
                tokenManager.saveUserName(result.data.name)
                tokenManager.saveUserPhone(result.data.phone)
                result.data.id?.let { tokenManager.saveUserId(it) }
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
