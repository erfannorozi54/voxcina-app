package com.voxcina.shop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.VoxcinaTheme

/**
 * A reusable circular color selector button.
 * Shows the actual color with selection and disabled states.
 * 
 * Reusable for product details, product filters, and quick add dialogs.
 *
 * Requirements: 5.1, 5.2, 5.5, 5.6
 *
 * @param colorHex Hex color code (e.g., "#FF5733")
 * @param isSelected Whether this color is currently selected
 * @param isEnabled Whether this color is available (not disabled)
 * @param onClick Callback when the button is clicked
 * @param modifier Modifier for the button
 * @param size Size of the color button
 * @param ringColor Color of the selection ring
 */
@Composable
fun ColorSelectorButton(
    colorHex: String,
    isSelected: Boolean,
    isEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    ringColor: Color = Primary
) {
    val color = parseHexColor(colorHex)
    val contentAlpha = if (isEnabled) 1f else 0.4f
    
    Box(
        modifier = modifier
            .size(size + 8.dp) // Extra space for ring
            .alpha(contentAlpha)
            .clickable(enabled = isEnabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        // Selection ring (outer ring with offset)
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(size + 6.dp)
                    .border(
                        width = 2.dp,
                        color = ringColor,
                        shape = CircleShape
                    )
            )
        }
        
        // Color circle
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(color)
                .border(
                    width = 1.dp,
                    color = Color.Gray.copy(alpha = 0.3f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            // Checkmark icon when selected
            if (isSelected) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "انتخاب شده",
                    tint = getContrastColor(color),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/**
 * Parses a hex color string to a Compose Color.
 * Supports formats: "#RRGGBB", "#AARRGGBB", "RRGGBB", "AARRGGBB"
 */
private fun parseHexColor(hexColor: String): Color {
    return try {
        val cleanHex = hexColor.removePrefix("#")
        val colorLong = when (cleanHex.length) {
            6 -> "FF$cleanHex".toLong(16)
            8 -> cleanHex.toLong(16)
            else -> 0xFFCCCCCC // Default gray for invalid
        }
        Color(colorLong)
    } catch (e: Exception) {
        Color.Gray // Fallback color
    }
}

/**
 * Returns a contrasting color (black or white) for text/icons on the given background.
 * Uses relative luminance calculation.
 */
private fun getContrastColor(backgroundColor: Color): Color {
    // Calculate relative luminance
    val luminance = 0.299 * backgroundColor.red + 
                    0.587 * backgroundColor.green + 
                    0.114 * backgroundColor.blue
    return if (luminance > 0.5) Color.Black else Color.White
}

@Preview(showBackground = true)
@Composable
private fun ColorSelectorButtonPreview() {
    VoxcinaTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Selected state
            ColorSelectorButton(
                colorHex = "#FF5733",
                isSelected = true,
                isEnabled = true,
                onClick = {}
            )
            
            // Unselected state
            ColorSelectorButton(
                colorHex = "#0000FF",
                isSelected = false,
                isEnabled = true,
                onClick = {}
            )
            
            // Disabled state
            ColorSelectorButton(
                colorHex = "#00FF00",
                isSelected = false,
                isEnabled = false,
                onClick = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ColorSelectorButtonVariantsPreview() {
    VoxcinaTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Dark color with white checkmark
            ColorSelectorButton(
                colorHex = "#1A1A1A",
                isSelected = true,
                isEnabled = true,
                onClick = {}
            )
            
            // Light color with black checkmark
            ColorSelectorButton(
                colorHex = "#FFFFFF",
                isSelected = true,
                isEnabled = true,
                onClick = {}
            )
            
            // Yellow color
            ColorSelectorButton(
                colorHex = "#FFEB3B",
                isSelected = true,
                isEnabled = true,
                onClick = {}
            )
        }
    }
}
