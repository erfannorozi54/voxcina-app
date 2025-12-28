package com.voxcina.shop.presentation.productdetail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.voxcina.shop.domain.model.ColorVariant
import com.voxcina.shop.domain.model.SizeVariant
import com.voxcina.shop.ui.components.ColorSelectorButton
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.VoxcinaTheme

/**
 * Color selector section for the product detail screen.
 * Displays color buttons in a flow layout that wraps to next row.
 *
 * @param colorVariants List of available color variants
 * @param selectedColorVariant Currently selected color variant
 * @param onColorSelected Callback when a color is selected
 * @param isColorAvailable Function to check if a color variant is available
 * @param modifier Modifier for the section container
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ColorSelector(
    colorVariants: List<ColorVariant>,
    selectedColorVariant: ColorVariant,
    onColorSelected: (ColorVariant) -> Unit,
    isColorAvailable: (ColorVariant) -> Boolean,
    modifier: Modifier = Modifier
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Section header with palette icon
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "رنگ انتخابی: ${selectedColorVariant.colorName}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
            }
            
            // Color buttons in flow layout (wraps to next row)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                colorVariants.forEach { colorVariant ->
                    ColorSelectorButton(
                        colorHex = colorVariant.color,
                        isSelected = colorVariant.color == selectedColorVariant.color,
                        isEnabled = isColorAvailable(colorVariant),
                        onClick = { onColorSelected(colorVariant) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Preview(showBackground = true)
@Composable
private fun ColorSelectorPreview() {
    val colorVariants = listOf(
        ColorVariant(
            color = "#FF5733",
            colorName = "قرمز",
            images = emptyList(),
            tryOnImage = null,
            sizes = listOf(
                SizeVariant("M", "SKU-001", 5),
                SizeVariant("L", "SKU-002", 3)
            )
        ),
        ColorVariant(
            color = "#0000FF",
            colorName = "آبی",
            images = emptyList(),
            tryOnImage = null,
            sizes = listOf(
                SizeVariant("M", "SKU-003", 2),
                SizeVariant("L", "SKU-004", 0)
            )
        ),
        ColorVariant(
            color = "#00FF00",
            colorName = "سبز",
            images = emptyList(),
            tryOnImage = null,
            sizes = listOf(
                SizeVariant("M", "SKU-005", 0),
                SizeVariant("L", "SKU-006", 0)
            )
        ),
        ColorVariant(
            color = "#1A1A1A",
            colorName = "مشکی",
            images = emptyList(),
            tryOnImage = null,
            sizes = listOf(
                SizeVariant("S", "SKU-007", 10),
                SizeVariant("M", "SKU-008", 8)
            )
        ),
        ColorVariant(
            color = "#FFFFFF",
            colorName = "سفید",
            images = emptyList(),
            tryOnImage = null,
            sizes = listOf(
                SizeVariant("S", "SKU-009", 5)
            )
        ),
        ColorVariant(
            color = "#FFC0CB",
            colorName = "صورتی",
            images = emptyList(),
            tryOnImage = null,
            sizes = listOf(
                SizeVariant("M", "SKU-010", 3)
            )
        ),
        ColorVariant(
            color = "#800080",
            colorName = "بنفش",
            images = emptyList(),
            tryOnImage = null,
            sizes = listOf(
                SizeVariant("L", "SKU-011", 2)
            )
        )
    )
    
    VoxcinaTheme {
        ColorSelector(
            colorVariants = colorVariants,
            selectedColorVariant = colorVariants[0],
            onColorSelected = {},
            isColorAvailable = { colorVariant ->
                colorVariant.sizes.any { it.quantity > 0 }
            },
            modifier = Modifier.padding(16.dp)
        )
    }
}
