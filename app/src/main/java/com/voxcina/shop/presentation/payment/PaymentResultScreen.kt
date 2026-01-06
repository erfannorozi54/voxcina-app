package com.voxcina.shop.presentation.payment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.SecondaryLight

@Composable
fun PaymentResultScreen(
    orderId: String,
    trackId: Long,
    isSuccess: Boolean,
    onNavigateToOrders: () -> Unit,
    onRetryPayment: () -> Unit,
    viewModel: PaymentResultViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(trackId) {
        if (trackId > 0) {
            viewModel.verifyPayment(trackId, orderId)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SecondaryLight),
        contentAlignment = Alignment.Center
    ) {
        when (val state = uiState) {
            is PaymentResultUiState.Loading -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator(color = Primary)
                    Text("در حال بررسی وضعیت پرداخت...")
                }
            }
            is PaymentResultUiState.Success -> {
                PaymentResultContent(
                    isSuccess = true,
                    orderNumber = state.orderNumber,
                    message = "پرداخت شما با موفقیت انجام شد",
                    onPrimaryAction = onNavigateToOrders,
                    primaryButtonText = "مشاهده سفارش‌ها"
                )
            }
            is PaymentResultUiState.Failed -> {
                PaymentResultContent(
                    isSuccess = false,
                    orderNumber = state.orderNumber,
                    message = state.message ?: "پرداخت ناموفق بود",
                    onPrimaryAction = onRetryPayment,
                    primaryButtonText = "تلاش مجدد",
                    onSecondaryAction = onNavigateToOrders,
                    secondaryButtonText = "بازگشت به سفارش‌ها"
                )
            }
            is PaymentResultUiState.Error -> {
                PaymentResultContent(
                    isSuccess = false,
                    orderNumber = null,
                    message = state.message,
                    onPrimaryAction = onNavigateToOrders,
                    primaryButtonText = "بازگشت"
                )
            }
        }
    }
}

@Composable
private fun PaymentResultContent(
    isSuccess: Boolean,
    orderNumber: String?,
    message: String,
    onPrimaryAction: () -> Unit,
    primaryButtonText: String,
    onSecondaryAction: (() -> Unit)? = null,
    secondaryButtonText: String? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(
                    color = if (isSuccess) Color(0xFF10B981).copy(alpha = 0.1f)
                    else Color(0xFFEF4444).copy(alpha = 0.1f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isSuccess) Icons.Default.CheckCircle else Icons.Default.Error,
                contentDescription = null,
                modifier = Modifier.size(60.dp),
                tint = if (isSuccess) Color(0xFF10B981) else Color(0xFFEF4444)
            )
        }

        // Title
        Text(
            text = if (isSuccess) "پرداخت موفق" else "پرداخت ناموفق",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = if (isSuccess) Color(0xFF10B981) else Color(0xFFEF4444)
        )

        // Message
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )

        // Order number
        if (orderNumber != null) {
            Text(
                text = "شماره سفارش: $orderNumber",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Primary button
        Button(
            onClick = onPrimaryAction,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isSuccess) Primary else Color(0xFFEF4444)
            )
        ) {
            Text(primaryButtonText)
        }

        // Secondary button
        if (onSecondaryAction != null && secondaryButtonText != null) {
            OutlinedButton(
                onClick = onSecondaryAction,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(secondaryButtonText)
            }
        }
    }
}
