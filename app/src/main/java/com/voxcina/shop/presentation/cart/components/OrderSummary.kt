package com.voxcina.shop.presentation.cart.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.voxcina.shop.domain.model.CartSummary
import com.voxcina.shop.ui.components.SoftShadowCard
import com.voxcina.shop.ui.theme.Destructive
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.VoxcinaTheme
import com.voxcina.shop.util.PersianDigitConverter

/**
 * Order summary component displaying price breakdown.
 * Shows items price, discount, shipping, and total payable amount.
 *
 * @param summary Cart summary with pricing breakdown
 * @param itemCount Number of items in cart
 * @param discountPercentage Optional discount percentage to display
 * @param isCartPage If true, shows only subtotal with "مجموع سبد خرید" label
 * @param modifier Modifier for the component
 */
@Composable
fun OrderSummary(
    summary: CartSummary,
    itemCount: Int,
    discountPercentage: Int? = null,
    isCartPage: Boolean = false,
    modifier: Modifier = Modifier
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        SoftShadowCard(
            modifier = modifier.fillMaxWidth(),
            cornerRadius = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (isCartPage) {
                    // Cart page: show only subtotal
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "مجموع سبد خرید",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Primary
                        )
                        Text(
                            text = "${PersianDigitConverter.formatPrice(summary.subtotal)} تومان",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = Primary
                        )
                    }
                } else {
                    // Checkout page: show full breakdown
                    // Items price
                    SummaryRow(
                        label = "قیمت کالاها (${PersianDigitConverter.toPersianDigits(itemCount.toString())})",
                        value = "${PersianDigitConverter.formatPrice(summary.subtotal)} تومان",
                        valueColor = Primary
                    )
                    
                    // Shipping cost
                    if (summary.shipping > 0) {
                        SummaryRow(
                            label = "هزینه ارسال",
                            value = "${PersianDigitConverter.formatPrice(summary.shipping)} تومان",
                            valueColor = Primary
                        )
                    }
                    
                    // Discount (if any)
                    if (summary.discount > 0) {
                        val discountText = if (discountPercentage != null) {
                            "(${PersianDigitConverter.toPersianDigits(discountPercentage.toString())}٪) ${PersianDigitConverter.formatPrice(summary.discount)} تومان"
                        } else {
                            "${PersianDigitConverter.formatPrice(summary.discount)} تومان"
                        }
                        SummaryRow(
                            label = "تخفیف کالاها",
                            value = discountText,
                            valueColor = Destructive,
                            isNegative = true
                        )
                    }
                    
                    // Divider
                    HorizontalDivider(
                        color = Color.Gray.copy(alpha = 0.2f)
                    )
                    
                    // Total payable
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "مبلغ قابل پرداخت",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Primary
                        )
                        Text(
                            text = "${PersianDigitConverter.formatPrice(summary.total)} تومان",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = Primary
                        )
                    }
                }
            }
        }
    }
}

/**
 * A single row in the order summary.
 */
@Composable
private fun SummaryRow(
    label: String,
    value: String,
    valueColor: Color,
    isNegative: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        Text(
            text = if (isNegative) "-$value" else value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = valueColor
        )
    }
}
