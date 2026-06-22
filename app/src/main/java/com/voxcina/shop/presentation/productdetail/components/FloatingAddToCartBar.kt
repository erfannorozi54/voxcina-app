package com.voxcina.shop.presentation.productdetail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.voxcina.shop.ui.components.GradientButton
import com.voxcina.shop.ui.components.QuantitySelector
import com.voxcina.shop.ui.theme.VoxcinaTheme

// Amber-orange gradient colors for add-to-cart button
private val AmberColor = Color(0xFFFBBF24)
private val OrangeColor = Color(0xFFF97316)

/**
 * Floating action bar for adding products to cart.
 * Contains quantity selector and add-to-cart button with amber-orange gradient.
 * 
 * Should be positioned fixed above bottom navigation (bottom = 85.dp).
 *
 * @param quantity Current quantity value
 * @param maxQuantity Maximum allowed quantity (stock limit)
 * @param onQuantityChanged Callback when quantity changes
 * @param onAddToCart Callback when add-to-cart button is clicked
 * @param isLoading Whether add-to-cart is processing
 * @param enabled Whether the add-to-cart button is enabled
 * @param modifier Modifier for the bar container
 */
@Composable
fun FloatingAddToCartBar(
    quantity: Int,
    maxQuantity: Int,
    onQuantityChanged: (Int) -> Unit,
    onAddToCart: () -> Unit,
    isLoading: Boolean,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(24.dp),
                    ambientColor = AmberColor.copy(alpha = 0.3f),
                    spotColor = OrangeColor.copy(alpha = 0.4f)
                )
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Quantity Selector
                QuantitySelector(
                    quantity = quantity,
                    onQuantityChange = onQuantityChanged,
                    minQuantity = 1,
                    maxQuantity = maxQuantity,
                    isLoading = false
                )
                
                // Add to Cart Button with amber-orange gradient
                GradientButton(
                    text = "افزودن به سبد خرید",
                    onClick = onAddToCart,
                    modifier = Modifier.weight(1f),
                    enabled = enabled,
                    isLoading = isLoading,
                    gradientColors = listOf(AmberColor, OrangeColor)
                )
            }
        }
    }
}
