package com.voxcina.shop.domain.validator

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Validator for password strength and matching.
 * Validates minimum 8 characters with uppercase, lowercase, and digit requirements.
 *
 * Requirements: 5.5, 5.6
 */
@Singleton
class PasswordValidator @Inject constructor() {

    companion object {
        private const val MIN_LENGTH = 8
        private const val WEAK_PASSWORD_MESSAGE = "رمز عبور باید حداقل ۸ کاراکتر با حروف بزرگ، کوچک و عدد باشد"
        private const val PASSWORD_MISMATCH_MESSAGE = "رمز عبور و تکرار آن یکسان نیستند"
    }

    /**
     * Validates password strength.
     * Password must have:
     * - At least 8 characters
     * - At least one uppercase letter
     * - At least one lowercase letter
     * - At least one digit
     *
     * @param password The password to validate
     * @return ValidationResult indicating whether the password meets strength requirements
     */
    fun validate(password: String): ValidationResult {
        val hasMinLength = password.length >= MIN_LENGTH
        val hasUppercase = password.any { it.isUpperCase() }
        val hasLowercase = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }

        return if (hasMinLength && hasUppercase && hasLowercase && hasDigit) {
            ValidationResult.valid()
        } else {
            ValidationResult.invalid(WEAK_PASSWORD_MESSAGE)
        }
    }

    /**
     * Validates that two passwords match.
     *
     * @param password The original password
     * @param confirmPassword The confirmation password
     * @return ValidationResult indicating whether the passwords match
     */
    fun validateMatch(password: String, confirmPassword: String): ValidationResult {
        return if (password == confirmPassword) {
            ValidationResult.valid()
        } else {
            ValidationResult.invalid(PASSWORD_MISMATCH_MESSAGE)
        }
    }
}
