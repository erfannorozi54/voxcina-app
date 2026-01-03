package com.voxcina.shop.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.voxcina.shop.domain.model.Ticket
import com.voxcina.shop.domain.model.TicketMessage

data class TicketDto(
    @SerializedName("id") val id: String?,
    @SerializedName("ticket_number") val ticketNumber: String?,
    @SerializedName("subject") val subject: String?,
    @SerializedName("category") val category: String?,
    @SerializedName("priority") val priority: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("messages") val messages: List<TicketMessageDto>?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)

data class TicketMessageDto(
    @SerializedName("id") val id: String?,
    @SerializedName("sender") val sender: String?,
    @SerializedName("body") val body: String?,
    @SerializedName("created_at") val createdAt: String?
)

data class TicketsResponse(
    @SerializedName("tickets") val tickets: List<TicketDto>?,
    @SerializedName("pagination") val pagination: TicketPaginationDto?
)

data class TicketPaginationDto(
    @SerializedName("currentPage") val currentPage: Int?,
    @SerializedName("totalPages") val totalPages: Int?,
    @SerializedName("totalTickets") val totalTickets: Int?
)

data class CreateTicketRequest(
    @SerializedName("subject") val subject: String,
    @SerializedName("category") val category: String,
    @SerializedName("priority") val priority: String,
    @SerializedName("message") val message: String
)

data class AddMessageRequest(
    @SerializedName("body") val body: String
)

fun TicketMessageDto.toDomain(): TicketMessage = TicketMessage(
    id = id.orEmpty(),
    sender = sender.orEmpty(),
    body = body.orEmpty(),
    createdAt = createdAt.orEmpty()
)

fun TicketDto.toDomain(): Ticket = Ticket(
    id = id.orEmpty(),
    ticketNumber = ticketNumber.orEmpty(),
    subject = subject.orEmpty(),
    category = category.orEmpty(),
    priority = priority ?: "medium",
    status = status ?: "open",
    messages = messages?.map { it.toDomain() } ?: emptyList(),
    createdAt = createdAt.orEmpty(),
    updatedAt = updatedAt.orEmpty()
)
