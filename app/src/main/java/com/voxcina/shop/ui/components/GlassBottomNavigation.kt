package com.voxcina.shop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.VoxcinaTheme

/**
 * A glassmorphism-styled bottom navigation bar with rounded top corners.
 * Implements the frosted glass effect with high alpha white background,
 * backdrop blur, and subtle top border gradient.
 *
 * @param modifier Modifier for the navigation container
 * @param content Row content for navigation items
 */
@Composable
fun GlassBottomNavigation(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    val shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
    ) {
        // Glass background layer with blur effect
        Box(
            modifier = Modifier
                .matchParentSize()
                .blur(radius = 20.dp)
                .background(
                    color = Color.White.copy(alpha = 0.9f),
                    shape = shape
                )
        )
        
        // Border gradient layer - subtle top border
        Box(
            modifier = Modifier
                .matchParentSize()
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Gray.copy(alpha = 0.1f),
                            Color.Transparent
                        )
                    ),
                    shape = shape
                )
        )
        
        // Content layer with safe area padding
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
    }
}
