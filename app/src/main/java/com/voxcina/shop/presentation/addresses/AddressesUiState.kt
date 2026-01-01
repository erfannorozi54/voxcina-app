package com.voxcina.shop.presentation.addresses

import com.voxcina.shop.domain.model.UserAddress

data class AddressesUiState(
    val isLoading: Boolean = true,
    val addresses: List<UserAddress> = emptyList(),
    val error: String? = null,
    val isDeleting: Int? = null, // Index of address being deleted
    val showAddDialog: Boolean = false,
    val editingIndex: Int? = null,
    val isSaving: Boolean = false,
    val saveError: String? = null
)

data class AddressFormState(
    val street: String = "",
    val address: String = "",
    val city: String = "",
    val postalCode: String = "",
    val isDefault: Boolean = false
) {
    fun toUserAddress() = UserAddress(
        street = street.ifBlank { null },
        address = address.ifBlank { null },
        city = city,
        postalCode = postalCode,
        latitude = 0.0,
        longitude = 0.0,
        isDefault = isDefault
    )

    fun isValid(): Boolean = city.isNotBlank() && postalCode.isNotBlank() &&
            (street.isNotBlank() || address.isNotBlank())
}

fun UserAddress.toFormState() = AddressFormState(
    street = street ?: "",
    address = address ?: "",
    city = city,
    postalCode = postalCode,
    isDefault = isDefault
)
