package com.voxcina.shop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.voxcina.shop.ui.theme.Destructive
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.PrimaryDark
import com.voxcina.shop.ui.theme.VoxcinaTheme
import com.voxcina.shop.util.PersianDigitConverter

/**
 * A glassmorphism quantity selector component with deep blue buttons.
 * Features frosted glass container with blur effect.
 * When quantity is at minimum and allowDelete is true, shows delete icon instead of minus.
 * 
 * Reusable for cart, product detail, and quick add dialogs.
 *
 * @param quantity Current quantity value
 * @param onQuantityChange Callback when quantity changes (receives new quantity)
 * @param modifier Modifier for the selector container
 * @param isLoading Whether to show loading indicator instead of quantity
 * @param minQuantity Minimum allowed quantity (default: 1)
 * @param maxQuantity Maximum allowed quantity (default: 99)
 * @param allowDelete When true and quantity equals minQuantity, shows delete icon (default: false)
 * @param onDelete Callback when delete is clicked (only called when allowDelete is true and quantity is at min)
 */
@Composable
fun QuantitySelector(
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    minQuantity: Int = 1,
    maxQuantity: Int = 99,
    allowDelete: Boolean = false,
    onDelete: (() -> Unit)? = null
) {
    val showDeleteButton = allowDelete && quantity <= minQuantity
    
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(16.dp))
        ) {
            // Glassmorphism background layer
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .blur(radius = 16.dp)
            )
            
            // Content layer with glass effect
            Row(
                modifier = Modifier
                    .background(
                        color = Color.White.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Increment button - Deep Blue
                DeepBlueButton(
                    onClick = { 
                        if (quantity < maxQuantity) {
                            onQuantityChange(quantity + 1)
                        }
                    },
                    enabled = !isLoading && quantity < maxQuantity,
                    contentDescription = "افزایش تعداد",
                    buttonType = ButtonType.Increment
                )
                
                // Quantity display
                Box(
                    modifier = Modifier.width(36.dp),
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
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryDark
                        )
                    }
                }
                
                // Decrement or Delete button
                if (showDeleteButton) {
                    DeepBlueButton(
                        onClick = { onDelete?.invoke() },
                        enabled = !isLoading,
                        contentDescription = "حذف از سبد",
                        buttonType = ButtonType.Delete
                    )
                } else {
                    DeepBlueButton(
                        onClick = { 
                            if (quantity > minQuantity) {
                                onQuantityChange(quantity - 1)
                            }
                        },
                        enabled = !isLoading && quantity > minQuantity,
                        contentDescription = "کاهش تعداد",
                        buttonType = ButtonType.Decrement
                    )
                }
            }
        }
    }
}

private enum class ButtonType {
    Increment, Decrement, Delete
}

@Composable
private fun DeepBlueButton(
    onClick: () -> Unit,
    enabled: Boolean,
    contentDescription: String,
    buttonType: ButtonType
) {
    val buttonColor = when (buttonType) {
        ButtonType.Delete -> if (enabled) Destructive else Destructive.copy(alpha = 0.4f)
        else -> if (enabled) Primary else Primary.copy(alpha = 0.4f)
    }
    
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(color = buttonColor)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        when (buttonType) {
            ButtonType.Increment -> {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = contentDescription,
                    modifier = Modifier.size(20.dp),
                    tint = Color.White
                )
            }
            ButtonType.Delete -> {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = contentDescription,
                    modifier = Modifier.size(20.dp),
                    tint = Color.White
                )
            }
            ButtonType.Decrement -> {
                // Custom minus sign
                Box(
                    modifier = Modifier
                        .width(14.dp)
                        .height(2.5.dp)
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(1.dp)
                        )
                )
            }
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
private fun QuantitySelectorWithDeletePreview() {
    VoxcinaTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Shows delete button when quantity is 1
            QuantitySelector(
                quantity = 1,
                onQuantityChange = {},
                allowDelete = true,
                onDelete = {}
            )
            
            // Shows minus button when quantity > 1
            QuantitySelector(
                quantity = 2,
                onQuantityChange = {},
                allowDelete = true,
                onDelete = {}
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
