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
    @SerializedName("title") val title: String?,
    @SerializedName("first_name") val firstName: String?,
    @SerializedName("last_name") val lastName: String?,
    @SerializedName("phone_number") val phoneNumber: String?,
    @SerializedName("province") val province: String?,
    @SerializedName("province_code") val provinceCode: Int?,
    @SerializedName("city") val city: String,
    @SerializedName("city_code") val cityCode: Int?,
    @SerializedName("street") val street: String?,
    @SerializedName("address") val address: String?,
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
    @SerializedName("title") val title: String?,
    @SerializedName("first_name") val firstName: String?,
    @SerializedName("last_name") val lastName: String?,
    @SerializedName("phone_number") val phoneNumber: String?,
    @SerializedName("province") val province: String?,
    @SerializedName("province_code") val provinceCode: Int?,
    @SerializedName("city") val city: String,
    @SerializedName("city_code") val cityCode: Int?,
    @SerializedName("street") val street: String?,
    @SerializedName("address") val address: String?,
    @SerializedName("postal_code") val postalCode: String,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("is_default") val isDefault: Boolean
)

/**
 * Request DTO for recording app activity.
 * POST /api/users/app-activity
 */
data class AppActivityRequestDto(
    @SerializedName("platform") val platform: String = "android",
    @SerializedName("app_version") val appVersion: String
)

/**
 * Response DTO for app activity endpoint.
 */
data class AppActivityResponseDto(
    @SerializedName("message") val message: String,
    @SerializedName("last_app_open") val lastAppOpen: String?
)


/**
 * DTO for promotion/discount.
 */
data class PromotionDto(
    @SerializedName("id") val id: String,
    @SerializedName("code") val code: String,
    @SerializedName("type") val type: String, // "percentage" or "fixed"
    @SerializedName("value") val value: Double,
    @SerializedName("min_order_amount") val minOrderAmount: Double,
    @SerializedName("valid_from") val validFrom: String,
    @SerializedName("valid_to") val validTo: String,
    @SerializedName("max_uses") val maxUses: Int?,
    @SerializedName("used_count") val usedCount: Int,
    @SerializedName("is_public") val isPublic: Boolean
)
