package com.voxcina.shop.presentation.checkout

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.voxcina.shop.domain.model.Cart
import com.voxcina.shop.domain.model.CartItem
import com.voxcina.shop.domain.model.CartProduct
import com.voxcina.shop.domain.model.CartSummary
import com.voxcina.shop.domain.model.CartVariant
import com.voxcina.shop.domain.model.PaymentMethod
import com.voxcina.shop.domain.model.ShippingMethod
import com.voxcina.shop.domain.model.UserAddress
import com.voxcina.shop.presentation.cart.components.DiscountCodeInput
import com.voxcina.shop.presentation.cart.components.OrderSummary
import com.voxcina.shop.presentation.cart.components.DiscountState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import com.voxcina.shop.ui.components.GlassNotification
import com.voxcina.shop.ui.components.NotificationState
import com.voxcina.shop.ui.components.NotificationType
import com.voxcina.shop.presentation.checkout.components.CheckoutBottomBar
import com.voxcina.shop.presentation.checkout.components.CheckoutBottomBarExpanded
import com.voxcina.shop.presentation.checkout.components.CheckoutStepper
import com.voxcina.shop.presentation.checkout.components.DeliveryMethodSelector
import com.voxcina.shop.presentation.checkout.components.PaymentMethodSelector
import com.voxcina.shop.presentation.checkout.components.ShippingAddressCard
import com.voxcina.shop.presentation.home.components.BottomNavBar
import com.voxcina.shop.presentation.home.components.BottomNavDestination
import com.voxcina.shop.ui.components.ScreenHeader
import com.voxcina.shop.ui.components.VoxcinaPrimaryButton
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.SecondaryLight
import androidx.compose.ui.platform.LocalContext
import com.voxcina.shop.ui.theme.VoxcinaTheme

/**
 * Checkout screen composable that displays the checkout flow.
 * Handles Loading, Success, and Error states.
 *
 * Requirements: 1.1, 1.2, 1.3, 1.4, 2.1, 3.1, 4.1, 5.1, 7.1, 8.1, 9.1, 10.1, 11.1
 *
 * @param viewModel CheckoutViewModel instance
 * @param onNavigateBack Callback when back button is clicked
 * @param onNavigateToAddresses Callback when address change is clicked
 * @param onNavigateToPaymentResult Callback to navigate to payment result screen
 * @param onBottomNavClick Callback when bottom navigation item is clicked
 */
@Composable
fun CheckoutScreen(
    viewModel: CheckoutViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onNavigateToAddresses: () -> Unit = {},
    onNavigateToPaymentResult: (orderId: String, trackId: String, gateway: String) -> Unit = { _, _, _ -> },
    onBottomNavClick: (BottomNavDestination) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    var notificationState by remember { mutableStateOf(NotificationState()) }
    
    // Store payment info for when user returns from browser
    var pendingPaymentOrderId by remember { mutableStateOf<String?>(null) }
    var pendingPaymentTrackId by remember { mutableStateOf<String?>(null) }
    var pendingPaymentGateway by remember { mutableStateOf<String?>(null) }

    // Collect snackbar messages
    LaunchedEffect(Unit) {
        viewModel.snackbarMessage.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    // Collect notification events for GlassNotification
    LaunchedEffect(Unit) {
        viewModel.notificationEvent.collect { message ->
            notificationState = NotificationState(
                message = message,
                type = NotificationType.Info,
                isVisible = true
            )
        }
    }

    // Collect navigation events
    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is CheckoutNavigationEvent.NavigateBack -> onNavigateBack()
                is CheckoutNavigationEvent.NavigateToAddresses -> onNavigateToAddresses()
                is CheckoutNavigationEvent.RedirectToPayment -> {
                    // Store payment info for when user returns
                    pendingPaymentOrderId = event.orderId
                    pendingPaymentTrackId = event.trackId
                    pendingPaymentGateway = (uiState as? CheckoutUiState.Success)?.selectedPaymentMethod?.let {
                        if (it == PaymentMethod.DIGIPAY) "digipay" else "zibal"
                    } ?: "zibal"
                    
                    // Open payment URL in browser
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.payUrl))
                    context.startActivity(intent)
                }
                is CheckoutNavigationEvent.PaymentSuccess -> {
                    onNavigateToPaymentResult(event.orderId, "", "zibal")
                }
            }
        }
    }
    
    // Check payment status when returning from browser
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                val orderId = pendingPaymentOrderId
                val trackId = pendingPaymentTrackId
                val gateway = pendingPaymentGateway
                if (orderId != null && trackId != null && gateway != null) {
                    pendingPaymentOrderId = null
                    pendingPaymentTrackId = null
                    pendingPaymentGateway = null
                    onNavigateToPaymentResult(orderId, trackId, gateway)
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    CheckoutScreenContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        notificationState = notificationState,
        onNotificationDismiss = {
            notificationState = notificationState.copy(isVisible = false)
        },
        onEvent = viewModel::onEvent,
        onNavigateBack = onNavigateBack,
        onNavigateToAddresses = onNavigateToAddresses,
        onBottomNavClick = onBottomNavClick
    )
}


