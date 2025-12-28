package com.voxcina.shop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.VoxcinaTheme

/**
 * A reusable chip-style size selector button.
 * Shows size text with selected and disabled states.
 * 
 * Reusable for product details, quick add dialogs, and size filters.
 *
 * Requirements: 6.1, 6.2, 6.3
 *
 * @param size Size text to display (e.g., "S", "M", "L", "XL")
 * @param isSelected Whether this size is currently selected
 * @param isEnabled Whether this size is available (not out of stock)
 * @param onClick Callback when the button is clicked
 * @param modifier Modifier for the button
 */
@Composable
fun SizeSelectorButton(
    size: String,
    isSelected: Boolean,
    isEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when {
        isSelected -> Primary
        !isEnabled -> Color.Gray.copy(alpha = 0.2f)
        else -> Color.Transparent
    }
    
    val textColor = when {
        isSelected -> Color.White
        !isEnabled -> Color.Gray
        else -> Primary
    }
    
    val borderColor = when {
        isSelected -> Primary
        !isEnabled -> Color.Gray.copy(alpha = 0.3f)
        else -> Primary.copy(alpha = 0.5f)
    }
    
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = modifier
                .widthIn(min = 48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(backgroundColor)
                .border(
                    width = 1.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable(enabled = isEnabled, onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (!isEnabled) "$size (ناموجود)" else size,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = textColor,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SizeSelectorButtonPreview() {
    VoxcinaTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Selected state
            SizeSelectorButton(
                size = "M",
                isSelected = true,
                isEnabled = true,
                onClick = {}
            )
            
            // Unselected state
            SizeSelectorButton(
                size = "L",
                isSelected = false,
                isEnabled = true,
                onClick = {}
            )
            
            // Disabled state (out of stock)
            SizeSelectorButton(
                size = "XL",
                isSelected = false,
                isEnabled = false,
                onClick = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SizeSelectorButtonRowPreview() {
    VoxcinaTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SizeSelectorButton(
                size = "S",
                isSelected = false,
                isEnabled = true,
                onClick = {}
            )
            SizeSelectorButton(
                size = "M",
                isSelected = true,
                isEnabled = true,
                onClick = {}
            )
            SizeSelectorButton(
                size = "L",
                isSelected = false,
                isEnabled = true,
                onClick = {}
            )
            SizeSelectorButton(
                size = "XL",
                isSelected = false,
                isEnabled = false,
                onClick = {}
            )
        }
    }
}
