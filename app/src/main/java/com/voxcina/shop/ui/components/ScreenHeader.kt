package com.voxcina.shop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.voxcina.shop.ui.theme.Destructive
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.SecondaryLight
import com.voxcina.shop.ui.theme.VoxcinaTheme

/**
 * A reusable screen header component with back button, centered title, and optional action button.
 * Features sticky positioning with backdrop blur effect.
 * 
 * Reusable for cart, product detail, checkout, profile screens.
 *
 * @param title The centered title text
 * @param onBackClick Callback when back button is clicked
 * @param modifier Modifier for the header
 * @param actionIcon Optional action icon (displayed on the left in RTL)
 * @param actionIconTint Tint color for the action icon (default: Primary)
 * @param onActionClick Callback when action button is clicked
 * @param backgroundColor Background color for the header (default: SecondaryLight with blur)
 * @param showBackButton Whether to show the back button (default: true)
 */
@Composable
fun ScreenHeader(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    actionIcon: ImageVector? = null,
    actionIconTint: Color = Primary,
    onActionClick: () -> Unit = {},
    backgroundColor: Color = SecondaryLight,
    showBackButton: Boolean = true
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(backgroundColor.copy(alpha = 0.95f))
        ) {
            // Blur effect layer
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .blur(16.dp)
                    .background(backgroundColor.copy(alpha = 0.8f))
            )
            
            // Content layer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back button (right side in RTL)
                if (showBackButton) {
                    HeaderIconButton(
                        icon = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "بازگشت",
                        onClick = onBackClick,
                        tint = Primary,
                        forceDirection = LayoutDirection.Ltr
                    )
                } else {
                    // Spacer to maintain layout
                    Box(modifier = Modifier.size(48.dp))
                }
                
                // Centered title
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
                
                // Action button (left side in RTL)
                if (actionIcon != null) {
                    HeaderIconButton(
                        icon = actionIcon,
                        contentDescription = "عملیات",
                        onClick = onActionClick,
                        tint = actionIconTint
                    )
                } else {
                    // Spacer to maintain layout
                    Box(modifier = Modifier.size(48.dp))
                }
            }
        }
    }
}

@Composable
private fun HeaderIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    tint: Color,
    forceDirection: LayoutDirection? = null
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (forceDirection != null) {
            CompositionLocalProvider(LocalLayoutDirection provides forceDirection) {
                Icon(
                    imageVector = icon,
                    contentDescription = contentDescription,
                    modifier = Modifier.size(24.dp),
                    tint = tint
                )
            }
        } else {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                modifier = Modifier.size(24.dp),
                tint = tint
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ScreenHeaderPreview() {
    VoxcinaTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Cart header with delete action
            ScreenHeader(
                title = "سبد خرید",
                onBackClick = {},
                actionIcon = Icons.Default.Delete,
                actionIconTint = Destructive,
                onActionClick = {}
            )
            
            // Simple header without action
            ScreenHeader(
                title = "جزئیات محصول",
                onBackClick = {}
            )
            
            // Header with more options
            ScreenHeader(
                title = "پروفایل",
                onBackClick = {},
                actionIcon = Icons.Default.MoreVert,
                onActionClick = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ScreenHeaderNoBackPreview() {
    VoxcinaTheme {
        ScreenHeader(
            title = "خانه",
            onBackClick = {},
            showBackButton = false,
            actionIcon = Icons.Default.MoreVert,
            onActionClick = {}
        )
    }
}
