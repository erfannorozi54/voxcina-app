package com.voxcina.shop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.voxcina.shop.ui.theme.VoxcinaTheme

/**
 * A reusable card component with soft shadow styling.
 * Shadow: 0 4px 20px -2px rgba(0,0,0,0.05)
 * 
 * Used for cart items, order summary, and any card requiring soft shadow effect.
 *
 * @param modifier Modifier for the card container
 * @param cornerRadius Corner radius for the card shape (default: 8dp)
 * @param backgroundColor Background color of the card (default: White)
 * @param content Content to display inside the card
 */
@Composable
fun SoftShadowCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 8.dp,
    backgroundColor: Color = Color.White,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    
    Box(
        modifier = modifier
            .shadow(
                elevation = 4.dp,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.05f),
                spotColor = Color.Black.copy(alpha = 0.05f)
            )
            .background(
                color = backgroundColor,
                shape = shape
            ),
        content = content
    )
}
