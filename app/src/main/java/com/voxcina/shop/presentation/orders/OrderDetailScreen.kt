package com.voxcina.shop.presentation.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Payment
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.voxcina.shop.domain.model.Order
import com.voxcina.shop.domain.model.OrderItem
import com.voxcina.shop.domain.model.OrderStatus
import com.voxcina.shop.domain.model.PaymentStatus
import com.voxcina.shop.ui.components.PriceText
import com.voxcina.shop.ui.components.ScreenHeader
import com.voxcina.shop.ui.components.SoftShadowCard
import com.voxcina.shop.ui.components.VoxcinaLoading
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.Primary100
import com.voxcina.shop.ui.theme.SecondaryLight
import com.voxcina.shop.ui.theme.Success
import com.voxcina.shop.ui.theme.Warning
import com.voxcina.shop.util.PersianDigitConverter

@Composable
fun OrderDetailScreen(
    orderId: String,
    onNavigateBack: () -> Unit,
    viewModel: OrderDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(orderId) {
        viewModel.loadOrder(orderId)
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                ScreenHeader(
                    title = "جزئیات سفارش",
                    onBackClick = onNavigateBack
                )
            },
            containerColor = SecondaryLight
        ) { paddingValues ->
            when (val state = uiState) {
                is OrderDetailUiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        VoxcinaLoading(loadingText = "در حال بارگذاری...")
                    }
                }
                is OrderDetailUiState.Success -> {
                    OrderDetailContent(
                        order = state.order,
                        modifier = Modifier.padding(paddingValues)
                    )
                }
                is OrderDetailUiState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = state.message, color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderDetailContent(
    order: Order,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Order header with status
        OrderHeaderCard(order = order)

        // Order status timeline
        OrderStatusCard(order = order)

        // Products list
        ProductsCard(items = order.items)

        // Shipping address
        AddressCard(order = order)

        // Payment summary
        PaymentSummaryCard(order = order)
    }
}

@Composable
private fun OrderHeaderCard(order: Order) {
    SoftShadowCard(cornerRadius = 12.dp) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "سفارش ${order.orderNumber}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
                StatusBadge(status = order.status)
            }
            Spacer(modifier = Modifier.height(8.dp))
            order.jalaliCreatedAt?.let {
                Text(
                    text = "تاریخ ثبت: ${PersianDigitConverter.toPersianDigits(it)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun OrderStatusCard(order: Order) {
    SoftShadowCard(cornerRadius = 12.dp) {
        Column(modifier = Modifier.padding(16.dp)) {
            SectionHeader(icon = Icons.Outlined.LocalShipping, title = "وضعیت سفارش")
            Spacer(modifier = Modifier.height(12.dp))
            
            val statuses = listOf(
                OrderStatus.PENDING to "ثبت سفارش",
                OrderStatus.PROCESSING to "در حال پردازش",
                OrderStatus.SHIPPED to "ارسال شده",
                OrderStatus.DELIVERED to "تحویل داده شده"
            )
            
            statuses.forEachIndexed { index, (status, label) ->
                val isActive = order.status.ordinal >= status.ordinal && order.status != OrderStatus.CANCELLED
                val isCurrent = order.status == status
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(if (isActive) Success else Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isActive) {
                            Text("✓", color = Color.White, fontSize = 12.sp)
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                        color = if (isActive) Primary else Color.Gray
                    )
                }
                if (index < statuses.lastIndex) {
                    Box(
                        modifier = Modifier
                            .padding(start = 11.dp)
                            .width(2.dp)
                            .height(20.dp)
                            .background(if (order.status.ordinal > status.ordinal) Success else Color.LightGray)
                    )
                }
            }
            
            if (order.status == OrderStatus.CANCELLED) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "این سفارش لغو شده است",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Red
                )
            }
        }
    }
}

