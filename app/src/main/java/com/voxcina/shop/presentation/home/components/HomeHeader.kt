package com.voxcina.shop.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.voxcina.shop.R
import com.voxcina.shop.ui.theme.Destructive
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.PrimaryDark
import com.voxcina.shop.ui.theme.VazirMatnFamily
import com.voxcina.shop.ui.theme.VoxcinaTheme

@Composable
fun HomeHeader(
    modifier: Modifier = Modifier,
    userName: String? = null,
    hasNotifications: Boolean = false,
    cartItemCount: Int = 0,
    onNotificationClick: () -> Unit = {},
    onCartClick: () -> Unit = {}
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Primary.copy(alpha = 0.15f),
                                    Primary.copy(alpha = 0.05f)
                                )
                            )
                        )
                        .border(1.5.dp, Primary.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userName?.firstOrNull()?.toString() ?: "ک",
                        color = Primary,
                        fontSize = 18.sp,
                        fontFamily = VazirMatnFamily,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column {
                    Text(
                        text = stringResource(R.string.home_welcome),
                        color = Color.Gray,
                        fontSize = 12.sp,
                        fontFamily = VazirMatnFamily
                    )
                    Text(
                        text = userName ?: stringResource(R.string.home_user_greeting),
                        color = PrimaryDark,
                        fontSize = 16.sp,
                        fontFamily = VazirMatnFamily,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                GlassActionButton(
                    icon = Icons.Outlined.Notifications,
                    contentDescription = stringResource(R.string.home_notifications),
                    onClick = onNotificationClick,
                    showDot = hasNotifications
                )
                GlassActionButton(
                    icon = Icons.Outlined.ShoppingCart,
                    contentDescription = stringResource(R.string.home_cart),
                    onClick = onCartClick,
                    badgeCount = cartItemCount
                )
            }
        }
    }
}

@Composable
private fun GlassActionButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    showDot: Boolean = false,
    badgeCount: Int = 0
) {
    Box(
        modifier = Modifier
            .size(42.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.8f))
            .border(1.dp, Color.Gray.copy(alpha = 0.12f), RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        IconButton(onClick = onClick, modifier = Modifier.size(42.dp)) {
            BadgedBox(
                badge = {
                    when {
                        badgeCount > 0 -> Badge(containerColor = Destructive, contentColor = Color.White) {
                            Text(if (badgeCount > 99) "۹۹+" else badgeCount.toString(), fontSize = 9.sp, fontFamily = VazirMatnFamily)
                        }
                        showDot -> Badge(containerColor = Destructive, modifier = Modifier.size(8.dp))
                    }
                }
            ) {
                Icon(icon, contentDescription, tint = PrimaryDark, modifier = Modifier.size(22.dp))
            }
        }
    }
}
