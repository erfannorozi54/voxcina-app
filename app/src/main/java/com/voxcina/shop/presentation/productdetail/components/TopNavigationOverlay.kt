package com.voxcina.shop.presentation.productdetail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.voxcina.shop.ui.components.GlassCard
import com.voxcina.shop.ui.theme.Destructive
import com.voxcina.shop.ui.theme.VoxcinaTheme

/**
 * Top navigation overlay for the product detail screen.
 * Displays glassmorphism-styled circular buttons for back, favorite, and share actions.
 * Positioned over the image gallery.
 *
 * Requirements: 2.1, 2.2, 2.3, 2.4, 2.5
 *
 * @param onBackClick Callback when back button is clicked
 * @param onFavoriteClick Callback when favorite button is clicked
 * @param onShareClick Callback when share button is clicked
 * @param isFavorite Whether the product is currently favorited
 * @param modifier Modifier for the overlay container
 */
@Composable
fun TopNavigationOverlay(
    onBackClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onShareClick: () -> Unit,
    isFavorite: Boolean,
    modifier: Modifier = Modifier
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back button (arrow_forward for RTL - appears as back arrow)
            GlassNavigationButton(
                icon = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "بازگشت",
                onClick = onBackClick
            )
            
            // Right side buttons: Favorite and Share
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Favorite button with toggle state
                GlassNavigationButton(
                    icon = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = if (isFavorite) "حذف از علاقه‌مندی‌ها" else "افزودن به علاقه‌مندی‌ها",
                    onClick = onFavoriteClick,
                    tint = if (isFavorite) Destructive else Color.White
                )
                
                // Share button
                GlassNavigationButton(
                    icon = Icons.Filled.Share,
                    contentDescription = "اشتراک‌گذاری",
                    onClick = onShareClick
                )
            }
        }
    }
}

/**
 * A glassmorphism-styled circular navigation button.
 * Uses GlassCard for the frosted glass effect.
 *
 * @param icon Icon to display
 * @param contentDescription Accessibility description
 * @param onClick Click callback
 * @param modifier Modifier for the button
 * @param tint Icon tint color
 */
@Composable
private fun GlassNavigationButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = Color.White
) {
    GlassCard(
        modifier = modifier.size(44.dp),
        backgroundAlpha = 0.2f,
        borderAlpha = 0.35f,
        blurRadius = 18.dp,
        cornerRadius = 22.dp // Half of 44.dp for circular shape
    ) {
        Box(
            modifier = Modifier.size(44.dp),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = onClick,
                modifier = Modifier.size(44.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = contentDescription,
                    tint = tint,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1A3C69)
@Composable
private fun TopNavigationOverlayPreview() {
    VoxcinaTheme {
        Box(
            modifier = Modifier.padding(16.dp)
        ) {
            TopNavigationOverlay(
                onBackClick = {},
                onFavoriteClick = {},
                onShareClick = {},
                isFavorite = false
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1A3C69)
@Composable
private fun TopNavigationOverlayFavoritedPreview() {
    VoxcinaTheme {
        Box(
            modifier = Modifier.padding(16.dp)
        ) {
            TopNavigationOverlay(
                onBackClick = {},
                onFavoriteClick = {},
                onShareClick = {},
                isFavorite = true
            )
        }
    }
}
