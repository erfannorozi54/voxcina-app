package com.voxcina.shop.util

/**
 * Utility object for calculating discount percentages.
 */
object DiscountCalculator {

    /**
     * Calculates the discount percentage between original and current price.
     *
     * @param originalPrice The original price before discount
     * @param currentPrice The current discounted price
     * @return The discount percentage as an integer (1-99), or null if no valid discount
     */
    fun calculateDiscountPercentage(originalPrice: Long, currentPrice: Long): Int? {
        // Handle invalid cases
        if (originalPrice <= 0 || currentPrice <= 0) return null
        if (currentPrice >= originalPrice) return null
        
        val discount = ((originalPrice - currentPrice) * 100) / originalPrice
        
        // Ensure discount is within valid range (1-99)
        return when {
            discount < 1 -> null
            discount > 99 -> 99
            else -> discount.toInt()
        }
    }

    /**
     * Calculates the discount percentage and formats it as a Persian string.
     *
     * @param originalPrice The original price before discount
     * @param currentPrice The current discounted price
     * @return Formatted discount string (e.g., "۲۵٪"), or null if no valid discount
     */
    fun formatDiscountPercentage(originalPrice: Long, currentPrice: Long): String? {
        val percentage = calculateDiscountPercentage(originalPrice, currentPrice) ?: return null
        return "${PersianDigitConverter.toPersianDigits(percentage.toString())}٪"
    }
}
