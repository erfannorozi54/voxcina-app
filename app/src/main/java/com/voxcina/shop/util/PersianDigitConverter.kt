package com.voxcina.shop.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

/**
 * Utility object for converting between Latin and Persian digits
 * and formatting version strings and prices in Persian format.
 */
object PersianDigitConverter {

    private val latinDigits = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
    private val persianDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
    
    // Persian thousand separator
    private const val PERSIAN_THOUSAND_SEPARATOR = '٬'

    /**
     * Converts all Latin digits (0-9) in a string to Persian digits (۰-۹).
     *
     * @param input The string containing Latin digits to convert
     * @return A new string with all Latin digits replaced by Persian digits
     */
    fun toPersianDigits(input: String): String {
        val builder = StringBuilder(input.length)
        for (char in input) {
            val index = latinDigits.indexOf(char)
            if (index >= 0) {
                builder.append(persianDigits[index])
            } else {
                builder.append(char)
            }
        }
        return builder.toString()
    }

    /**
     * Alias for toPersianDigits for consistency with other naming conventions.
     */
    fun convertToPersian(input: String): String = toPersianDigits(input)

    /**
     * Converts all Persian digits (۰-۹) in a string to Latin digits (0-9).
     *
     * @param input The string containing Persian digits to convert
     * @return A new string with all Persian digits replaced by Latin digits
     */
    fun convertPersianToLatin(input: String): String {
        val builder = StringBuilder(input.length)
        for (char in input) {
            val index = persianDigits.indexOf(char)
            if (index >= 0) {
                builder.append(latinDigits[index])
            } else {
                builder.append(char)
            }
        }
        return builder.toString()
    }

    /**
     * Formats a version string with Persian digits and the "نسخه " prefix.
     *
     * @param version The version string in format "X.Y.Z" (e.g., "2.4.0")
     * @return Formatted version string with Persian prefix and digits (e.g., "نسخه ۲.۴.۰")
     */
    fun formatVersionPersian(version: String): String {
        val persianVersion = toPersianDigits(version)
        return "نسخه $persianVersion"
    }

    /**
     * Formats a price with thousand separators using Persian thousand separator (٬).
     *
     * @param price The price value as Long
     * @return Formatted price string with Persian digits and thousand separators
     */
    fun formatPrice(price: Long): String {
        if (price < 0) return toPersianDigits("0")
        
        val symbols = DecimalFormatSymbols(Locale.US).apply {
            groupingSeparator = PERSIAN_THOUSAND_SEPARATOR
        }
        val formatter = DecimalFormat("#,###", symbols)
        val formatted = formatter.format(price)
        return toPersianDigits(formatted)
    }

    /**
     * Formats a price with thousand separators and adds "تومان" suffix.
     *
     * @param price The price value as Long
     * @return Formatted price string with Persian digits, thousand separators, and "تومان" suffix
     */
    fun formatPriceWithSuffix(price: Long): String {
        return "${formatPrice(price)} تومان"
    }
}
