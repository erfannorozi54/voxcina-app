package com.voxcina.shop.domain.model

data class Ticket(
    val id: String,
    val ticketNumber: String,
    val subject: String,
    val category: String,
    val priority: String,
    val status: String,
    val messages: List<TicketMessage>,
    val createdAt: String,
    val updatedAt: String
)

data class TicketMessage(
    val id: String,
    val sender: String,
    val body: String,
    val createdAt: String
)

enum class TicketStatus(val apiValue: String, val displayName: String) {
    OPEN("open", "باز"),
    PENDING("pending", "در انتظار پاسخ"),
    ANSWERED("answered", "پاسخ داده شده"),
    CLOSED("closed", "بسته شده")
}

enum class TicketPriority(val apiValue: String, val displayName: String) {
    LOW("low", "کم"),
    MEDIUM("medium", "معمولی"),
    HIGH("high", "بالا"),
    URGENT("urgent", "فوری")
}

enum class TicketCategory(val apiValue: String, val displayName: String) {
    ORDER("order", "مشکلات سفارش"),
    PAYMENT("payment", "پرداخت و مالی"),
    PRODUCT("product", "محصولات و موجودی"),
    TECHNICAL("technical", "مشکلات فنی"),
    GENERAL("general", "سوالات عمومی")
}
