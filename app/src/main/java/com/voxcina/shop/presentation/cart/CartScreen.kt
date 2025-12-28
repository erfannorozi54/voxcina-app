package com.voxcina.shop.presentation.cart

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.voxcina.shop.domain.model.Cart
import com.voxcina.shop.domain.model.CartItem
import com.voxcina.shop.domain.model.CartProduct
import com.voxcina.shop.domain.model.CartSummary
import com.voxcina.shop.domain.model.CartVariant
import com.voxcina.shop.domain.model.Discount
import com.voxcina.shop.domain.model.DiscountType
import com.voxcina.shop.presentation.cart.components.CartItemCard
import com.voxcina.shop.presentation.cart.components.DiscountCodeInput
import com.voxcina.shop.presentation.cart.components.OrderSummary
import com.voxcina.shop.presentation.home.components.BottomNavBar
import com.voxcina.shop.presentation.home.components.BottomNavDestination
import com.voxcina.shop.ui.components.EmptyState
import com.voxcina.shop.ui.components.GradientButton
import com.voxcina.shop.ui.components.ScreenHeader
import com.voxcina.shop.ui.components.VoxcinaPrimaryButton
import com.voxcina.shop.ui.theme.Destructive
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.Secondary
import com.voxcina.shop.ui.theme.SecondaryLight
import com.voxcina.shop.ui.theme.VoxcinaTheme
import com.voxcina.shop.presentation.cart.DiscountState as ViewModelDiscountState

/**
 * Cart screen composable that displays the shopping cart.
 * Handles Loading, Success, Empty, and Error states.
 *
 * Requirements: 1.1, 1.2, 1.3, 2.1, 2.8, 7.1, 8.1, 9.4, 10.1, 10.2
 *
 * @param viewModel CartViewModel instance
 * @param onNavigateBack Callback when back button is clicked
 * @param onCheckout Callback when checkout button is clicked
 * @param onStartShopping Callback when start shopping button is clicked (empty state)
 * @param onBottomNavClick Callback when bottom navigation item is clicked
 */
@Composable
fun CartScreen(
    viewModel: CartViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onCheckout: () -> Unit = {},
    onStartShopping: () -> Unit = {},
    onBottomNavClick: (BottomNavDestination) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Collect snackbar messages
    LaunchedEffect(Unit) {
        viewModel.snackbarMessage.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }
    
    CartScreenContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onEvent = viewModel::onEvent,
        onNavigateBack = onNavigateBack,
        onCheckout = onCheckout,
        onStartShopping = onStartShopping,
        onBottomNavClick = onBottomNavClick,
        calculateDiscountAmount = viewModel::calculateDiscountAmount,
        getDiscountPercentage = viewModel::getDiscountPercentage
    )
}


