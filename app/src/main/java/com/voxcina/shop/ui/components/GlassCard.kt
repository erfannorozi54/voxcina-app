package com.voxcina.shop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.voxcina.shop.ui.theme.VoxcinaTheme

/**
 * A glassmorphism-styled card component with configurable transparency, blur, and border.
 * Creates a frosted glass effect suitable for overlays, floating elements, and image overlays.
 *
 * @param modifier Modifier for the card container
 * @param backgroundAlpha Alpha value for the white background (0.0 - 1.0)
 * @param borderAlpha Alpha value for the white border (0.0 - 1.0)
 * @param blurRadius Blur radius for the glass effect
 * @param cornerRadius Corner radius for the card shape
 * @param content Content to display inside the glass card
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    backgroundAlpha: Float = 0.15f,
    borderAlpha: Float = 0.3f,
    blurRadius: Dp = 16.dp,
    cornerRadius: Dp = 16.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    
    Box(
        modifier = modifier.clip(shape)
    ) {
        // Background blur layer - creates the frosted glass effect
        Box(
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer {
                    // Apply blur effect
                    this.alpha = 1f
                }
                .blur(radius = blurRadius)
                .background(
                    color = Color.White.copy(alpha = backgroundAlpha),
                    shape = shape
                )
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = borderAlpha),
                    shape = shape
                )
        )
        
        // Content layer with subtle background
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    color = Color.White.copy(alpha = backgroundAlpha * 0.67f),
                    shape = shape
                )
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = borderAlpha * 0.83f),
                    shape = shape
                )
        )
        
        // Actual content
        Box(
            modifier = Modifier.matchParentSize(),
            content = content
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1A3C69)
@Composable
private fun GlassCardPreview() {
    VoxcinaTheme {
        Box(
            modifier = Modifier
                .padding(16.dp)
                .background(Color(0xFF1A3C69))
                .padding(16.dp)
        ) {
            GlassCard(
                modifier = Modifier.padding(16.dp)
            ) {
                Box(modifier = Modifier.padding(16.dp)) {
                    androidx.compose.material3.Text(
                        text = "Glass Card Content",
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFF5733)
@Composable
private fun GlassCardOnImagePreview() {
    VoxcinaTheme {
        Box(
            modifier = Modifier
                .padding(16.dp)
                .background(Color(0xFFFF5733))
                .padding(16.dp)
        ) {
            GlassCard(
                backgroundAlpha = 0.2f,
                borderAlpha = 0.35f,
                blurRadius = 18.dp
            ) {
                Box(modifier = Modifier.padding(16.dp)) {
                    androidx.compose.material3.Text(
                        text = "Overlay on Image",
                        color = Color.White
                    )
                }
            }
        }
    }
}
