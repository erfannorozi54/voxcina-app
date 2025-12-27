package com.voxcina.shop.domain.validator

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Validator for Iranian phone numbers.
 * Handles Persian digit conversion and validates the 09xxxxxxxxx format.
 *
 * Requirements: 1.2, 1.3, 1.4, 1.5
 */
@Singleton
class PhoneValidator @Inject constructor() {

    companion object {
        private val PERSIAN_DIGITS = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
        private val LATIN_DIGITS = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
        private val PHONE_PATTERN = Regex("^09[0-9]{9}$")
        private const val INVALID_PHONE_MESSAGE = "شماره تلفن نامعتبر است"
    }

    /**
     * Validates an Iranian phone number.
     * The phone number must match the pattern 09xxxxxxxxx (11 digits starting with 09).
     * Persian digits are automatically converted to Latin digits before validation.
     *
     * @param phone The phone number to validate (may contain Persian or Latin digits)
     * @return ValidationResult indicating whether the phone is valid
     */
    fun validate(phone: String): ValidationResult {
        val normalizedPhone = normalizePhone(phone)
        return if (PHONE_PATTERN.matches(normalizedPhone)) {
            ValidationResult.valid()
        } else {
            ValidationResult.invalid(INVALID_PHONE_MESSAGE)
        }
    }

    /**
     * Normalizes a phone number by converting Persian digits (۰-۹) to Latin digits (0-9).
     * Non-digit characters are preserved.
     *
     * @param phone The phone number to normalize
     * @return The phone number with all Persian digits converted to Latin digits
     */
    fun normalizePhone(phone: String): String {
        val builder = StringBuilder(phone.length)
        for (char in phone) {
            val persianIndex = PERSIAN_DIGITS.indexOf(char)
            if (persianIndex >= 0) {
                builder.append(LATIN_DIGITS[persianIndex])
            } else {
                builder.append(char)
            }
        }
        return builder.toString()
    }
}
