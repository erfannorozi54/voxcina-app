package com.voxcina.shop.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Response DTO for user profile endpoint.
 * GET /api/users/profile
 */
data class ProfileResponseDto(
    @SerializedName("user_data") val userData: UserDataDto,
    @SerializedName("has_addresses") val hasAddresses: Boolean,
    @SerializedName("addresses_data") val addressesData: List<AddressDto>?
)

/**
 * DTO for user data within profile response.
 */
data class UserDataDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String?,
    @SerializedName("phone") val phone: String,
    @SerializedName("role") val role: String,
    @SerializedName("is_active") val isActive: Boolean,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String
)

/**
 * DTO for user address.
 */
data class AddressDto(
    @SerializedName("street") val street: String?,
    @SerializedName("address") val address: String?,
    @SerializedName("city") val city: String,
    @SerializedName("postal_code") val postalCode: String,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("is_default") val isDefault: Boolean
)

/**
 * DTO for order counts by status.
 */
data class OrderCountsDto(
    @SerializedName("pending") val pending: Int = 0,
    @SerializedName("processing") val processing: Int = 0,
    @SerializedName("shipped") val shipped: Int = 0,
    @SerializedName("returned") val returned: Int = 0
)

/**
 * Request DTO for adding/updating address.
 */
data class AddressRequestDto(
    @SerializedName("street") val street: String?,
    @SerializedName("address") val address: String?,
    @SerializedName("city") val city: String,
    @SerializedName("postal_code") val postalCode: String,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("is_default") val isDefault: Boolean
)
