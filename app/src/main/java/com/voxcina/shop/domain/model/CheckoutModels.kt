package com.voxcina.shop.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.ui.graphics.vector.ImageVector
import java.util.Calendar

/**
 * Domain model for a shipping method option.
 */
data class ShippingMethod(
    val id: String,
    val name: String,
    val price: Long,
    val estimatedDays: String,
    val description: String
)

/**
 * Enum representing available payment methods.
 */
enum class PaymentMethod(
    val displayName: String,
    val icon: ImageVector
) {
    BANK_CARD(
        displayName = "کارت بانکی",
        icon = Icons.Default.CreditCard
    ),
    WALLET(
        displayName = "کیف پول",
        icon = Icons.Default.AccountBalanceWallet
    ),
    CASH_ON_DELIVERY(
        displayName = "پرداخت در محل",
        icon = Icons.Default.LocalShipping
    )
}

/**
 * Domain model for bank card details with validation.
 */
data class CardDetails(
    val cardNumber: String = "",
    val expiryDate: String = "",
    val cvv: String = "",
    val saveCard: Boolean = false
) {
    /**
     * Returns the card number formatted with spaces every 4 digits.
     * Example: "1234567890123456" -> "1234 5678 9012 3456"
     */
    val formattedCardNumber: String
        get() = cardNumber.filter { it.isDigit() }
            .chunked(4)
            .joinToString(" ")

    /**
     * Validates if the card number contains exactly 16 digits.
     */
    val isCardNumberValid: Boolean
        get() = cardNumber.filter { it.isDigit() }.length == 16

    /**
     * Validates if the expiry date is in MM/YY format and not expired.
     */
    val isExpiryValid: Boolean
        get() {
            val digitsOnly = expiryDate.filter { it.isDigit() }
            if (digitsOnly.length != 4) return false
            
            val month = digitsOnly.substring(0, 2).toIntOrNull() ?: return false
            val year = digitsOnly.substring(2, 4).toIntOrNull() ?: return false
            
            // Month must be between 1 and 12
            if (month < 1 || month > 12) return false
            
            // Check if card is not expired
            val calendar = Calendar.getInstance()
            val currentYear = calendar.get(Calendar.YEAR) % 100 // Get last 2 digits
            val currentMonth = calendar.get(Calendar.MONTH) + 1 // Calendar months are 0-indexed
            
            return when {
                year > currentYear -> true
                year == currentYear && month >= currentMonth -> true
                else -> false
            }
        }

    /**
     * Validates if the CVV contains 3 or 4 digits.
     */
    val isCvvValid: Boolean
        get() {
            val digitsOnly = cvv.filter { it.isDigit() }
            return digitsOnly.length in 3..4
        }

    /**
     * Returns true if all card details are valid.
     */
    val isValid: Boolean
        get() = isCardNumberValid && isExpiryValid && isCvvValid

    companion object {
        /**
         * Formats a raw card number string with spaces every 4 digits.
         * Useful for formatting user input.
         */
        fun formatCardNumber(input: String): String {
            return input.filter { it.isDigit() }
                .take(16)
                .chunked(4)
                .joinToString(" ")
        }

        /**
         * Formats expiry date input to MM/YY format.
         */
        fun formatExpiryDate(input: String): String {
            val digits = input.filter { it.isDigit() }.take(4)
            return when {
                digits.length <= 2 -> digits
                else -> "${digits.substring(0, 2)}/${digits.substring(2)}"
            }
        }
    }
}
