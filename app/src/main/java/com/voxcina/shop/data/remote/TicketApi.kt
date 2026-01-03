package com.voxcina.shop.data.remote

import com.voxcina.shop.data.remote.dto.AddMessageRequest
import com.voxcina.shop.data.remote.dto.CreateTicketRequest
import com.voxcina.shop.data.remote.dto.TicketDto
import com.voxcina.shop.data.remote.dto.TicketsResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface TicketApi {
    @GET("tickets")
    suspend fun getTickets(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10,
        @Query("status") status: String? = null
    ): TicketsResponse

    @GET("tickets/{ticketId}")
    suspend fun getTicketById(@Path("ticketId") ticketId: String): TicketDto

    @POST("tickets")
    suspend fun createTicket(@Body request: CreateTicketRequest): TicketDto

    @POST("tickets/{ticketId}/messages")
    suspend fun addMessage(
        @Path("ticketId") ticketId: String,
        @Body request: AddMessageRequest
    )
}
