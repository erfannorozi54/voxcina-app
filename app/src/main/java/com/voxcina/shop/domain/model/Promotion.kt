package com.voxcina.shop.domain.model

/**
 * Domain model for user promotion.
 */
data class Promotion(
    val id: String,
    val code: String,
    val type: String, // "percentage" or "fixed"
    val value: Double,
    val minOrderAmount: Double,
    val validFrom: String,
    val validTo: String,
    val maxUses: Int?,
    val usedCount: Int,
    val isPublic: Boolean
) {
    val isPercentage: Boolean get() = type == "percentage"
    
    fun getDisplayValue(): String {
        return if (isPercentage) {
            "${value.toInt()}%"
        } else {
            "${value.toLong()} تومان"
        }
    }
}
