package com.voxcina.shop.presentation.profile.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.AssignmentReturn
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.PendingActions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.voxcina.shop.ui.components.SoftShadowCard
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.VoxcinaTheme

/**
 * Order status enum representing different order states.
 */
enum class OrderStatus(val label: String, val apiValue: String) {
    PENDING("در انتظار", "pending"),
    PROCESSING("در حال پردازش", "processing"),
    SHIPPED("ارسال شده", "shipped"),
    RETURNED("مرجوعی", "returned")
}

/**
 * A section displaying order status summary with header and status items.
 * Shows "سفارش‌های من" header with "مشاهده همه" link and four order status items.
 *
 * @param pendingCount Number of pending orders
 * @param processingCount Number of processing orders
 * @param shippedCount Number of shipped orders
 * @param returnedCount Number of returned orders
 * @param onStatusClick Callback when a status item is clicked with the status type
 * @param onViewAllClick Callback when "مشاهده همه" is clicked
 * @param modifier Modifier for the section
 */
@Composable
fun OrderStatusSection(
    pendingCount: Int,
    processingCount: Int,
    shippedCount: Int,
    returnedCount: Int,
    onStatusClick: (OrderStatus) -> Unit,
    onViewAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Section header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "سفارش‌های من",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            Text(
                text = "مشاهده همه",
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 13.sp,
                color = Primary,
                modifier = Modifier.clickable(onClick = onViewAllClick)
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Order status items in a card
        SoftShadowCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 16.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Pending orders
                OrderStatusItem(
                    icon = Icons.Outlined.PendingActions,
                    label = OrderStatus.PENDING.label,
                    count = pendingCount,
                    onClick = { onStatusClick(OrderStatus.PENDING) },
                    modifier = Modifier.weight(1f)
                )
                
                // Processing orders
                OrderStatusItem(
                    icon = Icons.Outlined.Inventory2,
                    label = OrderStatus.PROCESSING.label,
                    count = processingCount,
                    onClick = { onStatusClick(OrderStatus.PROCESSING) },
                    modifier = Modifier.weight(1f)
                )
                
                // Shipped orders
                OrderStatusItem(
                    icon = Icons.Outlined.LocalShipping,
                    label = OrderStatus.SHIPPED.label,
                    count = shippedCount,
                    onClick = { onStatusClick(OrderStatus.SHIPPED) },
                    modifier = Modifier.weight(1f)
                )
                
                // Returned orders
                OrderStatusItem(
                    icon = Icons.AutoMirrored.Outlined.AssignmentReturn,
                    label = OrderStatus.RETURNED.label,
                    count = returnedCount,
                    onClick = { onStatusClick(OrderStatus.RETURNED) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
