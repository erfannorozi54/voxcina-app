package com.voxcina.shop.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.voxcina.shop.R
import com.voxcina.shop.ui.theme.Destructive
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.PrimaryDark
import com.voxcina.shop.ui.theme.Secondary
import com.voxcina.shop.ui.theme.VoxcinaTheme

/**
 * Home screen header component with user avatar, welcome text, and action buttons.
 * Implements Requirements 1.1, 1.2, 1.3 from the home screen spec.
 *
 * @param modifier Modifier for the header container
 * @param hasNotifications Whether to show notification badge
 * @param cartItemCount Number of items in cart (0 hides badge)
 * @param onNotificationClick Callback when notification button is clicked
 * @param onCartClick Callback when cart button is clicked
 */
@Composable
fun HomeHeader(
    modifier: Modifier = Modifier,
    hasNotifications: Boolean = false,
    cartItemCount: Int = 0,
    onNotificationClick: () -> Unit = {},
    onCartClick: () -> Unit = {}
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // User avatar and welcome text
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // User avatar placeholder
                UserAvatarPlaceholder()
                
                Spacer(modifier = Modifier.width(12.dp))
                
                // Welcome text
                WelcomeText()
            }
            
            // Action buttons
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Notification button with badge
                NotificationButton(
                    hasNotifications = hasNotifications,
                    onClick = onNotificationClick
                )
                
                Spacer(modifier = Modifier.width(4.dp))
                
                // Cart button with badge
                CartButton(
                    itemCount = cartItemCount,
                    onClick = onCartClick
                )
            }
        }
    }
}

/**
 * User avatar placeholder - circular icon with user initial or default icon.
 */
@Composable
private fun UserAvatarPlaceholder(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(Primary.copy(alpha = 0.1f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "ک",
            color = Primary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Welcome text with greeting and user name.
 */
@Composable
private fun WelcomeText(
    modifier: Modifier = Modifier
) {
    androidx.compose.foundation.layout.Column(
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.home_welcome),
            color = Color.Gray,
            fontSize = 12.sp
        )
        Text(
            text = stringResource(R.string.home_user_greeting),
            color = PrimaryDark,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Notification button with optional badge indicator.
 * Minimum 48dp touch target for accessibility (Requirement 1.2).
 */
@Composable
private fun NotificationButton(
    hasNotifications: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(48.dp)
    ) {
        BadgedBox(
            badge = {
                if (hasNotifications) {
                    Badge(
                        containerColor = Destructive,
                        modifier = Modifier.size(8.dp)
                    )
                }
            }
        ) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = stringResource(R.string.home_notifications),
                tint = PrimaryDark,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

/**
 * Cart button with item count badge.
 * Minimum 48dp touch target for accessibility (Requirement 1.2).
 */
@Composable
private fun CartButton(
    itemCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(48.dp)
    ) {
        BadgedBox(
            badge = {
                if (itemCount > 0) {
                    Badge(
                        containerColor = Destructive,
                        contentColor = Color.White
                    ) {
                        Text(
                            text = if (itemCount > 99) "۹۹+" else itemCount.toString(),
                            fontSize = 10.sp
                        )
                    }
                }
            }
        ) {
            Icon(
                imageVector = Icons.Outlined.ShoppingCart,
                contentDescription = stringResource(R.string.home_cart),
                tint = PrimaryDark,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeHeaderPreview() {
    VoxcinaTheme {
        HomeHeader(
            hasNotifications = true,
            cartItemCount = 3
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeHeaderNoNotificationsPreview() {
    VoxcinaTheme {
        HomeHeader(
            hasNotifications = false,
            cartItemCount = 0
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeHeaderManyItemsPreview() {
    VoxcinaTheme {
        HomeHeader(
            hasNotifications = true,
            cartItemCount = 150
        )
    }
}
