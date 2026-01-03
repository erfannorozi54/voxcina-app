package com.voxcina.shop.data.repository

import com.voxcina.shop.data.remote.TicketApi
import com.voxcina.shop.data.remote.dto.AddMessageRequest
import com.voxcina.shop.data.remote.dto.CreateTicketRequest
import com.voxcina.shop.data.remote.dto.toDomain
import com.voxcina.shop.domain.model.Ticket
import com.voxcina.shop.domain.repository.TicketRepository
import com.voxcina.shop.domain.repository.TicketsResult
import com.voxcina.shop.util.Result
import com.voxcina.shop.util.TicketError
import javax.inject.Inject

class TicketRepositoryImpl @Inject constructor(
    private val ticketApi: TicketApi
) : TicketRepository {

    override suspend fun getTickets(page: Int, status: String?): Result<TicketsResult> {
        return try {
            val response = ticketApi.getTickets(page = page, status = status)
            val tickets = response.tickets?.map { it.toDomain() } ?: emptyList()
            Result.Success(
                TicketsResult(
                    tickets = tickets,
                    currentPage = response.pagination?.currentPage ?: 1,
                    totalPages = response.pagination?.totalPages ?: 1,
                    totalTickets = response.pagination?.totalTickets ?: tickets.size
                )
            )
        } catch (e: Exception) {
            Result.Error(TicketError.TicketsLoadFailed)
        }
    }

    override suspend fun getTicketById(ticketId: String): Result<Ticket> {
        return try {
            val ticket = ticketApi.getTicketById(ticketId).toDomain()
            Result.Success(ticket)
        } catch (e: Exception) {
            Result.Error(TicketError.TicketNotFound)
        }
    }

    override suspend fun createTicket(
        subject: String,
        category: String,
        priority: String,
        message: String
    ): Result<Ticket> {
        return try {
            val request = CreateTicketRequest(subject, category, priority, message)
            val ticket = ticketApi.createTicket(request).toDomain()
            Result.Success(ticket)
        } catch (e: Exception) {
            Result.Error(TicketError.TicketCreateFailed)
        }
    }

    override suspend fun addMessage(ticketId: String, message: String): Result<Unit> {
        return try {
            ticketApi.addMessage(ticketId, AddMessageRequest(message))
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(TicketError.MessageSendFailed)
        }
    }
}
