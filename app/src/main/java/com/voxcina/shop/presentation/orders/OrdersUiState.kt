package com.voxcina.shop.presentation.orders

import com.voxcina.shop.domain.model.Order

sealed class OrdersUiState {
    data object Loading : OrdersUiState()
    
    data class Success(
        val orders: List<Order>,
        val hasOrders: Boolean
    ) : OrdersUiState()
    
    data class Error(val message: String) : OrdersUiState()
}

enum class OrderFilterTab(val label: String, val apiValue: String?) {
    ALL("همه", null),
    PENDING("در انتظار", "pending"),
    PROCESSING("در حال پردازش", "processing"),
    SHIPPED("ارسال شده", "shipped"),
    DELIVERED("تحویل شده", "delivered")
}
