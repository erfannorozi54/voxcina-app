package com.voxcina.shop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.voxcina.shop.ui.theme.Destructive
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.VoxcinaTheme
import com.voxcina.shop.util.PersianDigitConverter

/**
 * An icon component with an optional badge count.
 * Uses PersianDigitConverter for displaying count in Persian digits.
 * 
 * Reusable for cart badge, notification badge, and similar use cases.
 *
 * @param icon The icon to display
 * @param contentDescription Content description for accessibility
 * @param modifier Modifier for the component
 * @param badgeCount Optional badge count (null or 0 hides the badge)
 * @param iconSize Size of the icon (default: 24dp)
 * @param iconTint Tint color for the icon (default: Primary)
 * @param badgeColor Background color for the badge (default: Destructive/Red)
 * @param badgeTextColor Text color for the badge count (default: White)
 * @param badgeOffsetX Horizontal offset for badge position (default: -4dp for RTL)
 * @param badgeOffsetY Vertical offset for badge position (default: -4dp)
 */
@Composable
fun BadgeIcon(
    icon: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    badgeCount: Int? = null,
    iconSize: Dp = 24.dp,
    iconTint: Color = Primary,
    badgeColor: Color = Destructive,
    badgeTextColor: Color = Color.White,
    badgeOffsetX: Dp = (-4).dp,
    badgeOffsetY: Dp = (-4).dp
) {
    Box(modifier = modifier) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(iconSize),
            tint = iconTint
        )
        
        // Show badge only if count is greater than 0
        if (badgeCount != null && badgeCount > 0) {
            val displayCount = if (badgeCount > 99) "۹۹+" else PersianDigitConverter.toPersianDigits(badgeCount.toString())
            
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = badgeOffsetX, y = badgeOffsetY)
                    .size(if (badgeCount > 9) 20.dp else 16.dp)
                    .background(badgeColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = displayCount,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = if (badgeCount > 9) 9.sp else 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = badgeTextColor
                )
            }
        }
    }
}
