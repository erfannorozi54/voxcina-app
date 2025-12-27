package com.voxcina.shop.domain.usecase

import com.voxcina.shop.data.repository.AuthRepository
import com.voxcina.shop.util.Result
import javax.inject.Inject

/**
 * Use case for checking if a phone number is already registered.
 * Determines whether to direct user to login or signup flow.
 *
 * Requirements: 2.1, 2.2, 2.3
 */
class CheckPhoneExistsUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    /**
     * Checks if the given phone number exists in the system.
     *
     * @param phone The phone number to check (format: 09xxxxxxxxx)
     * @return Result<Boolean> - true if phone exists (login flow), false otherwise (signup flow)
     */
    suspend operator fun invoke(phone: String): Result<Boolean> {
        return authRepository.checkPhoneExists(phone)
    }
}
