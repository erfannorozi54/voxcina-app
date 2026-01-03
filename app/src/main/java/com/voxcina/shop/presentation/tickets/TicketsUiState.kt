package com.voxcina.shop.presentation.tickets

import com.voxcina.shop.domain.model.Ticket

sealed interface TicketsUiState {
    data object Loading : TicketsUiState
    
    data class Success(
        val tickets: List<Ticket>,
        val currentPage: Int = 1,
        val totalPages: Int = 1,
        val selectedStatus: String? = null,
        val showCreateDialog: Boolean = false,
        val isCreating: Boolean = false
    ) : TicketsUiState
    
    data class Error(val message: String) : TicketsUiState
}

sealed interface TicketsEvent {
    data class StatusFilterChanged(val status: String?) : TicketsEvent
    data class PageChanged(val page: Int) : TicketsEvent
    data object ShowCreateDialog : TicketsEvent
    data object HideCreateDialog : TicketsEvent
    data class CreateTicket(
        val subject: String,
        val category: String,
        val priority: String,
        val message: String
    ) : TicketsEvent
    data object Retry : TicketsEvent
}
