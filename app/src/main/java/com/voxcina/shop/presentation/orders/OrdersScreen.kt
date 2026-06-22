package com.voxcina.shop.presentation.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.voxcina.shop.ui.components.EmptyState
import com.voxcina.shop.ui.components.PriceText
import com.voxcina.shop.ui.components.ScreenHeader
import com.voxcina.shop.ui.components.SoftShadowCard
import com.voxcina.shop.ui.components.VoxcinaLoading
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.Primary100
import com.voxcina.shop.ui.theme.SecondaryLight
import com.voxcina.shop.ui.theme.Success
import com.voxcina.shop.ui.theme.VoxcinaTheme
import com.voxcina.shop.ui.theme.Warning
import com.voxcina.shop.util.PersianDigitConverter

@Composable
fun OrdersScreen(
    onNavigateBack: () -> Unit,
    onOrderClick: (String) -> Unit = {},
    viewModel: OrdersViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                ScreenHeader(
                    title = "سفارشهای من",
                    onBackClick = onNavigateBack
                )
            },
            containerColor = SecondaryLight
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                FilterTabsRow(
                    selectedTab = selectedTab,
                    onTabSelected = { viewModel.selectTab(it) }
                )

                when (val state = uiState) {
                    is OrdersUiState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            VoxcinaLoading(loadingText = "در حال بارگذاری سفارشات...")
                        }
                    }
                    is OrdersUiState.Success -> {
                        if (state.orders.isEmpty()) {
                            EmptyState(
                                icon = Icons.Outlined.Inventory2,
                                title = if (state.hasOrders) "سفارشی با این وضعیت یافت نشد" else "هنوز سفارشی ثبت نکردهاید",
                                subtitle = if (state.hasOrders) "فیلتر دیگری را امتحان کنید" else "محصولات مورد علاقه خود را سفارش دهید"
                            )
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(state.orders, key = { it.id }) { order ->
                                    OrderCard(
                                        order = order,
                                        onClick = { onOrderClick(order.id) }
                                    )
                                }
                            }
                        }
                    }
                    is OrdersUiState.Error -> {
                        EmptyState(
                            icon = Icons.Outlined.Inventory2,
                            title = "خطا در بارگذاری",
                            subtitle = state.message,
                            actionButtonText = "تلاش مجدد",
                            onActionClick = { viewModel.loadOrders() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterTabsRow(
    selectedTab: OrderFilterTab,
    onTabSelected: (OrderFilterTab) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(OrderFilterTab.entries) { tab ->
            FilterChip(
                selected = selectedTab == tab,
                onClick = { onTabSelected(tab) },
                label = { Text(tab.label, fontSize = 13.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Primary,
                    selectedLabelColor = Color.White,
                    containerColor = Color.White,
                    labelColor = Primary
                ),
                border = FilterChipDefaults.filterChipBorder(
                    borderColor = Primary.copy(alpha = 0.3f),
                    selectedBorderColor = Primary,
                    enabled = true,
                    selected = selectedTab == tab
                )
            )
        }
    }
}

@Composable
private fun OrderCard(
    order: Order,
    onClick: () -> Unit
) {
    SoftShadowCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        cornerRadius = 12.dp
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header: Order number + Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "سفارش ${order.orderNumber}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
                OrderStatusBadge(status = order.status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Date
            order.jalaliCreatedAt?.let { date ->
                Text(
                    text = PersianDigitConverter.toPersianDigits(date),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Product items with image, name, color
            order.items.take(2).forEach { item ->
                OrderItemRow(item = item)
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            if (order.items.size > 2) {
                Text(
                    text = "+${PersianDigitConverter.toPersianDigits((order.items.size - 2).toString())} کالای دیگر",
                    style = MaterialTheme.typography.labelSmall,
                    color = Primary,
                    modifier = Modifier.padding(start = 48.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Footer: Item count + Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${PersianDigitConverter.toPersianDigits(order.productCount.toString())} کالا",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                PriceText(
                    price = order.totalAmount,
                    priceStyle = MaterialTheme.typography.titleSmall
                )
            }
        }
    }
}

@Composable
private fun OrderItemRow(item: OrderItem) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Product image
        Box(
            modifier = Modifier
                .size(40.dp)
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
        
        // Product name and variant
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.product.name,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Color dot
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(parseColor(item.variant.color))
                )
                Text(
                    text = "${item.variant.colorName} - ${item.variant.size}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
        }
        
        // Quantity
        Text(
            text = "×${PersianDigitConverter.toPersianDigits(item.quantity.toString())}",
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray
        )
    }
}

private fun parseColor(hex: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (e: Exception) {
        Color.Gray
    }
}

@Composable
private fun OrderStatusBadge(status: OrderStatus) {
    val (backgroundColor, textColor) = when (status) {
        OrderStatus.PENDING -> Warning.copy(alpha = 0.15f) to Warning
        OrderStatus.PROCESSING -> Primary.copy(alpha = 0.15f) to Primary
        OrderStatus.SHIPPED -> Success.copy(alpha = 0.15f) to Success
        OrderStatus.DELIVERED -> Success.copy(alpha = 0.15f) to Success
        OrderStatus.CANCELLED -> Color.Red.copy(alpha = 0.15f) to Color.Red
    }

    Box(
        modifier = Modifier
            .background(backgroundColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = status.displayName,
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.Medium
        )
    }
}