/**
 * Checkout screen content composable that handles different UI states.
 * Separated from CheckoutScreen for easier testing and preview.
 *
 * Requirements: 1.1, 1.2, 1.3, 1.4, 2.1, 9.1, 9.2, 10.1
 */
@Composable
fun CheckoutScreenContent(
    uiState: CheckoutUiState,
    snackbarHostState: SnackbarHostState,
    notificationState: NotificationState = NotificationState(),
    onNotificationDismiss: () -> Unit = {},
    onEvent: (CheckoutEvent) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToAddresses: () -> Unit,
    onBottomNavClick: (BottomNavDestination) -> Unit
) {
    var discountCode by remember { mutableStateOf("") }
    var isOrderDetailsExpanded by remember { mutableStateOf(false) }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = SecondaryLight
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Screen Header with back button
                    // Requirements: 1.1, 1.2, 1.3, 1.4
                    ScreenHeader(
                        title = "تسویه حساب",
                        onBackClick = {
                            onEvent(CheckoutEvent.NavigateBack)
                            onNavigateBack()
                        }
                    )

                    // Checkout Stepper - Step 1 (Information)
                    // Requirements: 2.1
                    CheckoutStepper(
                        currentStep = 1,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Main content based on state (takes remaining space)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                    when (uiState) {
                        is CheckoutUiState.Loading -> {
                            // Requirements: 9.1
                            CheckoutLoadingContent()
                        }

                        is CheckoutUiState.Error -> {
                            // Requirements: 9.2
                            CheckoutErrorContent(
                                message = uiState.message,
                                canRetry = uiState.canRetry,
                                onRetry = { onEvent(CheckoutEvent.Retry) }
                            )
                        }

                        is CheckoutUiState.Success -> {
                            // Requirements: 3.1, 4.1, 5.1, 5.5, 7.1
                            CheckoutSuccessContent(
                                state = uiState,
                                discountCode = discountCode,
                                onDiscountCodeChange = { discountCode = it },
                                onEvent = onEvent,
                                onNavigateToAddresses = onNavigateToAddresses
                            )
                        }
                    }
                }

                // Bottom bar and navigation - only for Success state
                // Requirements: 8.1, 11.1, 11.2, 11.3, 11.4
                if (uiState is CheckoutUiState.Success) {
                    if (isOrderDetailsExpanded) {
                        CheckoutBottomBarExpanded(
                            subtotal = uiState.cart.summary.subtotal,
                            shippingCost = uiState.shippingCost,
                            tax = uiState.cart.summary.tax,
                            discount = uiState.appliedDiscount?.let { 
                                calculateDiscountAmount(it, uiState.cart.summary.subtotal) 
                            } ?: 0L,
                            totalAmount = uiState.totalAmount,
                            onCollapseClick = { isOrderDetailsExpanded = false },
                            onCheckoutClick = { onEvent(CheckoutEvent.ProcessCheckout) },
                            isProcessing = uiState.isProcessing,
                            enabled = uiState.canProceedToPayment
                        )
                    } else {
                        CheckoutBottomBar(
                            totalAmount = uiState.totalAmount,
                            onDetailsClick = { isOrderDetailsExpanded = true },
                            onCheckoutClick = { onEvent(CheckoutEvent.ProcessCheckout) },
                            isProcessing = uiState.isProcessing,
                            enabled = uiState.canProceedToPayment
                        )
                    }
                    }

                    // Bottom Navigation - always at bottom
                    // Requirements: 11.1, 11.2, 11.3, 11.4
                    BottomNavBar(
                        selectedDestination = BottomNavDestination.CART,
                        cartItemCount = (uiState as? CheckoutUiState.Success)?.itemCount ?: 0,
                        onDestinationSelected = onBottomNavClick,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            
                // GlassNotification overlay at top center
                GlassNotification(
                    state = notificationState,
                    onDismiss = onNotificationDismiss,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 16.dp)
                )
            }
        }
    }
}

/**
 * Helper function to calculate discount amount.
 */
