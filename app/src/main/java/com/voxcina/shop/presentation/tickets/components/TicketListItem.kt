package com.voxcina.shop.presentation.tickets.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.voxcina.shop.domain.model.Ticket
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.VoxcinaTheme

@Composable
fun TicketListItem(
    ticket: Ticket,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = ticket.ticketNumber,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = Primary
            )
            TicketStatusBadge(status = ticket.status)
        }
        
        Text(
            text = ticket.subject,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 8.dp)
        )
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TicketPriorityBadge(priority = ticket.priority)
            Text(
                text = getCategoryLabel(ticket.category),
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun TicketStatusBadge(status: String, modifier: Modifier = Modifier) {
    val (bgColor, textColor, label) = when (status) {
        "open" -> Triple(Color(0xFFFEF3C7), Color(0xFF92400E), "باز")
        "pending" -> Triple(Color(0xFFDBEAFE), Color(0xFF1E40AF), "در انتظار")
        "answered" -> Triple(Color(0xFFD1FAE5), Color(0xFF065F46), "پاسخ داده شده")
        "closed" -> Triple(Color(0xFFF3F4F6), Color(0xFF374151), "بسته شده")
        else -> Triple(Color(0xFFF3F4F6), Color(0xFF374151), status)
    }
    
    Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = textColor,
        modifier = modifier
            .background(bgColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}

@Composable
fun TicketPriorityBadge(priority: String, modifier: Modifier = Modifier) {
    val (bgColor, textColor, label) = when (priority) {
        "low" -> Triple(Color(0xFFE0F2FE), Color(0xFF0369A1), "کم")
        "medium" -> Triple(Color(0xFFE0E7FF), Color(0xFF4338CA), "معمولی")
        "high" -> Triple(Color(0xFFFFEDD5), Color(0xFFC2410C), "بالا")
        "urgent" -> Triple(Color(0xFFFEE2E2), Color(0xFFB91C1C), "فوری")
        else -> Triple(Color(0xFFF3F4F6), Color(0xFF374151), priority)
    }
    
    Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = textColor,
        modifier = modifier
            .background(bgColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}

private fun getCategoryLabel(category: String): String = when (category) {
    "order" -> "مشکلات سفارش"
    "payment" -> "پرداخت و مالی"
    "product" -> "محصولات"
    "technical" -> "مشکلات فنی"
    "general" -> "عمومی"
    else -> category
}
