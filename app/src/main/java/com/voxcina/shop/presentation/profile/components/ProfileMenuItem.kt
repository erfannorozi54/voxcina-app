package com.voxcina.shop.presentation.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.VoxcinaTheme

/**
 * A menu item component for the profile screen.
 * Displays an icon in a rounded container, label text, and a chevron arrow.
 *
 * @param icon The icon to display
 * @param label The label text for the menu item
 * @param onClick Callback when the menu item is clicked
 * @param modifier Modifier for the menu item
 * @param showDivider Whether to show a divider below the item (default: true)
 * @param iconTint Tint color for the icon (default: Primary)
 */
@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showDivider: Boolean = true,
    iconTint: Color = Primary
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon container with primary/5 background
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Primary.copy(alpha = 0.05f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    modifier = Modifier.size(22.dp),
                    tint = iconTint
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Label text
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )
            
            // Chevron arrow (pointing left for RTL)
            Icon(
                imageVector = Icons.Filled.KeyboardArrowLeft,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = Color.Gray
            )
        }
        
        // Divider
        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(start = 68.dp),
                thickness = 0.5.dp,
                color = Color.LightGray.copy(alpha = 0.5f)
            )
        }
    }
}