private fun calculateDiscountAmount(
    discount: com.voxcina.shop.domain.model.Discount,
    subtotal: Long
): Long {
    return when (discount.type) {
        com.voxcina.shop.domain.model.DiscountType.PERCENTAGE -> {
            (subtotal * discount.value / 100).coerceAtMost(subtotal)
        }
        com.voxcina.shop.domain.model.DiscountType.FIXED -> {
            discount.value.toLong().coerceAtMost(subtotal)
        }
    }
}


/**
 * Success content with checkout sections: address, delivery, payment, discount.
 *
 * Requirements: 3.1, 4.1, 5.1, 5.5, 7.1
 */
@Composable
private fun CheckoutSuccessContent(
    state: CheckoutUiState.Success,
    discountCode: String,
    onDiscountCodeChange: (String) -> Unit,
    onEvent: (CheckoutEvent) -> Unit,
    onNavigateToAddresses: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 16.dp,
            bottom = 16.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Shipping Address Section
        // Requirements: 3.1, 3.2, 3.3, 3.5, 3.6
        item {
            SectionHeader(title = "آدرس تحویل")
            Spacer(modifier = Modifier.height(8.dp))
            ShippingAddressCard(
                address = state.selectedAddress,
                onChangeClick = {
                    onEvent(CheckoutEvent.NavigateToAddresses)
                    onNavigateToAddresses()
                }
            )
            // Show validation error for address
            state.getValidationError(CheckoutValidationFields.ADDRESS)?.let { error ->
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFEF4444),
                    modifier = Modifier.padding(top = 4.dp, start = 8.dp)
                )
            }
        }

        // Delivery Method Section
        // Requirements: 4.1, 4.2, 4.3, 4.5
        item {
            SectionHeader(title = "شیوه ارسال")
            Spacer(modifier = Modifier.height(8.dp))
            DeliveryMethodSelector(
                methods = state.shippingMethods,
                selectedMethod = state.selectedShippingMethod,
                isLoading = state.isShippingLoading,
                onMethodSelected = { method ->
                    onEvent(CheckoutEvent.SelectDeliveryMethod(method))
                }
            )
            // Show validation error for shipping method
            state.getValidationError(CheckoutValidationFields.SHIPPING_METHOD)?.let { error ->
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFEF4444),
                    modifier = Modifier.padding(top = 4.dp, start = 8.dp)
                )
            }
        }

        // Payment Method Section
        // Requirements: 5.1, 5.2, 5.3, 5.4
        item {
            SectionHeader(title = "روش پرداخت")
            Spacer(modifier = Modifier.height(8.dp))
            PaymentMethodSelector(
                selectedMethod = state.selectedPaymentMethod,
                onMethodSelected = { method ->
                    onEvent(CheckoutEvent.SelectPaymentMethod(method))
                },
                onComingSoon = {
                    onEvent(CheckoutEvent.PaymentMethodComingSoon)
                }
            )
        }

        // Order Summary Section
        item {
            Spacer(modifier = Modifier.height(8.dp))
            SectionHeader(title = "خلاصه سفارش")
            Spacer(modifier = Modifier.height(8.dp))
            val shippingCost = state.selectedShippingMethod?.price?.roundToThousand() ?: 0L
            val summaryWithShipping = state.cart.summary.copy(
                shipping = shippingCost,
                total = state.cart.summary.subtotal + shippingCost - state.cart.summary.discount
            )
            OrderSummary(
                summary = summaryWithShipping,
                itemCount = state.itemCount,
                isCartPage = false
            )
        }

        // Checkout Button
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    onEvent(CheckoutEvent.ProceedToPayment)
                },
                enabled = state.canProceedToPayment && !state.isPaymentLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(horizontal = 16.dp)
            ) {
                if (state.isPaymentLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("پرداخت")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Discount Code Section
        // Requirements: 7.1, 7.2, 7.3, 7.4, 7.5
        item {
            SectionHeader(title = "کد تخفیف")
            Spacer(modifier = Modifier.height(8.dp))
            DiscountCodeInput(
                code = if (state.discountState is CheckoutDiscountState.Applied) {
                    state.discountState.discount.code
                } else {
                    discountCode
                },
                onCodeChange = { code ->
                    onDiscountCodeChange(code)
                    onEvent(CheckoutEvent.UpdateDiscountCode(code))
                },
                onSubmit = { onEvent(CheckoutEvent.ApplyDiscount(discountCode)) },
                state = state.discountState.toComponentState()
            )
        }

        // Bottom spacing for the fixed bottom bar
        item {
            Spacer(modifier = Modifier.height(120.dp))
        }
    }
}

/**
 * Section header component for checkout sections.
 */
@Composable
private fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = Primary,
        modifier = modifier
    )
}

