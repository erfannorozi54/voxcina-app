package com.voxcina.shop.presentation.tickets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.voxcina.shop.domain.repository.TicketRepository
import com.voxcina.shop.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TicketsViewModel @Inject constructor(
    private val ticketRepository: TicketRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<TicketsUiState>(TicketsUiState.Loading)
    val uiState: StateFlow<TicketsUiState> = _uiState.asStateFlow()

    init {
        loadTickets()
    }

    fun onEvent(event: TicketsEvent) {
        when (event) {
            is TicketsEvent.StatusFilterChanged -> {
                val current = _uiState.value
                if (current is TicketsUiState.Success) {
                    _uiState.update { current.copy(selectedStatus = event.status) }
                    loadTickets(status = event.status)
                }
            }
            is TicketsEvent.PageChanged -> loadTickets(page = event.page)
            is TicketsEvent.ShowCreateDialog -> {
                val current = _uiState.value
                if (current is TicketsUiState.Success) {
                    _uiState.update { current.copy(showCreateDialog = true) }
                }
            }
            is TicketsEvent.HideCreateDialog -> {
                val current = _uiState.value
                if (current is TicketsUiState.Success) {
                    _uiState.update { current.copy(showCreateDialog = false) }
                }
            }
            is TicketsEvent.CreateTicket -> createTicket(
                event.subject, event.category, event.priority, event.message
            )
            is TicketsEvent.Retry -> loadTickets()
        }
    }

    private fun loadTickets(page: Int = 1, status: String? = null) {
        viewModelScope.launch {
            val currentState = _uiState.value
            val selectedStatus = if (currentState is TicketsUiState.Success) {
                status ?: currentState.selectedStatus
            } else status

            if (currentState !is TicketsUiState.Success) {
                _uiState.value = TicketsUiState.Loading
            }

            when (val result = ticketRepository.getTickets(page, selectedStatus)) {
                is Result.Success -> {
                    _uiState.value = TicketsUiState.Success(
                        tickets = result.data.tickets,
                        currentPage = result.data.currentPage,
                        totalPages = result.data.totalPages,
                        selectedStatus = selectedStatus
                    )
                }
                is Result.Error -> {
                    _uiState.value = TicketsUiState.Error(result.error.message)
                }
            }
        }
    }

    private fun createTicket(subject: String, category: String, priority: String, message: String) {
        viewModelScope.launch {
            val current = _uiState.value
            if (current is TicketsUiState.Success) {
                _uiState.update { current.copy(isCreating = true) }

                when (ticketRepository.createTicket(subject, category, priority, message)) {
                    is Result.Success -> {
                        _uiState.update { current.copy(showCreateDialog = false, isCreating = false) }
                        loadTickets()
                    }
                    is Result.Error -> {
                        _uiState.update { current.copy(isCreating = false) }
                    }
                }
            }
        }
    }
}
