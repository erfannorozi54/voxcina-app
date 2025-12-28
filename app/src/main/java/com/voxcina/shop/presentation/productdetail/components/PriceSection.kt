package com.voxcina.shop.presentation.productdetail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.voxcina.shop.ui.components.PriceText
import com.voxcina.shop.ui.components.SoftShadowCard
import com.voxcina.shop.ui.theme.Destructive
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.VoxcinaTheme
import com.voxcina.shop.util.PersianDigitConverter

/**
 * Price section component displaying current price, original price with strikethrough,
 * and discount badge. Uses SoftShadowCard for card container.
 *
 * Requirements: 4.1, 4.2, 4.3, 4.4
 *
 * @param currentPrice The current/discounted price
 * @param originalPrice The original price (nullable, shown with strikethrough if present)
 * @param modifier Modifier for the component
 */
@Composable
fun PriceSection(
    currentPrice: Long,
    originalPrice: Long?,
    modifier: Modifier = Modifier
) {
    val hasDiscount = originalPrice != null && originalPrice > currentPrice
    val discountPercentage = if (hasDiscount && originalPrice != null) {
        ((originalPrice - currentPrice) * 100 / originalPrice).toInt()
    } else null

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        SoftShadowCard(
            modifier = modifier.fillMaxWidth(),
            cornerRadius = 16.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Price column (right side in RTL)
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Original price with strikethrough (if discount exists)
                    if (hasDiscount && originalPrice != null) {
                        PriceText(
                            price = originalPrice,
                            priceStyle = MaterialTheme.typography.bodySmall,
                            priceColor = Color.Gray,
                            suffixStyle = MaterialTheme.typography.labelSmall,
                            suffixColor = Color.Gray,
                            isStrikethrough = true
                        )
                    }
                    
                    // Current price
                    PriceText(
                        price = currentPrice,
                        priceStyle = MaterialTheme.typography.titleLarge,
                        priceColor = Primary
                    )
                }
                
                // Discount badge (left side in RTL)
                if (discountPercentage != null && discountPercentage > 0) {
                    DiscountBadge(percentage = discountPercentage)
                }
            }
        }
    }
}

/**
 * Discount badge showing percentage off with red background.
 *
 * @param percentage The discount percentage
 * @param modifier Modifier for the component
 */
@Composable
private fun DiscountBadge(
    percentage: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                color = Destructive,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = PersianDigitConverter.toPersianDigits("$percentage%"),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "تخفیف",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFCFAF8)
@Composable
private fun PriceSectionWithDiscountPreview() {
    VoxcinaTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            PriceSection(
                currentPrice = 450000,
                originalPrice = 550000
            )
            
            PriceSection(
                currentPrice = 1200000,
                originalPrice = 1500000
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFCFAF8)
@Composable
private fun PriceSectionNoDiscountPreview() {
    VoxcinaTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // No discount
            PriceSection(
                currentPrice = 350000,
                originalPrice = null
            )
            
            // Same price (no discount shown)
            PriceSection(
                currentPrice = 500000,
                originalPrice = 500000
            )
        }
    }
}
