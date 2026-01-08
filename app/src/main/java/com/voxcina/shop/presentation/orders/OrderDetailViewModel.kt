package com.voxcina.shop.presentation.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.voxcina.shop.domain.model.Order
import com.voxcina.shop.domain.repository.OrderRepository
import com.voxcina.shop.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderDetailViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<OrderDetailUiState>(OrderDetailUiState.Loading)
    val uiState: StateFlow<OrderDetailUiState> = _uiState

    fun loadOrder(orderId: String) {
        viewModelScope.launch {
            _uiState.value = OrderDetailUiState.Loading
            when (val result = orderRepository.getOrderById(orderId)) {
                is Result.Success -> {
                    _uiState.value = OrderDetailUiState.Success(result.data)
                }
                is Result.Error -> {
                    _uiState.value = OrderDetailUiState.Error(result.error.message)
                }
            }
        }
    }
}

sealed class OrderDetailUiState {
    data object Loading : OrderDetailUiState()
    data class Success(val order: Order) : OrderDetailUiState()
    data class Error(val message: String) : OrderDetailUiState()
}