@Composable
private fun ProductsCard(items: List<OrderItem>) {
    SoftShadowCard(cornerRadius = 12.dp) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "محصولات (${PersianDigitConverter.toPersianDigits(items.size.toString())})",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            items.forEachIndexed { index, item ->
                ProductItemRow(item = item)
                if (index < items.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = Color.LightGray.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductItemRow(item: OrderItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Primary100)
        ) {
            AsyncImage(
                model = item.product.image,
                contentDescription = item.product.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.product.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(parseColor(item.variant.color))
                )
                Text(
                    text = "${item.variant.colorName} - سایز ${item.variant.size}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "تعداد: ${PersianDigitConverter.toPersianDigits(item.quantity.toString())}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                PriceText(price = item.priceAtPurchase * item.quantity)
            }
        }
    }
}

@Composable
private fun AddressCard(order: Order) {
    SoftShadowCard(cornerRadius = 12.dp) {
        Column(modifier = Modifier.padding(16.dp)) {
            SectionHeader(icon = Icons.Outlined.LocationOn, title = "آدرس تحویل")
            Spacer(modifier = Modifier.height(12.dp))
            
            order.shippingAddress.fullName.takeIf { it.isNotBlank() }?.let {
                Text(text = it, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(4.dp))
            }
            Text(
                text = order.shippingAddress.fullAddress,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            order.shippingAddress.phoneNumber?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = PersianDigitConverter.toPersianDigits(it),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun PaymentSummaryCard(order: Order) {
    SoftShadowCard(cornerRadius = 12.dp) {
        Column(modifier = Modifier.padding(16.dp)) {
            SectionHeader(icon = Icons.Outlined.Payment, title = "خلاصه پرداخت")
            Spacer(modifier = Modifier.height(12.dp))
            
            val subtotal = order.totalAmount - order.shippingCost + order.discountAmount
            
            SummaryRow("جمع محصولات", subtotal)
            SummaryRow("هزینه ارسال", order.shippingCost)
            if (order.discountAmount > 0) {
                SummaryRow("تخفیف", -order.discountAmount, isDiscount = true)
            }
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("مبلغ کل", fontWeight = FontWeight.Bold)
                PriceText(price = order.totalAmount, priceStyle = MaterialTheme.typography.titleSmall)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            PaymentStatusBadge(status = order.paymentStatus)
        }
    }
}

@Composable
private fun SectionHeader(icon: ImageVector, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = Primary, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SummaryRow(label: String, amount: Long, isDiscount: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        PriceText(
            price = amount,
            priceStyle = MaterialTheme.typography.bodySmall.copy(
                color = if (isDiscount) Success else Color.Unspecified
            )
        )
    }
}

@Composable
private fun StatusBadge(status: OrderStatus) {
    val (bgColor, textColor) = when (status) {
        OrderStatus.PENDING -> Warning.copy(alpha = 0.15f) to Warning
        OrderStatus.PROCESSING -> Primary.copy(alpha = 0.15f) to Primary
        OrderStatus.SHIPPED -> Success.copy(alpha = 0.15f) to Success
        OrderStatus.DELIVERED -> Success.copy(alpha = 0.15f) to Success
        OrderStatus.CANCELLED -> Color.Red.copy(alpha = 0.15f) to Color.Red
    }
    Box(
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text = status.displayName, style = MaterialTheme.typography.labelSmall, color = textColor, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun PaymentStatusBadge(status: PaymentStatus) {
    val (bgColor, textColor) = when (status) {
        PaymentStatus.PAID -> Success.copy(alpha = 0.15f) to Success
        PaymentStatus.PENDING -> Warning.copy(alpha = 0.15f) to Warning
        else -> Color.Red.copy(alpha = 0.15f) to Color.Red
    }
    Box(
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text = status.displayName, style = MaterialTheme.typography.labelSmall, color = textColor)
    }
}

private fun parseColor(hex: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (e: Exception) {
        Color.Gray
    }
}
