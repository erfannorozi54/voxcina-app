package com.voxcina.shop.domain.model

/**
 * Domain model representing an authenticated user.
 */
data class User(
    val id: String,
    val name: String,
    val phone: String
)
