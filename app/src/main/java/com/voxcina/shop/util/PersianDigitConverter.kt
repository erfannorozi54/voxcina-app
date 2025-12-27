package com.voxcina.shop.util

/**
 * Utility object for converting between Latin and Persian digits
 * and formatting version strings in Persian format.
 */
object PersianDigitConverter {

    private val latinDigits = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
    private val persianDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')

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
}