/**
 * Cart screen content composable that handles different UI states.
 * Separated from CartScreen for easier testing and preview.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreenContent(
    uiState: CartUiState,
    snackbarHostState: SnackbarHostState,
    onEvent: (CartEvent) -> Unit,
    onNavigateBack: () -> Unit,
    onCheckout: () -> Unit,
    onStartShopping: () -> Unit,
    onBottomNavClick: (BottomNavDestination) -> Unit,
    calculateDiscountAmount: (Discount, Long) -> Long = { _, _ -> 0L },
    getDiscountPercentage: (Discount) -> Int? = { null }
) {
    var showClearCartDialog by remember { mutableStateOf(false) }
    var discountCode by remember { mutableStateOf("") }
    
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = SecondaryLight
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Screen Header with back button and clear cart action
                ScreenHeader(
                    title = "سبد خرید",
                    onBackClick = {
                        onEvent(CartEvent.NavigateBack)
                        onNavigateBack()
                    },
                    actionIcon = if (uiState is CartUiState.Success) Icons.Default.Delete else null,
                    actionIconTint = Destructive,
                    onActionClick = { showClearCartDialog = true }
                )
                
                // Main content based on state (takes remaining space)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    when (uiState) {
                        is CartUiState.Loading -> {
                            CartLoadingContent()
                        }
                        
                        is CartUiState.Empty -> {
                            EmptyState(
                                icon = Icons.Outlined.ShoppingCart,
                                title = uiState.message,
                                subtitle = "محصولات مورد علاقه خود را به سبد خرید اضافه کنید",
                                actionButtonText = "شروع خرید",
                                onActionClick = {
                                    onEvent(CartEvent.StartShopping)
                                    onStartShopping()
                                }
                            )
                        }
                        
                        is CartUiState.Error -> {
                            CartErrorContent(
                                message = uiState.message,
                                canRetry = uiState.canRetry,
                                onRetry = { onEvent(CartEvent.Retry) }
                            )
                        }
                        
                        is CartUiState.Success -> {
                            CartSuccessContent(
                                state = uiState,
                                discountCode = discountCode,
                                onDiscountCodeChange = { discountCode = it },
                                onEvent = onEvent,
                                onCheckout = onCheckout,
                                calculateDiscountAmount = calculateDiscountAmount,
                                getDiscountPercentage = getDiscountPercentage
                            )
                        }
                    }
                }
                
                // Checkout button - fixed above navbar (only for Success state)
                if (uiState is CartUiState.Success) {
                    CheckoutButtonContainer(
                        onCheckout = {
                            onEvent(CartEvent.Checkout)
                            onCheckout()
                        }
                    )
                }
                
                // Bottom Navigation - always at bottom
                BottomNavBar(
                    selectedDestination = BottomNavDestination.CART,
                    cartItemCount = (uiState as? CartUiState.Success)?.itemCount ?: 0,
                    onDestinationSelected = onBottomNavClick,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        
        // Clear cart confirmation dialog
        if (showClearCartDialog) {
            ClearCartDialog(
                onConfirm = {
                    onEvent(CartEvent.ClearCart)
                    showClearCartDialog = false
                },
                onDismiss = { showClearCartDialog = false }
            )
        }
    }
}

/**
 * Loading content with shimmer placeholders.
 * Requirements: 10.1
 */
@Composable
private fun CartLoadingContent() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(3) {
            ShimmerCartItemCard()
        }
        item {
            ShimmerDiscountInput()
        }
        item {
            ShimmerOrderSummary()
        }
    }
}

/**
 * Error content with retry button.
 * Requirements: 10.2
 */
