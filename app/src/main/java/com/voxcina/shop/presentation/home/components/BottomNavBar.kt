package com.voxcina.shop.presentation.home.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.ripple
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.voxcina.shop.ui.components.BadgeIcon
import com.voxcina.shop.ui.components.GlassBottomNavigation
import com.voxcina.shop.ui.theme.Destructive
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.Secondary
import com.voxcina.shop.ui.theme.VoxcinaTheme

/**
 * Navigation destinations for the bottom navigation bar.
 */
enum class BottomNavDestination(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val contentDescription: String
) {
    HOME(
        route = "home",
        label = "خانه",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        contentDescription = "صفحه اصلی"
    ),
    PRODUCTS(
        route = "products",
        label = "محصولات",
        selectedIcon = Icons.Filled.Home, // Will use custom icon
        unselectedIcon = Icons.Outlined.Home,
        contentDescription = "محصولات"
    ),
    CART(
        route = "cart",
        label = "سبد خرید",
        selectedIcon = Icons.Filled.ShoppingCart,
        unselectedIcon = Icons.Outlined.ShoppingCart,
        contentDescription = "سبد خرید"
    ),
    PROFILE(
        route = "profile",
        label = "پروفایل",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person,
        contentDescription = "پروفایل کاربری"
    )
}


/**
 * Bottom navigation bar with glassmorphism styling.
 * Displays four navigation items: Home, Products, Cart, and Profile.
 * 
 * Features:
 * - Active state with primary color and indicator dot
 * - Cart badge showing item count (Persian digits)
 * - Glassmorphism background effect
 * - Minimum 48dp touch targets for accessibility
 * 
 * @param selectedDestination Currently selected navigation destination
 * @param cartItemCount Number of items in cart (shows badge if > 0)
 * @param onDestinationSelected Callback when a navigation item is tapped
 * @param modifier Optional modifier for the navigation bar
 * 
 * Requirements: 6.2, 6.3, 6.4
 */
@Composable
fun BottomNavBar(
    selectedDestination: BottomNavDestination,
    cartItemCount: Int = 0,
    onDestinationSelected: (BottomNavDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    // Force RTL layout direction for proper ordering (Profile -> Cart -> Products -> Home)
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        GlassBottomNavigation(modifier = modifier) {
            BottomNavDestination.entries.forEach { destination ->
                val isSelected = destination == selectedDestination
                
                BottomNavItem(
                    destination = destination,
                    isSelected = isSelected,
                    badgeCount = if (destination == BottomNavDestination.CART) cartItemCount else 0,
                    onClick = { onDestinationSelected(destination) }
                )
            }
        }
    }
}

/**
 * Individual navigation item with icon, label, and optional badge.
 * Uses BadgeIcon component for cart to show item count badge.
 * 
 * @param destination The navigation destination this item represents
 * @param isSelected Whether this item is currently selected
 * @param badgeCount Badge count to display (0 = no badge)
 * @param onClick Callback when item is tapped
 * 
 * Requirements: 8.2, 8.3, 8.4
 */
@Composable
private fun BottomNavItem(
    destination: BottomNavDestination,
    isSelected: Boolean,
    badgeCount: Int,
    onClick: () -> Unit
) {
    val animatedColor by animateColorAsState(
        targetValue = if (isSelected) Primary else Color.Gray,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "navItemColor"
    )
    
    val icon = if (isSelected) destination.selectedIcon else destination.unselectedIcon
    
    Column(
        modifier = Modifier
            .clip(CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, radius = 40.dp),
                onClick = onClick
            )
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .semantics {
                contentDescription = destination.contentDescription
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        // Icon with optional badge - use BadgeIcon for cart
        when {
            destination == BottomNavDestination.CART -> {
                // Use BadgeIcon component for cart with badge support
                BadgeIcon(
                    icon = icon,
                    contentDescription = null,
                    badgeCount = if (badgeCount > 0) badgeCount else null,
                    iconSize = 20.dp,
                    iconTint = animatedColor,
                    badgeColor = Destructive,
                    badgeTextColor = Color.White,
                    badgeOffsetX = (-4).dp,
                    badgeOffsetY = (-4).dp
                )
            }
            destination == BottomNavDestination.PRODUCTS -> {
                // Use custom grid icon for products
                ProductsIcon(
                    isSelected = isSelected,
                    tint = animatedColor
                )
            }
            else -> {
                // Standard icon for other destinations
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = animatedColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        
        // Label
        Text(
            text = destination.label,
            color = animatedColor,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
        
        // Active indicator dot
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(3.dp)
                    .background(Primary, CircleShape)
            )
        }
    }
}


/**
 * Custom products icon using a grid pattern.
 * Material Icons doesn't have a perfect category icon,
 * so we create a simple grid representation.
 * 
 * @param isSelected Whether the icon should appear selected
 * @param tint Color to apply to the icon
 */
@Composable
private fun ProductsIcon(
    isSelected: Boolean,
    tint: Color
) {
    // Using a simple grid layout to represent products
    Box(
        modifier = Modifier.size(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            androidx.compose.foundation.layout.Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            color = tint,
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(2.dp)
                        )
                )
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            color = tint,
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(2.dp)
                        )
                )
            }
            androidx.compose.foundation.layout.Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            color = tint,
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(2.dp)
                        )
                )
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            color = tint,
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(2.dp)
                        )
                )
            }
        }
    }
}

