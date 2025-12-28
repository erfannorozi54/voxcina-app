package com.voxcina.shop.presentation.productdetail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.voxcina.shop.domain.model.SizeVariant
import com.voxcina.shop.ui.components.SizeSelectorButton
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.VoxcinaTheme

/**
 * Size selector section for the product detail screen.
 * Displays a row of SizeSelectorButton components with a section header.
 *
 * Requirements: 6.1, 6.2, 6.3
 *
 * @param sizes List of available size variants for the selected color
 * @param selectedSize Currently selected size variant (null if none selected)
 * @param onSizeSelected Callback when a size is selected
 * @param isSizeAvailable Function to check if a size variant is available
 * @param modifier Modifier for the section container
 */
@Composable
fun SizeSelector(
    sizes: List<SizeVariant>,
    selectedSize: SizeVariant?,
    onSizeSelected: (SizeVariant) -> Unit,
    isSizeAvailable: (SizeVariant) -> Boolean,
    modifier: Modifier = Modifier
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Section header with ruler icon
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
                    text = if (selectedSize != null) {
                        "سایز انتخابی: ${selectedSize.size}"
                    } else {
                        "سایز"
                    },
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
            }
            
            // Size buttons row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(
                    items = sizes,
                    key = { it.sku }
                ) { sizeVariant ->
                    SizeSelectorButton(
                        size = sizeVariant.size,
                        isSelected = selectedSize?.sku == sizeVariant.sku,
                        isEnabled = isSizeAvailable(sizeVariant),
                        onClick = { onSizeSelected(sizeVariant) }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SizeSelectorPreview() {
    val sizes = listOf(
        SizeVariant("S", "SKU-001", 5),
        SizeVariant("M", "SKU-002", 3),
        SizeVariant("L", "SKU-003", 0),
        SizeVariant("XL", "SKU-004", 8)
    )
    
    VoxcinaTheme {
        SizeSelector(
            sizes = sizes,
            selectedSize = sizes[1],
            onSizeSelected = {},
            isSizeAvailable = { it.quantity > 0 },
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SizeSelectorNoSelectionPreview() {
    val sizes = listOf(
        SizeVariant("S", "SKU-001", 5),
        SizeVariant("M", "SKU-002", 3),
        SizeVariant("L", "SKU-003", 0),
        SizeVariant("XL", "SKU-004", 8)
    )
    
    VoxcinaTheme {
        SizeSelector(
            sizes = sizes,
            selectedSize = null,
            onSizeSelected = {},
            isSizeAvailable = { it.quantity > 0 },
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SizeSelectorAllDisabledPreview() {
    val sizes = listOf(
        SizeVariant("S", "SKU-001", 0),
        SizeVariant("M", "SKU-002", 0),
        SizeVariant("L", "SKU-003", 0)
    )
    
    VoxcinaTheme {
        SizeSelector(
            sizes = sizes,
            selectedSize = null,
            onSizeSelected = {},
            isSizeAvailable = { it.quantity > 0 },
            modifier = Modifier.padding(16.dp)
        )
    }
}