@Composable
private fun CartErrorContent(
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


/**
 * Success content with cart items, discount input, order summary, and checkout button.
 * Requirements: 2.1, 2.8, 7.1
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CartSuccessContent(
    state: CartUiState.Success,
    discountCode: String,
    onDiscountCodeChange: (String) -> Unit,
    onEvent: (CartEvent) -> Unit,
    onCheckout: () -> Unit,
    calculateDiscountAmount: (Discount, Long) -> Long,
    getDiscountPercentage: (Discount) -> Int?
) {
    val pullToRefreshState = rememberPullToRefreshState()
    var showRemoveItemDialog by remember { mutableStateOf<CartItem?>(null) }
    
    // Get applied discount once to avoid smart cast issues
    val appliedDiscount = state.appliedDiscount
    
    // Calculate adjusted summary with applied discount
    val adjustedSummary = remember(state.cart.summary, appliedDiscount) {
        if (appliedDiscount != null) {
            val discountAmount = calculateDiscountAmount(
                appliedDiscount,
                state.cart.summary.subtotal
            )
            state.cart.summary.copy(
                discount = discountAmount,
                total = state.cart.summary.subtotal + state.cart.summary.shipping + 
                        state.cart.summary.tax - discountAmount
            )
        } else {
            state.cart.summary
        }
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        PullToRefreshBox(
            isRefreshing = state.isUpdating && state.updatingItemId == null,
            onRefresh = { onEvent(CartEvent.Refresh) },
            state = pullToRefreshState,
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 16.dp,
                    bottom = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Cart items with animations
                itemsIndexed(
                    items = state.cart.items,
                    key = { _, item -> "${item.product.id}-${item.variant.sku}" }
                ) { index, item ->
                    AnimatedCartItem(
                        item = item,
                        index = index,
                        isUpdating = state.isItemUpdating(item.product.id, item.variant.sku),
                        onQuantityChange = { newQuantity ->
                            if (newQuantity <= 0) {
                                showRemoveItemDialog = item
                            } else {
                                onEvent(
                                    CartEvent.UpdateQuantity(
                                        productId = item.product.id,
                                        variantSku = item.variant.sku,
                                        newQuantity = newQuantity
                                    )
                                )
                            }
                        },
                        onRemove = { showRemoveItemDialog = item },
                        onSaveForLater = {
                            onEvent(
                                CartEvent.SaveForLater(
                                    productId = item.product.id,
                                    variantSku = item.variant.sku
                                )
                            )
                        }
                    )
                }
                
                // Discount code input
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    DiscountCodeInput(
                        code = if (state.discountState is ViewModelDiscountState.Applied) {
                            state.discountState.discount.code
                        } else {
                            discountCode
                        },
                        onCodeChange = onDiscountCodeChange,
                        onSubmit = { onEvent(CartEvent.ApplyDiscount(discountCode)) },
                        state = state.discountState.toComponentState()
                    )
                }
                
                // Order summary
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    OrderSummary(
                        summary = adjustedSummary,
                        itemCount = state.itemCount,
                        discountPercentage = appliedDiscount?.let { getDiscountPercentage(it) }
                    )
                }
            }
        }
    }
    
    // Remove item confirmation dialog (outside Box for proper overlay)
    showRemoveItemDialog?.let { item ->
        RemoveItemDialog(
            productName = item.product.name,
            onConfirm = {
                onEvent(
                    CartEvent.RemoveItem(
                        productId = item.product.id,
                        variantSku = item.variant.sku
                    )
                )
                showRemoveItemDialog = null
            },
            onDismiss = { showRemoveItemDialog = null }
        )
    }
}

/**
 * Animated cart item with fade-in and slide-up effect on load.
 * Implements staggered animation based on item index for a cascading effect.
 * 
 * Requirements: 13.1, 13.4
 */
@Composable
private fun AnimatedCartItem(
    item: CartItem,
    index: Int,
    isUpdating: Boolean,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit,
    onSaveForLater: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    
    // Staggered animation delay based on index (50ms per item)
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(index * 50L)
        visible = true
    }
    
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ) + slideInVertically(
            initialOffsetY = { it / 2 },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ),
        // Exit animation: fade-out and slide for item removal (Requirement 13.4)
        exit = fadeOut(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ) + slideOutVertically(
            targetOffsetY = { -it / 4 },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )
    ) {
        CartItemCard(
            item = item,
            onQuantityChange = onQuantityChange,
            onRemove = onRemove,
            onSaveForLater = onSaveForLater,
            isUpdating = isUpdating
        )
    }
}

/**
 * Checkout button container - fixed at bottom above navbar.
 * Requirements: 7.1, 7.2, 7.3, 7.4, 7.6
 */
@Composable
private fun CheckoutButtonContainer(
    onCheckout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(SecondaryLight)
    ) {
        // Gradient fade from transparent to background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            SecondaryLight
                        )
                    )
                )
        )
        
        // Checkout button
        GradientButton(
            text = "ادامه فرآیند خرید",
            onClick = onCheckout,
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 8.dp)
        )
    }
}


/**
 * Clear cart confirmation dialog.
 * Requirements: 1.5
 */
@Composable
private fun ClearCartDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "خالی کردن سبد خرید",
                fontWeight = FontWeight.Bold,
                color = Primary
            )
        },
        text = {
            Text(
                text = "آیا مطمئن هستید که می‌خواهید همه محصولات را از سبد خرید حذف کنید؟",
                color = Primary
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = "بله، حذف شود",
                    color = Destructive,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "انصراف",
                    color = Primary
                )
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp)
    )
}

/**
 * Remove item confirmation dialog.
 * Requirements: 3.4
 */
