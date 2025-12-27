package com.voxcina.shop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.VoxcinaTheme
import com.voxcina.shop.util.PersianDigitConverter

/**
 * A reusable quantity selector component with increment/decrement buttons.
 * Features gray container with white buttons and subtle shadows.
 * 
 * Reusable for cart, product detail, and quick add dialogs.
 *
 * @param quantity Current quantity value
 * @param onQuantityChange Callback when quantity changes (receives new quantity)
 * @param modifier Modifier for the selector container
 * @param isLoading Whether to show loading indicator instead of quantity
 * @param minQuantity Minimum allowed quantity (default: 1)
 * @param maxQuantity Maximum allowed quantity (default: 99)
 */
@Composable
fun QuantitySelector(
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    minQuantity: Int = 1,
    maxQuantity: Int = 99
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Row(
            modifier = modifier
                .background(
                    color = Color.Gray.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Increment button
            QuantityButton(
                onClick = { 
                    if (quantity < maxQuantity) {
                        onQuantityChange(quantity + 1)
                    }
                },
                enabled = !isLoading && quantity < maxQuantity,
                contentDescription = "افزایش تعداد",
                isIncrement = true
            )
            
            // Quantity display
            Box(
                modifier = Modifier.width(32.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = Primary
                    )
                } else {
                    Text(
                        text = PersianDigitConverter.toPersianDigits(quantity.toString()),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                }
            }
            
            // Decrement button
            QuantityButton(
                onClick = { 
                    if (quantity > minQuantity) {
                        onQuantityChange(quantity - 1)
                    }
                },
                enabled = !isLoading && quantity > minQuantity,
                contentDescription = "کاهش تعداد",
                isIncrement = false
            )
        }
    }
}

@Composable
private fun QuantityButton(
    onClick: () -> Unit,
    enabled: Boolean,
    contentDescription: String,
    isIncrement: Boolean
) {
    Box(
        modifier = Modifier
            .size(28.dp)
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(6.dp),
                ambientColor = Color.Black.copy(alpha = 0.1f),
                spotColor = Color.Black.copy(alpha = 0.1f)
            )
            .background(Color.White, RoundedCornerShape(6.dp))
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isIncrement) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = contentDescription,
                modifier = Modifier.size(16.dp),
                tint = if (enabled) Primary else Color.Gray
            )
        } else {
            // Custom minus sign using a Box
            Box(
                modifier = Modifier
                    .width(10.dp)
                    .height(2.dp)
                    .background(
                        color = if (enabled) Primary else Color.Gray,
                        shape = RoundedCornerShape(1.dp)
                    )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun QuantitySelectorPreview() {
    VoxcinaTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            QuantitySelector(
                quantity = 1,
                onQuantityChange = {}
            )
            
            QuantitySelector(
                quantity = 5,
                onQuantityChange = {}
            )
            
            QuantitySelector(
                quantity = 99,
                onQuantityChange = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun QuantitySelectorLoadingPreview() {
    VoxcinaTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            QuantitySelector(
                quantity = 3,
                onQuantityChange = {},
                isLoading = true
            )
        }
    }
}
