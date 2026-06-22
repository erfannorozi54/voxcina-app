package com.voxcina.shop.presentation.profile.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.voxcina.shop.ui.components.BadgeIcon
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.VoxcinaTheme

/**
 * A component displaying an order status with icon, badge count, and label.
 * Used in the "My Orders" section of the profile screen.
 *
 * @param icon The icon representing the order status
 * @param label The label text for the status (in Persian)
 * @param count The count of orders in this status (badge shown only if > 0)
 * @param onClick Callback when the item is clicked
 * @param modifier Modifier for the component
 * @param iconTint Tint color for the icon (default: Primary)
 */
@Composable
fun OrderStatusItem(
    icon: ImageVector,
    label: String,
    count: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconTint: Color = Primary
) {
    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BadgeIcon(
            icon = icon,
            contentDescription = label,
            badgeCount = if (count > 0) count else null,
            iconSize = 28.dp,
            iconTint = iconTint,
            badgeOffsetX = (-6).dp,
            badgeOffsetY = (-6).dp
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            fontSize = 11.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}
