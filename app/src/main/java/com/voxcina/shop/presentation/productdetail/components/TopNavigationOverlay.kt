package com.voxcina.shop.presentation.productdetail.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.voxcina.shop.ui.components.GlassCard
import com.voxcina.shop.ui.theme.Destructive
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.VoxcinaTheme

/**
 * Top navigation overlay for the product detail screen.
 * Displays glassmorphism-styled circular buttons for back, favorite, and share actions.
 * Supports scroll-aware styling - buttons get solid background when scrolled.
 *
 * Requirements: 2.1, 2.2, 2.3, 2.4, 2.5
 *
 * @param onBackClick Callback when back button is clicked
 * @param onFavoriteClick Callback when favorite button is clicked
 * @param onShareClick Callback when share button is clicked
 * @param isFavorite Whether the product is currently favorited
 * @param isScrolled Whether the content has been scrolled (shows solid background)
 * @param modifier Modifier for the overlay container
 */
@Composable
fun TopNavigationOverlay(
    onBackClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onShareClick: () -> Unit,
    isFavorite: Boolean,
    modifier: Modifier = Modifier,
    isScrolled: Boolean = false
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back button (arrow pointing right for RTL back navigation)
            ScrollAwareNavigationButton(
                icon = Icons.Filled.ArrowForward,
                contentDescription = "بازگشت",
                onClick = onBackClick,
                isScrolled = isScrolled,
                mirrorIcon = false
            )
            
            // Right side buttons: Favorite and Share
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Favorite button with toggle state
                ScrollAwareNavigationButton(
                    icon = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = if (isFavorite) "حذف از علاقه‌مندی‌ها" else "افزودن به علاقه‌مندی‌ها",
                    onClick = onFavoriteClick,
                    isScrolled = isScrolled,
                    tintWhenNotScrolled = if (isFavorite) Destructive else Color.White,
                    tintWhenScrolled = if (isFavorite) Destructive else Primary,
                    mirrorIcon = false
                )
                
                // Share button - mirror horizontally for RTL
                ScrollAwareNavigationButton(
                    icon = Icons.Filled.Share,
                    contentDescription = "اشتراک‌گذاری",
                    onClick = onShareClick,
                    isScrolled = isScrolled,
                    mirrorIcon = true
                )
            }
        }
    }
}

/**
 * A navigation button that changes appearance based on scroll state.
 * When not scrolled: Glass effect with white icon
 * When scrolled: Solid white background with primary color icon
 *
 * @param icon Icon to display
 * @param contentDescription Accessibility description
 * @param onClick Click callback
 * @param isScrolled Whether content is scrolled
 * @param modifier Modifier for the button
 * @param tintWhenNotScrolled Icon tint when not scrolled
 * @param tintWhenScrolled Icon tint when scrolled
 * @param mirrorIcon Whether to mirror the icon horizontally (for RTL)
 */
@Composable
private fun ScrollAwareNavigationButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    isScrolled: Boolean,
    modifier: Modifier = Modifier,
    tintWhenNotScrolled: Color = Color.White,
    tintWhenScrolled: Color = Primary,
    mirrorIcon: Boolean = false
) {
    val animatedTint by animateColorAsState(
        targetValue = if (isScrolled) tintWhenScrolled else tintWhenNotScrolled,
        animationSpec = tween(durationMillis = 200),
        label = "iconTint"
    )
    
    val animatedBackground by animateColorAsState(
        targetValue = if (isScrolled) Color.White else Color.Transparent,
        animationSpec = tween(durationMillis = 200),
        label = "background"
    )

    if (isScrolled) {
        // Solid white background when scrolled
        Box(
            modifier = modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(animatedBackground),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = onClick,
                modifier = Modifier.size(44.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = contentDescription,
                    tint = animatedTint,
                    modifier = Modifier
                        .size(24.dp)
                        .then(if (mirrorIcon) Modifier.scale(scaleX = -1f, scaleY = 1f) else Modifier)
                )
            }
        }
    } else {
        // Glass effect when not scrolled
        GlassCard(
            modifier = modifier.size(44.dp),
            backgroundAlpha = 0.2f,
            borderAlpha = 0.35f,
            blurRadius = 18.dp,
            cornerRadius = 22.dp
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
                        tint = animatedTint,
                        modifier = Modifier
                            .size(24.dp)
                            .then(if (mirrorIcon) Modifier.scale(scaleX = -1f, scaleY = 1f) else Modifier)
                    )
                }
            }
        }
    }
}

/**
 * Legacy glass-only navigation button (kept for backward compatibility).
 */
@Composable
private fun GlassNavigationButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = Color.White,
    mirrorIcon: Boolean = false
) {
    GlassCard(
        modifier = modifier.size(44.dp),
        backgroundAlpha = 0.2f,
        borderAlpha = 0.35f,
        blurRadius = 18.dp,
        cornerRadius = 22.dp
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
                    modifier = Modifier
                        .size(24.dp)
                        .then(if (mirrorIcon) Modifier.scale(scaleX = -1f, scaleY = 1f) else Modifier)
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
                isFavorite = false,
                isScrolled = false
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFCFAF8)
@Composable
private fun TopNavigationOverlayScrolledPreview() {
    VoxcinaTheme {
        Box(
            modifier = Modifier.padding(16.dp)
        ) {
            TopNavigationOverlay(
                onBackClick = {},
                onFavoriteClick = {},
                onShareClick = {},
                isFavorite = false,
                isScrolled = true
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
                isFavorite = true,
                isScrolled = false
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFCFAF8)
@Composable
private fun TopNavigationOverlayFavoritedScrolledPreview() {
    VoxcinaTheme {
        Box(
            modifier = Modifier.padding(16.dp)
        ) {
            TopNavigationOverlay(
                onBackClick = {},
                onFavoriteClick = {},
                onShareClick = {},
                isFavorite = true,
                isScrolled = true
            )
        }
    }
}
