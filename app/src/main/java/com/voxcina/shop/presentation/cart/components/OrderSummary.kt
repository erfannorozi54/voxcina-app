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
import androidx.compose.ui.tooling.preview.Preview
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
 * @param modifier Modifier for the component
 */
@Composable
fun OrderSummary(
    summary: CartSummary,
    itemCount: Int,
    discountPercentage: Int? = null,
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
                // Items price
                SummaryRow(
                    label = "قیمت کالاها (${PersianDigitConverter.toPersianDigits(itemCount.toString())})",
                    value = "${PersianDigitConverter.formatPrice(summary.subtotal)} تومان",
                    valueColor = Primary
                )
                
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
                
                // Shipping cost
                val shippingText = if (summary.shipping > 0) {
                    "${PersianDigitConverter.formatPrice(summary.shipping)} تومان"
                } else {
                    "رایگان"
                }
                SummaryRow(
                    label = "هزینه ارسال",
                    value = shippingText,
                    valueColor = if (summary.shipping > 0) Primary else Color(0xFF10B981)
                )
                
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

@Preview(showBackground = true, backgroundColor = 0xFFFCFAF8)
@Composable
private fun OrderSummaryPreview() {
    VoxcinaTheme {
        OrderSummary(
            summary = CartSummary(
                subtotal = 900000,
                shipping = 150000,
                tax = 90000,
                discount = 0,
                total = 1140000
            ),
            itemCount = 2,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFCFAF8)
@Composable
private fun OrderSummaryWithDiscountPreview() {
    VoxcinaTheme {
        OrderSummary(
            summary = CartSummary(
                subtotal = 1500000,
                shipping = 150000,
                tax = 135000,
                discount = 300000,
                total = 1485000
            ),
            itemCount = 3,
            discountPercentage = 20,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFCFAF8)
@Composable
private fun OrderSummaryFreeShippingPreview() {
    VoxcinaTheme {
        OrderSummary(
            summary = CartSummary(
                subtotal = 2500000,
                shipping = 0,
                tax = 250000,
                discount = 500000,
                total = 2250000
            ),
            itemCount = 5,
            discountPercentage = 20,
            modifier = Modifier.padding(16.dp)
        )
    }
}
