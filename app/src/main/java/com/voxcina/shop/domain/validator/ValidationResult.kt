package com.voxcina.shop.domain.validator

/**
 * Represents the result of a validation operation.
 *
 * @property isValid Whether the validation passed
 * @property errorMessage The error message if validation failed, null otherwise
 */
data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null
) {
    companion object {
        fun valid() = ValidationResult(isValid = true)
        fun invalid(message: String) = ValidationResult(isValid = false, errorMessage = message)
    }
}
