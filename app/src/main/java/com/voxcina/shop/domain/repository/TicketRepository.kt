package com.voxcina.shop.domain.repository

import com.voxcina.shop.domain.model.Ticket
import com.voxcina.shop.util.Result

interface TicketRepository {
    suspend fun getTickets(page: Int = 1, status: String? = null): Result<TicketsResult>
    suspend fun getTicketById(ticketId: String): Result<Ticket>
    suspend fun createTicket(subject: String, category: String, priority: String, message: String): Result<Ticket>
    suspend fun addMessage(ticketId: String, message: String): Result<Unit>
}

data class TicketsResult(
    val tickets: List<Ticket>,
    val currentPage: Int,
    val totalPages: Int,
    val totalTickets: Int
)
