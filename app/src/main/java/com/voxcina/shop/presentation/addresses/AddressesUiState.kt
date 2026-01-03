package com.voxcina.shop.presentation.addresses

import com.voxcina.shop.data.remote.dto.CityDto
import com.voxcina.shop.data.remote.dto.ProvinceDto
import com.voxcina.shop.domain.model.UserAddress

data class AddressesUiState(
    val isLoading: Boolean = true,
    val addresses: List<UserAddress> = emptyList(),
    val error: String? = null,
    val isDeleting: Int? = null,
    val showAddDialog: Boolean = false,
    val editingIndex: Int? = null,
    val isSaving: Boolean = false,
    val saveError: String? = null,
    // Locality data
    val provinces: List<ProvinceDto> = emptyList(),
    val cities: List<CityDto> = emptyList(),
    val loadingProvinces: Boolean = false,
    val loadingCities: Boolean = false
)

data class AddressFormState(
    val title: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val phoneNumber: String = "",
    val province: String = "",
    val provinceCode: Int = 0,
    val city: String = "",
    val cityCode: Int = 0,
    val address: String = "",
    val postalCode: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val isDefault: Boolean = false,
    val addressType: AddressType = AddressType.HOME
) {
    fun toUserAddress() = UserAddress(
        title = title.ifBlank { if (addressType == AddressType.HOME) "خانه" else "محل کار" },
        firstName = firstName.ifBlank { null },
        lastName = lastName.ifBlank { null },
        phoneNumber = phoneNumber.ifBlank { null },
        province = province.ifBlank { null },
        provinceCode = provinceCode,
        city = city,
        cityCode = cityCode,
        street = null,
        address = address.ifBlank { null },
        postalCode = postalCode,
        latitude = latitude,
        longitude = longitude,
        isDefault = isDefault
    )

    fun isValid(): Boolean = 
        firstName.isNotBlank() && 
        lastName.isNotBlank() && 
        phoneNumber.isNotBlank() &&
        province.isNotBlank() && 
        city.isNotBlank() && 
        address.isNotBlank() && 
        postalCode.isNotBlank() &&
        latitude != 0.0 && longitude != 0.0
}

enum class AddressType { HOME, WORK }

fun UserAddress.toFormState() = AddressFormState(
    title = title ?: "",
    firstName = firstName ?: "",
    lastName = lastName ?: "",
    phoneNumber = phoneNumber ?: "",
    province = province ?: "",
    provinceCode = provinceCode,
    city = city,
    cityCode = cityCode,
    address = address ?: "",
    postalCode = postalCode,
    latitude = latitude,
    longitude = longitude,
    isDefault = isDefault,
    addressType = when {
        title?.contains("کار") == true || title?.contains("دفتر") == true -> AddressType.WORK
        else -> AddressType.HOME
    }
)
