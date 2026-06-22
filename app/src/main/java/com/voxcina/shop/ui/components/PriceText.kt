package com.voxcina.shop.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.voxcina.shop.ui.theme.Destructive
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.VoxcinaTheme
import com.voxcina.shop.util.PersianDigitConverter

/**
 * A reusable price text component with Persian digit formatting.
 * Displays price with "تومان" suffix and optional strikethrough for original price.
 *
 * @param price The price value to display
 * @param modifier Modifier for the component
 * @param priceStyle Text style for the price (default: titleMedium bold)
 * @param priceColor Color for the price text (default: Primary)
 * @param suffixStyle Text style for the "تومان" suffix (default: bodySmall)
 * @param suffixColor Color for the suffix text (default: Gray)
 * @param showSuffix Whether to show the "تومان" suffix (default: true)
 * @param isStrikethrough Whether to show strikethrough decoration (for original prices)
 */
@Composable
fun PriceText(
    price: Long,
    modifier: Modifier = Modifier,
    priceStyle: TextStyle = MaterialTheme.typography.titleMedium,
    priceColor: Color = Primary,
    suffixStyle: TextStyle = MaterialTheme.typography.bodySmall,
    suffixColor: Color = Color.Gray,
    showSuffix: Boolean = true,
    isStrikethrough: Boolean = false
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = PersianDigitConverter.formatPrice(price),
                style = priceStyle.copy(
                    textDecoration = if (isStrikethrough) TextDecoration.LineThrough else TextDecoration.None
                ),
                fontWeight = if (isStrikethrough) FontWeight.Normal else FontWeight.Bold,
                color = if (isStrikethrough) Color.Gray else priceColor
            )
            if (showSuffix) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "تومان",
                    style = suffixStyle.copy(
                        textDecoration = if (isStrikethrough) TextDecoration.LineThrough else TextDecoration.None
                    ),
                    color = suffixColor
                )
            }
        }
    }
}

/**
 * A price display with both current and original price (with discount).
 * Shows current price prominently and original price with strikethrough.
 *
 * @param currentPrice The current/discounted price
 * @param originalPrice The original price (shown with strikethrough)
 * @param modifier Modifier for the component
 * @param currentPriceColor Color for the current price (default: Primary)
 * @param discountColor Color for discount-related elements (default: Destructive)
 */
@Composable
fun PriceWithDiscount(
    currentPrice: Long,
    originalPrice: Long,
    modifier: Modifier = Modifier,
    currentPriceColor: Color = Primary,
    discountColor: Color = Destructive
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            // Original price with strikethrough
            PriceText(
                price = originalPrice,
                priceStyle = MaterialTheme.typography.bodySmall,
                priceColor = Color.Gray,
                suffixStyle = MaterialTheme.typography.labelSmall,
                suffixColor = Color.Gray,
                isStrikethrough = true
            )
            
            // Current price
            PriceText(
                price = currentPrice,
                priceColor = currentPriceColor
            )
        }
    }
}
