package com.voxcina.shop.presentation.orders

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.voxcina.shop.domain.model.OrderStatus
import com.voxcina.shop.domain.repository.OrderRepository
import com.voxcina.shop.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow<OrdersUiState>(OrdersUiState.Loading)
    val uiState: StateFlow<OrdersUiState> = _uiState.asStateFlow()

    private val _selectedTab = MutableStateFlow(OrderFilterTab.ALL)
    val selectedTab: StateFlow<OrderFilterTab> = _selectedTab.asStateFlow()

    private var allOrders: List<com.voxcina.shop.domain.model.Order> = emptyList()

    init {
        val statusParam = savedStateHandle.get<String>("status")
        if (statusParam != null) {
            _selectedTab.value = OrderFilterTab.entries.find { it.apiValue == statusParam } ?: OrderFilterTab.ALL
        }
        loadOrders()
    }

    fun loadOrders() {
        viewModelScope.launch {
            _uiState.value = OrdersUiState.Loading
            when (val result = orderRepository.getOrders(page = 1, limit = 50)) {
                is Result.Success -> {
                    allOrders = result.data.orders
                    applyFilter()
                }
                is Result.Error -> {
                    _uiState.value = OrdersUiState.Error(result.error.message)
                }
            }
        }
    }

    fun selectTab(tab: OrderFilterTab) {
        _selectedTab.value = tab
        applyFilter()
    }

    private fun applyFilter() {
        val filtered = when (_selectedTab.value) {
            OrderFilterTab.ALL -> allOrders
            OrderFilterTab.PENDING -> allOrders.filter { it.status == OrderStatus.PENDING }
            OrderFilterTab.PROCESSING -> allOrders.filter { it.status == OrderStatus.PROCESSING }
            OrderFilterTab.SHIPPED -> allOrders.filter { it.status == OrderStatus.SHIPPED }
            OrderFilterTab.DELIVERED -> allOrders.filter { it.status == OrderStatus.DELIVERED }
        }
        _uiState.value = OrdersUiState.Success(
            orders = filtered,
            hasOrders = allOrders.isNotEmpty()
        )
    }
}