/**
 * Extension function to convert CheckoutDiscountState to component DiscountState.
 */
private fun CheckoutDiscountState.toComponentState(): DiscountState {
    return when (this) {
        is CheckoutDiscountState.Idle -> DiscountState.Idle
        is CheckoutDiscountState.Loading -> DiscountState.Loading
        is CheckoutDiscountState.Applied -> DiscountState.Applied(discount)
        is CheckoutDiscountState.Error -> DiscountState.Error(message)
    }
}


// ============ Loading and Error States ============

/**
 * Loading content with shimmer placeholders.
 * Requirements: 9.1
 */
@Composable
private fun CheckoutLoadingContent() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Address section shimmer
        item {
            ShimmerSectionHeader()
            Spacer(modifier = Modifier.height(8.dp))
            ShimmerAddressCard()
        }

        // Delivery method section shimmer
        item {
            ShimmerSectionHeader()
            Spacer(modifier = Modifier.height(8.dp))
            ShimmerDeliveryMethods()
        }

        // Payment method section shimmer
        item {
            ShimmerSectionHeader()
            Spacer(modifier = Modifier.height(8.dp))
            ShimmerPaymentMethods()
        }

        // Discount section shimmer
        item {
            ShimmerSectionHeader()
            Spacer(modifier = Modifier.height(8.dp))
            ShimmerDiscountInput()
        }
    }
}

/**
 * Error content with retry button.
 * Requirements: 9.2
 */
@Composable
private fun CheckoutErrorContent(
    message: String,
    canRetry: Boolean,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = Primary,
            fontWeight = FontWeight.Medium
        )

        if (canRetry) {
            Spacer(modifier = Modifier.height(16.dp))
            VoxcinaPrimaryButton(
                text = "تلاش مجدد",
                onClick = onRetry
            )
        }
    }
}

// ============ Shimmer Components for Loading State ============

/**
 * Shimmer placeholder for section header.
 */
@Composable
private fun ShimmerSectionHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.3f)
            .height(20.dp)
            .background(
                brush = shimmerBrush(),
                shape = RoundedCornerShape(4.dp)
            )
    )
}

/**
 * Shimmer placeholder for address card.
 */
@Composable
private fun ShimmerAddressCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(
                color = Color.White,
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icon placeholder
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        brush = shimmerBrush(),
                        shape = CircleShape
                    )
            )

            // Content placeholders
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(16.dp)
                        .background(
                            brush = shimmerBrush(),
                            shape = RoundedCornerShape(4.dp)
                        )
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(14.dp)
                        .background(
                            brush = shimmerBrush(),
                            shape = RoundedCornerShape(4.dp)
                        )
                )
            }
        }
    }
}

/**
 * Shimmer placeholder for delivery methods.
 */
@Composable
private fun ShimmerDeliveryMethods() {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        repeat(2) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(12.dp)
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                brush = shimmerBrush(),
                                shape = CircleShape
                            )
                    )
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                brush = shimmerBrush(),
                                shape = CircleShape
                            )
                    )
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.5f)
                                .height(14.dp)
                                .background(
                                    brush = shimmerBrush(),
                                    shape = RoundedCornerShape(4.dp)
                                )
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.3f)
                                .height(12.dp)
                                .background(
                                    brush = shimmerBrush(),
                                    shape = RoundedCornerShape(4.dp)
                                )
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.25f)
                            .height(16.dp)
                            .background(
                                brush = shimmerBrush(),
                                shape = RoundedCornerShape(4.dp)
                            )
                    )
                }
            }
        }
    }
}

/**
 * Shimmer placeholder for payment methods.
 */
@Composable
private fun ShimmerPaymentMethods() {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        repeat(3) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(90.dp)
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(12.dp)
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                brush = shimmerBrush(),
                                shape = CircleShape
                            )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(12.dp)
                            .background(
                                brush = shimmerBrush(),
                                shape = RoundedCornerShape(4.dp)
                            )
                    )
                }
            }
        }
    }
}

/**
 * Shimmer placeholder for discount input.
 */
@Composable
private fun ShimmerDiscountInput() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(
                brush = shimmerBrush(),
                shape = RoundedCornerShape(8.dp)
            )
    )
}

/**
 * Creates an animated shimmer brush effect.
 */
@Composable
private fun shimmerBrush(): Brush {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )

    return Brush.linearGradient(
        colors = listOf(
            Color(0xFFE0E0E0),
            Color(0xFFF5F5F5),
            Color(0xFFE0E0E0)
        ),
        start = Offset(translateAnim - 500f, translateAnim - 500f),
        end = Offset(translateAnim, translateAnim)
    )
}


