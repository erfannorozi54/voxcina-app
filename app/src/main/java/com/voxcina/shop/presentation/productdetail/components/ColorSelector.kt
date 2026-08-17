package com.voxcina.shop.presentation.productdetail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
    modifier: Modifier = Modifier,
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
                        onClick = { onColorSelected(colorVariant) },
                        swatchImageUrl = colorVariant.swatchImage
                    )
                }
            }
        }
    }
}