@Composable
private fun RemoveItemDialog(
    productName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "حذف محصول",
                fontWeight = FontWeight.Bold,
                color = Primary
            )
        },
        text = {
            Text(
                text = "آیا می‌خواهید «$productName» را از سبد خرید حذف کنید؟",
                color = Primary
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = "بله، حذف شود",
                    color = Destructive,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "انصراف",
                    color = Primary
                )
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp)
    )
}

/**
 * Extension function to convert ViewModel DiscountState to component DiscountState.
 */
private fun ViewModelDiscountState.toComponentState(): com.voxcina.shop.presentation.cart.components.DiscountState {
    return when (this) {
        is ViewModelDiscountState.Idle -> com.voxcina.shop.presentation.cart.components.DiscountState.Idle
        is ViewModelDiscountState.Loading -> com.voxcina.shop.presentation.cart.components.DiscountState.Loading
        is ViewModelDiscountState.Applied -> com.voxcina.shop.presentation.cart.components.DiscountState.Applied(discount)
        is ViewModelDiscountState.Error -> com.voxcina.shop.presentation.cart.components.DiscountState.Error(message)
    }
}

// ============ Shimmer Components for Loading State ============

/**
 * Shimmer placeholder for cart item card.
 */
@Composable
private fun ShimmerCartItemCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .background(
                color = Color.White,
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Image placeholder
            ShimmerBox(
                modifier = Modifier
                    .size(96.dp)
                    .background(
                        brush = shimmerBrush(),
                        shape = RoundedCornerShape(8.dp)
                    )
            )
            
            // Content placeholders
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ShimmerBox(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(16.dp)
                        .background(
                            brush = shimmerBrush(),
                            shape = RoundedCornerShape(4.dp)
                        )
                )
                ShimmerBox(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(12.dp)
                        .background(
                            brush = shimmerBrush(),
                            shape = RoundedCornerShape(4.dp)
                        )
                )
                ShimmerBox(
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
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
 * Shimmer placeholder for discount input.
 */
@Composable
private fun ShimmerDiscountInput() {
    ShimmerBox(
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
 * Shimmer placeholder for order summary.
 */
@Composable
private fun ShimmerOrderSummary() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .background(
                color = Color.White,
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            repeat(4) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ShimmerBox(
                        modifier = Modifier
                            .fillMaxWidth(0.4f)
                            .height(14.dp)
                            .background(
                                brush = shimmerBrush(),
                                shape = RoundedCornerShape(4.dp)
                            )
                    )
                    ShimmerBox(
                        modifier = Modifier
                            .fillMaxWidth(0.3f)
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
}

/**
 * Basic shimmer box component.
 */
@Composable
private fun ShimmerBox(modifier: Modifier = Modifier) {
    Box(modifier = modifier)
}

/**
 * Creates an animated shimmer brush effect.
 */
@Composable
private fun shimmerBrush(): Brush {
    val transition = androidx.compose.animation.core.rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = androidx.compose.animation.core.infiniteRepeatable(
            animation = androidx.compose.animation.core.tween(
                durationMillis = 1200,
                easing = androidx.compose.animation.core.FastOutSlowInEasing
            ),
            repeatMode = androidx.compose.animation.core.RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )
    
    return Brush.linearGradient(
        colors = listOf(
            Color(0xFFE0E0E0),
            Color(0xFFF5F5F5),
            Color(0xFFE0E0E0)
        ),
        start = androidx.compose.ui.geometry.Offset(translateAnim - 500f, translateAnim - 500f),
        end = androidx.compose.ui.geometry.Offset(translateAnim, translateAnim)
    )
}


// ============ Preview Functions ============

@Preview(showBackground = true)
@Composable
private fun CartScreenLoadingPreview() {
    VoxcinaTheme {
        CartScreenContent(
            uiState = CartUiState.Loading,
            snackbarHostState = remember { SnackbarHostState() },
            onEvent = {},
            onNavigateBack = {},
            onCheckout = {},
            onStartShopping = {},
            onBottomNavClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CartScreenEmptyPreview() {
    VoxcinaTheme {
        CartScreenContent(
            uiState = CartUiState.Empty(),
            snackbarHostState = remember { SnackbarHostState() },
            onEvent = {},
            onNavigateBack = {},
            onCheckout = {},
            onStartShopping = {},
            onBottomNavClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CartScreenErrorPreview() {
    VoxcinaTheme {
        CartScreenContent(
            uiState = CartUiState.Error(
                message = "خطا در بارگذاری سبد خرید",
                canRetry = true
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onEvent = {},
            onNavigateBack = {},
            onCheckout = {},
            onStartShopping = {},
            onBottomNavClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CartScreenSuccessPreview() {
    VoxcinaTheme {
        val sampleCart = Cart(
            id = "1",
            userId = "user1",
            items = listOf(
                CartItem(
                    product = CartProduct(
                        id = "1",
                        name = "تیشرت مردانه نایکی اسپرت",
                        price = 450000,
                        originalPrice = 550000,
                        mainImages = listOf("/uploads/products/sample.jpg"),
                        colorVariants = emptyList(),
                        brand = "Nike",
                        inStock = true
                    ),
                    variant = CartVariant(
                        size = "L",
                        color = "#FF0000",
                        colorName = "قرمز",
                        sku = "SKU123"
                    ),
                    quantity = 2
                ),
                CartItem(
                    product = CartProduct(
                        id = "2",
                        name = "شلوار جین مردانه لیوایز کلاسیک",
                        price = 1250000,
                        originalPrice = null,
                        mainImages = listOf("/uploads/products/sample2.jpg"),
                        colorVariants = emptyList(),
                        brand = "Levi's",
                        inStock = true
                    ),
                    variant = CartVariant(
                        size = "32",
                        color = "#000080",
                        colorName = "سرمه‌ای",
                        sku = "SKU456"
                    ),
                    quantity = 1
                )
            ),
            summary = CartSummary(
                subtotal = 2150000,
                shipping = 150000,
                tax = 215000,
                discount = 0,
                total = 2515000
            ),
            createdAt = "2024-01-01",
            updatedAt = "2024-01-01"
        )
        
        CartScreenContent(
            uiState = CartUiState.Success(cart = sampleCart),
            snackbarHostState = remember { SnackbarHostState() },
            onEvent = {},
            onNavigateBack = {},
            onCheckout = {},
            onStartShopping = {},
            onBottomNavClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CartScreenWithDiscountPreview() {
    VoxcinaTheme {
        val sampleCart = Cart(
            id = "1",
            userId = "user1",
            items = listOf(
                CartItem(
                    product = CartProduct(
                        id = "1",
                        name = "تیشرت مردانه نایکی",
                        price = 450000,
                        originalPrice = 550000,
                        mainImages = listOf("/uploads/products/sample.jpg"),
                        colorVariants = emptyList(),
                        brand = "Nike",
                        inStock = true
                    ),
                    variant = CartVariant(
                        size = "L",
                        color = "#FF0000",
                        colorName = "قرمز",
                        sku = "SKU123"
                    ),
                    quantity = 2
                )
            ),
            summary = CartSummary(
                subtotal = 900000,
                shipping = 150000,
                tax = 90000,
                discount = 180000,
                total = 960000
            ),
            createdAt = "2024-01-01",
            updatedAt = "2024-01-01"
        )
        
        val discount = Discount(
            id = "1",
            code = "SUMMER20",
            type = DiscountType.PERCENTAGE,
            value = 20,
            minOrderAmount = 500000,
            validFrom = "2024-01-01",
            validTo = "2024-12-31",
            maxUses = 100,
            usedCount = 45
        )
        
        CartScreenContent(
            uiState = CartUiState.Success(
                cart = sampleCart,
                discountState = ViewModelDiscountState.Applied(discount)
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onEvent = {},
            onNavigateBack = {},
            onCheckout = {},
            onStartShopping = {},
            onBottomNavClick = {},
            calculateDiscountAmount = { d, subtotal -> (subtotal * d.value) / 100 },
            getDiscountPercentage = { d -> if (d.type == DiscountType.PERCENTAGE) d.value else null }
        )
    }
}
