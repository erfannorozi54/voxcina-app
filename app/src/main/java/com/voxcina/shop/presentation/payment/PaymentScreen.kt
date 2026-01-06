package com.voxcina.shop.presentation.payment

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.voxcina.shop.presentation.checkout.CheckoutNavigationEvent
import com.voxcina.shop.ui.components.SoftShadowCard
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.Success
import com.voxcina.shop.util.PersianDigitConverter
import kotlinx.coroutines.flow.SharedFlow

/**
 * Payment screen for Zibal gateway integration.
 * Handles payment request, redirect to Zibal, and payment verification.
 */
@Composable
fun PaymentScreen(
    orderId: String,
    amount: Long,
    mobile: String? = null,
    viewModel: PaymentViewModel = hiltViewModel(),
    onPaymentSuccess: () -> Unit = {},
    onPaymentFailed: () -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Handle navigation events
    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is PaymentNavigationEvent.RedirectToPayment -> {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.payUrl))
                    context.startActivity(intent)
                }
                is PaymentNavigationEvent.PaymentSuccess -> onPaymentSuccess()
                is PaymentNavigationEvent.PaymentFailed -> onPaymentFailed()
                is PaymentNavigationEvent.NavigateBack -> onNavigateBack()
            }
        }
    }

    // Check for callback on screen load
    LaunchedEffect(Unit) {
        val uri = (context as? android.app.Activity)?.intent?.data
        if (uri != null) {
            val trackId = uri.getQueryParameter("trackId")?.toLongOrNull()
            val success = uri.getQueryParameter("success")
            if (trackId != null) {
                viewModel.verifyPayment(trackId)
            }
        }
    }

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFFCFAF8))
        ) {
            when (uiState) {
                is PaymentUiState.Idle -> {
                    IdleState(
                        amount = amount,
                        onInitiatePayment = {
                            viewModel.requestPayment(orderId, amount, mobile)
                        }
                    )
                }
                is PaymentUiState.Loading -> {
                    LoadingState(message = "در حال ایجاد درخواست پرداخت...")
                }
                is PaymentUiState.Redirecting -> {
                    LoadingState(message = "در حال انتقال به درگاه پرداخت...")
                }
                is PaymentUiState.Verifying -> {
                    LoadingState(message = "در حال تایید پرداخت...")
                }
                is PaymentUiState.Success -> {
                    SuccessState(
                        refNumber = (uiState as PaymentUiState.Success).refNumber,
                        onContinue = onPaymentSuccess
                    )
                }
                is PaymentUiState.Error -> {
                    ErrorState(
                        message = (uiState as PaymentUiState.Error).message,
                        onRetry = { viewModel.requestPayment(orderId, amount, mobile) },
                        onBack = onNavigateBack
                    )
                }
            }
        }
    }
}

@Composable
private fun IdleState(
    amount: Long,
    onInitiatePayment: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SoftShadowCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 12.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CreditCard,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = Primary
                )

                Text(
                    text = "درگاه پرداخت زیبال",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF4F1EC))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "مبلغ پرداخت:",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                            Text(
                                text = "${PersianDigitConverter.formatPrice(amount)} تومان",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = Primary
                            )
                        }
                    }
                }

                Button(
                    onClick = onInitiatePayment,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("شروع پرداخت")
                }

                Text(
                    text = "پرداخت شما توسط درگاه امن زیبال انجام میشود. اطلاعات کارت شما محفوظ است.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun LoadingState(message: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = Primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
    }
}

@Composable
private fun SuccessState(
    refNumber: String?,
    onContinue: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SoftShadowCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 12.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Success.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = Success
                    )
                }

                Text(
                    text = "پرداخت با موفقیت انجام شد",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Success
                )

                if (!refNumber.isNullOrEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF4F1EC))
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "شماره مرجع:",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                            Text(
                                text = refNumber,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = Primary
                            )
                        }
                    }
                }

                Button(
                    onClick = onContinue,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("ادامه")
                }
            }
        }
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SoftShadowCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 12.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xFFEF4444).copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = Color(0xFFEF4444)
                    )
                }

                Text(
                    text = "خطا در پرداخت",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFEF4444)
                )

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = onRetry,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("تلاش مجدد")
                }

                Button(
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("بازگشت")
                }
            }
        }
    }
}

sealed class PaymentUiState {
    data object Idle : PaymentUiState()
    data object Loading : PaymentUiState()
    data object Redirecting : PaymentUiState()
    data object Verifying : PaymentUiState()
    data class Success(val refNumber: String? = null) : PaymentUiState()
    data class Error(val message: String) : PaymentUiState()
}

sealed class PaymentNavigationEvent {
    data class RedirectToPayment(val payUrl: String) : PaymentNavigationEvent()
    data object PaymentSuccess : PaymentNavigationEvent()
    data object PaymentFailed : PaymentNavigationEvent()
    data object NavigateBack : PaymentNavigationEvent()
}
