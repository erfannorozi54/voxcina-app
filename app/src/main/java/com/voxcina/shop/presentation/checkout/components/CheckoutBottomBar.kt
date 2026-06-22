package com.voxcina.shop.presentation.checkout.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.voxcina.shop.ui.components.GlassCard
import com.voxcina.shop.ui.components.GradientButton
import com.voxcina.shop.ui.components.PriceText
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.SecondaryLight
import com.voxcina.shop.ui.theme.VoxcinaTheme

/**
 * Checkout bottom bar with order summary and checkout action button.
 * Features glassmorphism styling with gradient fade above.
 *
 * Requirements: 8.1, 8.2, 8.3, 8.4, 8.8
 *
 * @param totalAmount Total payable amount
 * @param onDetailsClick Callback when details expand button is clicked
 * @param onCheckoutClick Callback when checkout button is clicked
 * @param isProcessing Whether checkout is being processed
 * @param isExpanded Whether details are expanded
 * @param enabled Whether checkout button is enabled
 * @param modifier Modifier for the bar
 */
@Composable
fun CheckoutBottomBar(
    totalAmount: Long,
    onDetailsClick: () -> Unit,
    onCheckoutClick: () -> Unit,
    isProcessing: Boolean,
    modifier: Modifier = Modifier,
    isExpanded: Boolean = false,
    enabled: Boolean = true
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(modifier = modifier.fillMaxWidth()) {
            // Gradient fade above the bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                SecondaryLight.copy(alpha = 0.8f),
                                SecondaryLight
                            )
                        )
                    )
            )

            // Glass card container
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                backgroundAlpha = 0.95f,
                borderAlpha = 0.3f,
                cornerRadius = 24.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    // Total amount row with details button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Details expand button
                        TextButton(onClick = onDetailsClick) {
                            Text(
                                text = "جزئیات",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = Primary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.ExpandMore else Icons.Default.ExpandLess,
                                contentDescription = if (isExpanded) "بستن جزئیات" else "نمایش جزئیات",
                                modifier = Modifier.size(20.dp),
                                tint = Primary
                            )
                        }

                        // Total amount
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "مبلغ قابل پرداخت:",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            PriceText(
                                price = totalAmount,
                                priceStyle = MaterialTheme.typography.titleMedium,
                                priceColor = Primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Checkout button
                    GradientButton(
                        text = "پرداخت و تکمیل خرید",
                        onClick = onCheckoutClick,
                        enabled = enabled && !isProcessing,
                        isLoading = isProcessing,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

/**
 * Extended checkout bottom bar with order breakdown details.
 */
@Composable
fun CheckoutBottomBarExpanded(
    subtotal: Long,
    shippingCost: Long,
    tax: Long,
    discount: Long,
    totalAmount: Long,
    onCollapseClick: () -> Unit,
    onCheckoutClick: () -> Unit,
    isProcessing: Boolean,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(modifier = modifier.fillMaxWidth()) {
            // Gradient fade above the bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                SecondaryLight.copy(alpha = 0.8f),
                                SecondaryLight
                            )
                        )
                    )
            )

            // Glass card container
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                backgroundAlpha = 0.95f,
                borderAlpha = 0.3f,
                cornerRadius = 24.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    // Collapse button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onCollapseClick) {
                            Text(
                                text = "بستن",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = Primary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.ExpandMore,
                                contentDescription = "بستن جزئیات",
                                modifier = Modifier.size(20.dp),
                                tint = Primary
                            )
                        }
                    }

                    // Order breakdown
                    OrderBreakdownRow(label = "جمع سبد خرید", amount = subtotal)
                    OrderBreakdownRow(label = "هزینه ارسال", amount = shippingCost)
                    OrderBreakdownRow(label = "مالیات", amount = tax)
                    if (discount > 0) {
                        OrderBreakdownRow(
                            label = "تخفیف",
                            amount = -discount,
                            isDiscount = true
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Divider
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color(0xFFE5E7EB))
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Total row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "مبلغ قابل پرداخت",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Primary
                        )
                        PriceText(
                            price = totalAmount,
                            priceStyle = MaterialTheme.typography.titleMedium,
                            priceColor = Primary
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Checkout button
                    GradientButton(
                        text = "پرداخت و تکمیل خرید",
                        onClick = onCheckoutClick,
                        enabled = enabled && !isProcessing,
                        isLoading = isProcessing,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

/**
 * Single row in order breakdown showing label and amount.
 */
@Composable
private fun OrderBreakdownRow(
    label: String,
    amount: Long,
    isDiscount: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        PriceText(
            price = if (isDiscount) -amount else amount,
            priceStyle = MaterialTheme.typography.bodyMedium,
            priceColor = if (isDiscount) Color(0xFF10B981) else Primary,
            suffixStyle = MaterialTheme.typography.labelSmall
        )
    }
}
