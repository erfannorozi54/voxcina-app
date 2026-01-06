package com.voxcina.shop.presentation.payment

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
    val isRetrying by viewModel.isRetrying.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(trackId) {
        if (trackId > 0) {
            viewModel.verifyPayment(trackId, orderId)
        }
    }

    // Handle retry payment event
    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is PaymentResultEvent.RetryPayment -> {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.payUrl))
                    context.startActivity(intent)
                }
            }
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
                    type = ResultType.Success,
                    orderNumber = state.orderNumber,
                    message = "پرداخت شما با موفقیت انجام شد",
                    refNumber = state.refNumber,
                    onPrimaryAction = onNavigateToOrders,
                    primaryButtonText = "مشاهده سفارش‌ها"
                )
            }
            is PaymentResultUiState.Abandoned -> {
                PaymentResultContent(
                    type = ResultType.Abandoned,
                    orderNumber = state.orderNumber,
                    message = "پرداخت ناتمام ماند\nمی‌توانید دوباره تلاش کنید",
                    onPrimaryAction = { state.orderId?.let { viewModel.retryPayment(it) } },
                    primaryButtonText = if (isRetrying) "در حال انتقال..." else "تلاش مجدد پرداخت",
                    isPrimaryLoading = isRetrying,
                    onSecondaryAction = onNavigateToOrders,
                    secondaryButtonText = "بازگشت به سفارش‌ها"
                )
            }
            is PaymentResultUiState.Failed -> {
                PaymentResultContent(
                    type = ResultType.Failed,
                    orderNumber = state.orderNumber,
                    message = state.message ?: "پرداخت ناموفق بود",
                    onPrimaryAction = if (state.canRetry && state.orderId != null) {
                        { viewModel.retryPayment(state.orderId) }
                    } else onNavigateToOrders,
                    primaryButtonText = if (state.canRetry) {
                        if (isRetrying) "در حال انتقال..." else "تلاش مجدد"
                    } else "بازگشت به سفارش‌ها",
                    isPrimaryLoading = isRetrying,
                    onSecondaryAction = if (state.canRetry) onNavigateToOrders else null,
                    secondaryButtonText = if (state.canRetry) "بازگشت به سفارش‌ها" else null
                )
            }
            is PaymentResultUiState.Error -> {
                PaymentResultContent(
                    type = ResultType.Failed,
                    orderNumber = null,
                    message = state.message,
                    onPrimaryAction = onNavigateToOrders,
                    primaryButtonText = "بازگشت"
                )
            }
        }
    }
}

private enum class ResultType { Success, Abandoned, Failed }

@Composable
private fun PaymentResultContent(
    type: ResultType,
    orderNumber: String?,
    message: String,
    refNumber: String? = null,
    onPrimaryAction: () -> Unit,
    primaryButtonText: String,
    isPrimaryLoading: Boolean = false,
    onSecondaryAction: (() -> Unit)? = null,
    secondaryButtonText: String? = null
) {
    val iconColor = when (type) {
        ResultType.Success -> Color(0xFF10B981)
        ResultType.Abandoned -> Color(0xFFF59E0B)
        ResultType.Failed -> Color(0xFFEF4444)
    }
    val bgColor = iconColor.copy(alpha = 0.1f)
    val icon = when (type) {
        ResultType.Success -> Icons.Default.CheckCircle
        ResultType.Abandoned -> Icons.Default.Warning
        ResultType.Failed -> Icons.Default.Error
    }
    val title = when (type) {
        ResultType.Success -> "پرداخت موفق"
        ResultType.Abandoned -> "پرداخت ناتمام"
        ResultType.Failed -> "پرداخت ناموفق"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(color = bgColor, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(60.dp),
                tint = iconColor
            )
        }

        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = iconColor
        )

        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )

        if (orderNumber != null) {
            Text(
                text = "شماره سفارش: $orderNumber",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
        }

        if (refNumber != null) {
            Text(
                text = "شماره مرجع: $refNumber",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onPrimaryAction,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = !isPrimaryLoading,
            colors = ButtonDefaults.buttonColors(containerColor = iconColor)
        ) {
            if (isPrimaryLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text(primaryButtonText)
            }
        }

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
