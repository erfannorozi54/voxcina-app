package com.voxcina.shop.domain.model

import com.voxcina.shop.data.remote.dto.AddressDto
import com.voxcina.shop.data.remote.dto.OrderCountsDto
import com.voxcina.shop.data.remote.dto.ProfileResponseDto

/**
 * Domain model representing a user's profile.
 */
data class UserProfile(
    val id: String,
    val name: String,
    val phone: String,
    val email: String?,
    val avatarUrl: String? = null,
    val hasAddresses: Boolean = false,
    val addresses: List<UserAddress> = emptyList()
)

/**
 * Domain model representing a user's address.
 */
data class UserAddress(
    val street: String?,
    val address: String?,
    val city: String,
    val postalCode: String,
    val latitude: Double,
    val longitude: Double,
    val isDefault: Boolean
)

/**
 * Domain model for order counts by status.
 */
data class OrderCounts(
    val pending: Int = 0,
    val processing: Int = 0,
    val shipped: Int = 0,
    val returned: Int = 0
) {
    val total: Int get() = pending + processing + shipped + returned
    
    companion object {
        val EMPTY = OrderCounts()
    }
}

// ============ Mapping Functions ============

/**
 * Maps ProfileResponseDto to UserProfile domain model.
 */
fun ProfileResponseDto.toDomain(): UserProfile {
    return UserProfile(
        id = userData.id,
        name = userData.name,
        phone = userData.phone,
        email = userData.email,
        avatarUrl = null, // Avatar URL not provided by current API
        hasAddresses = hasAddresses,
        addresses = addressesData?.map { it.toDomain() } ?: emptyList()
    )
}

/**
 * Maps AddressDto to UserAddress domain model.
 */
fun AddressDto.toDomain(): UserAddress {
    return UserAddress(
        street = street,
        address = address,
        city = city,
        postalCode = postalCode,
        latitude = latitude,
        longitude = longitude,
        isDefault = isDefault
    )
}

/**
 * Maps OrderCountsDto to OrderCounts domain model.
 */
fun OrderCountsDto.toDomain(): OrderCounts {
    return OrderCounts(
        pending = pending,
        processing = processing,
        shipped = shipped,
        returned = returned
    )
}
