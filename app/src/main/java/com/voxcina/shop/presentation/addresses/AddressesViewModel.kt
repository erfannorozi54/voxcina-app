package com.voxcina.shop.presentation.addresses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.voxcina.shop.data.remote.LocalityApi
import com.voxcina.shop.domain.repository.AddressRepository
import com.voxcina.shop.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressesViewModel @Inject constructor(
    private val addressRepository: AddressRepository,
    private val localityApi: LocalityApi
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddressesUiState())
    val uiState = _uiState.asStateFlow()

    private val _formState = MutableStateFlow(AddressFormState())
    val formState = _formState.asStateFlow()

    init {
        loadAddresses()
        loadProvinces()
    }

    fun loadAddresses() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = addressRepository.getAddresses()) {
                is Result.Success -> _uiState.update { 
                    it.copy(isLoading = false, addresses = result.data) 
                }
                is Result.Error -> _uiState.update { 
                    it.copy(isLoading = false, error = result.error.message) 
                }
            }
        }
    }

    private fun loadProvinces() {
        viewModelScope.launch {
            _uiState.update { it.copy(loadingProvinces = true) }
            try {
                val response = localityApi.getProvinces()
                if (response.isSuccessful) {
                    _uiState.update { it.copy(provinces = response.body() ?: emptyList(), loadingProvinces = false) }
                } else {
                    _uiState.update { it.copy(loadingProvinces = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(loadingProvinces = false) }
            }
        }
    }

    fun loadCities(provinceCode: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(loadingCities = true, cities = emptyList()) }
            try {
                val response = localityApi.getCities(provinceCode)
                if (response.isSuccessful) {
                    _uiState.update { it.copy(cities = response.body() ?: emptyList(), loadingCities = false) }
                } else {
                    _uiState.update { it.copy(loadingCities = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(loadingCities = false) }
            }
        }
    }

    fun showAddDialog() {
        _formState.value = AddressFormState(isDefault = _uiState.value.addresses.isEmpty())
        _uiState.update { it.copy(showAddDialog = true, editingIndex = null, saveError = null, cities = emptyList()) }
    }

    fun showEditDialog(index: Int) {
        val address = _uiState.value.addresses.getOrNull(index) ?: return
        _formState.value = address.toFormState()
        _uiState.update { it.copy(showAddDialog = true, editingIndex = index, saveError = null) }
        // Load cities for the province
        if (address.provinceCode > 0) {
            loadCities(address.provinceCode)
        }
    }

    fun dismissDialog() {
        _uiState.update { it.copy(showAddDialog = false, editingIndex = null, saveError = null) }
    }

    fun updateForm(form: AddressFormState) {
        _formState.value = form
    }

    fun saveAddress() {
        val form = _formState.value
        if (!form.isValid()) {
            _uiState.update { it.copy(saveError = "لطفاً تمام فیلدهای ضروری را پر کنید") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, saveError = null) }
            val editIndex = _uiState.value.editingIndex
            val result = if (editIndex != null) {
                addressRepository.updateAddress(editIndex, form.toUserAddress())
            } else {
                addressRepository.addAddress(form.toUserAddress())
            }

            when (result) {
                is Result.Success -> _uiState.update {
                    it.copy(
                        isSaving = false,
                        addresses = result.data,
                        showAddDialog = false,
                        editingIndex = null
                    )
                }
                is Result.Error -> _uiState.update {
                    it.copy(isSaving = false, saveError = result.error.message)
                }
            }
        }
    }

    fun deleteAddress(index: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isDeleting = index) }
            when (val result = addressRepository.deleteAddress(index)) {
                is Result.Success -> _uiState.update {
                    it.copy(isDeleting = null, addresses = result.data)
                }
                is Result.Error -> _uiState.update {
                    it.copy(isDeleting = null, error = result.error.message)
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
