package com.voxcina.shop.presentation.productdetail

import android.content.Intent
import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.voxcina.shop.domain.model.ColorVariant
import com.voxcina.shop.domain.model.ProductAttribute
import com.voxcina.shop.domain.model.ProductDetail
import com.voxcina.shop.domain.model.ProductReview
import com.voxcina.shop.domain.model.SizeVariant
import com.voxcina.shop.presentation.home.components.BottomNavBar
import com.voxcina.shop.presentation.home.components.BottomNavDestination
import com.voxcina.shop.presentation.home.components.ShimmerBox
import com.voxcina.shop.presentation.productdetail.components.AddReviewBottomSheet
import com.voxcina.shop.presentation.productdetail.components.ColorSelector
import com.voxcina.shop.presentation.productdetail.components.DescriptionSection
import com.voxcina.shop.presentation.productdetail.components.FloatingAddToCartBar
import com.voxcina.shop.presentation.productdetail.components.PriceSection
import com.voxcina.shop.presentation.productdetail.components.ProductHeader
import com.voxcina.shop.presentation.productdetail.components.ReviewsSection
import com.voxcina.shop.presentation.productdetail.components.SizeSelector
import com.voxcina.shop.presentation.productdetail.components.SpecificationGrid
import com.voxcina.shop.presentation.productdetail.components.TopNavigationOverlay
import com.voxcina.shop.ui.components.EmptyState
import com.voxcina.shop.ui.components.GlassNotification
import com.voxcina.shop.ui.components.ImageGalleryPager
import com.voxcina.shop.ui.components.VoxcinaLoading
import com.voxcina.shop.ui.theme.SecondaryLight
import com.voxcina.shop.ui.theme.Secondary
import com.voxcina.shop.ui.theme.VoxcinaTheme
import kotlinx.coroutines.flow.collectLatest

/**
 * Main Product Detail Screen composable.
 * Displays full product information with image gallery, pricing, variants, and add-to-cart functionality.
 *
 * Requirements: 1.1-12.3
 *
 * @param productId The product ID to display
 * @param initialColorHex Optional initial color hex to pre-select
 * @param onNavigateBack Callback when back button is pressed
 * @param onNavigateToReviews Callback when "view all reviews" is clicked
 * @param onBottomNavClick Callback when bottom navigation item is clicked
 * @param viewModel The ViewModel for this screen
 */
@Composable
fun ProductDetailScreen(
    productId: String,
    initialColorHex: String? = null,
    onNavigateBack: () -> Unit,
    onNavigateToReviews: (String) -> Unit,
    onBottomNavClick: (BottomNavDestination) -> Unit,
    viewModel: ProductDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val notificationState by viewModel.notificationState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    // Handle system back button to use same navigation as header back
    BackHandler { onNavigateBack() }

    // Handle snackbar messages
    LaunchedEffect(Unit) {
        viewModel.snackbarMessage.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    // Handle add-to-cart success
    LaunchedEffect(Unit) {
        viewModel.addToCartSuccess.collectLatest {
            // Could trigger animation or navigation here
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            BottomNavBar(
                selectedDestination = BottomNavDestination.HOME,
                cartItemCount = (uiState as? ProductDetailUiState.Success)?.cartItemCount ?: 0,
                onDestinationSelected = onBottomNavClick
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(SecondaryLight)
        ) {
            when (val state = uiState) {
                is ProductDetailUiState.Loading -> {
                    ProductDetailLoadingSkeleton()
                }

                is ProductDetailUiState.Success -> {
                    ProductDetailContent(
                        state = state,
                        onEvent = viewModel::onEvent,
                        onNavigateBack = onNavigateBack,
                        onNavigateToReviews = { onNavigateToReviews(state.product.id) },
                        onShare = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, state.product.name)
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "محصول ${state.product.name} را در وکسینا ببینید:\nhttps://voxcina.com/products/${state.product.id}"
                                )
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "اشتراک‌گذاری"))
                        }
                    )
                    
                    // Add Review Bottom Sheet
                    if (state.showAddReviewSheet) {
                        AddReviewBottomSheet(
                            onDismiss = { viewModel.onEvent(ProductDetailEvent.DismissAddReview) },
                            onSubmit = { rating, comment, isRecommended ->
                                viewModel.onEvent(ProductDetailEvent.SubmitReview(rating, comment, isRecommended))
                            },
                            isSubmitting = state.isSubmittingReview
                        )
                    }
                }

                is ProductDetailUiState.Error -> {
                    ProductDetailErrorState(
                        message = state.message,
                        canRetry = state.canRetry,
                        onRetry = { viewModel.onEvent(ProductDetailEvent.Retry) },
                        onNavigateBack = onNavigateBack
                    )
                }

                is ProductDetailUiState.NotFound -> {
                    ProductDetailNotFoundState(
                        onNavigateBack = onNavigateBack
                    )
                }
            }
            
            // Glass notification for add-to-cart feedback
            GlassNotification(
                state = notificationState,
                onDismiss = { viewModel.dismissNotification() },
                durationMillis = 3000,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 72.dp)
            )
        }
    }
}


/**
 * Main content for the product detail screen in success state.
 * Contains scrollable content with all product sections and floating add-to-cart bar.
 * TopNavigationOverlay is sticky at the top when scrolling.
 */
@Composable
private fun ProductDetailContent(
    state: ProductDetailUiState.Success,
    onEvent: (ProductDetailEvent) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToReviews: () -> Unit,
    onShare: () -> Unit
) {
    var currentImageIndex by remember { mutableIntStateOf(0) }
    val scrollState = rememberScrollState()
    
    // Detect if user has scrolled past a threshold (e.g., 100dp worth of pixels)
    val isScrolled = scrollState.value > 100

    Box(modifier = Modifier.fillMaxSize()) {
        // Scrollable content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Image Gallery (without navigation overlay - it's now sticky)
            ImageGalleryPager(
                images = state.displayImages,
                currentIndex = currentImageIndex,
                onIndexChanged = { currentImageIndex = it },
                heightFraction = 0.55f,
                showGradientOverlay = true
            )

            // Content Sheet with rounded top corners
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                    .background(SecondaryLight)
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Product Header (name, brand, rating)
                ProductHeader(
                    name = state.product.name,
                    brand = state.product.brand,
                    rating = state.product.averageRating,
                    reviewCount = state.product.reviewCount
                )

                // Price Section
                PriceSection(
                    currentPrice = state.product.price,
                    originalPrice = state.product.originalPrice
                )

                // Color Selector
                if (state.product.colorVariants.isNotEmpty()) {
                    ColorSelector(
                        colorVariants = state.product.colorVariants,
                        selectedColorVariant = state.selectedColorVariant,
                        onColorSelected = { onEvent(ProductDetailEvent.SelectColor(it)) },
                        isColorAvailable = { state.isColorAvailable(it) }
                    )
                }

                // Size Selector
                if (state.availableSizes.isNotEmpty()) {
                    SizeSelector(
                        sizes = state.availableSizes,
                        selectedSize = state.selectedSize,
                        onSizeSelected = { onEvent(ProductDetailEvent.SelectSize(it)) },
                        isSizeAvailable = { state.isSizeAvailable(it) }
                    )
                }

                // Specification Grid
                if (state.product.attributes.isNotEmpty()) {
                    SpecificationGrid(
                        attributes = state.product.attributes
                    )
                }

                // Description Section
                if (state.product.description.isNotBlank()) {
                    DescriptionSection(
                        description = state.product.description,
                        isExpanded = state.isDescriptionExpanded,
                        onExpandToggle = { onEvent(ProductDetailEvent.ToggleDescription) }
                    )
                }

                // Reviews Section
                ReviewsSection(
                    reviews = state.reviews,
                    onViewAllClick = onNavigateToReviews,
                    onAddReviewClick = { onEvent(ProductDetailEvent.ShowAddReview) },
                    isLoadingReviews = state.isLoadingReviews
                )

                // Bottom spacing for floating bar
                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        // Sticky Top Navigation Overlay - stays fixed at top when scrolling
        TopNavigationOverlay(
            onBackClick = onNavigateBack,
            onFavoriteClick = { onEvent(ProductDetailEvent.ToggleFavorite) },
            onShareClick = onShare,
            isFavorite = state.isFavorite,
            isScrolled = isScrolled,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp)
        )

        // Floating Add to Cart Bar
        FloatingAddToCartBar(
            quantity = state.quantity,
            maxQuantity = state.maxQuantity,
            onQuantityChanged = { onEvent(ProductDetailEvent.ChangeQuantity(it)) },
            onAddToCart = { onEvent(ProductDetailEvent.AddToCart) },
            isLoading = state.isAddingToCart,
            enabled = state.canAddToCart,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        )
    }
}

/**
 * Loading skeleton for the product detail screen.
 * Displays shimmer effect for image, header, price, colors, and sizes.
 *
 * Requirements: 11.1
 */
@Composable
fun ProductDetailLoadingSkeleton(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SecondaryLight)
    ) {
        // Shimmer Image Gallery
        ShimmerImageGallery()

        // Content Sheet with shimmer placeholders
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                .background(SecondaryLight)
                .padding(horizontal = 24.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Shimmer Header
            ShimmerProductHeader()

            // Shimmer Price Section
            ShimmerPriceSection()

            // Shimmer Color Selector
            ShimmerColorSelector()

            // Shimmer Size Selector
            ShimmerSizeSelector()

            // Shimmer Specifications
            ShimmerSpecifications()
        }
    }
}

/**
 * Shimmer placeholder for the image gallery.
 */
@Composable
private fun ShimmerImageGallery(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(350.dp)
            .background(Secondary),
        contentAlignment = Alignment.Center
    ) {
        VoxcinaLoading(
            size = 100.dp,
            showText = false
        )

        // Shimmer navigation buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ShimmerBox(
                modifier = Modifier
                    .clip(CircleShape)
                    .size(44.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ShimmerBox(
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(44.dp)
                )
                ShimmerBox(
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(44.dp)
                )
            }
        }

        // Shimmer pagination dots
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(4) {
                ShimmerBox(
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(8.dp)
                )
            }
        }
    }
}

/**
 * Shimmer placeholder for the product header.
 */
@Composable
private fun ShimmerProductHeader(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(24.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
            ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .height(16.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
        }

        ShimmerBox(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(12.dp))
        )
    }
}

/**
 * Shimmer placeholder for the price section.
 */
@Composable
private fun ShimmerPriceSection(
    modifier: Modifier = Modifier
) {
    ShimmerBox(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .clip(RoundedCornerShape(16.dp))
    )
}

/**
 * Shimmer placeholder for the color selector.
 */
@Composable
private fun ShimmerColorSelector(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ShimmerBox(
            modifier = Modifier
                .fillMaxWidth(0.4f)
                .height(20.dp)
                .clip(RoundedCornerShape(4.dp))
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            repeat(4) {
                ShimmerBox(
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(48.dp)
                )
            }
        }
    }
}

/**
 * Shimmer placeholder for the size selector.
 */
@Composable
private fun ShimmerSizeSelector(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ShimmerBox(
            modifier = Modifier
                .fillMaxWidth(0.3f)
                .height(20.dp)
                .clip(RoundedCornerShape(4.dp))
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(4) {
                ShimmerBox(
                    modifier = Modifier
                        .width(56.dp)
                        .height(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }
        }
    }
}

/**
 * Shimmer placeholder for the specifications grid.
 */
@Composable
private fun ShimmerSpecifications(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ShimmerBox(
            modifier = Modifier
                .fillMaxWidth(0.4f)
                .height(20.dp)
                .clip(RoundedCornerShape(4.dp))
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            repeat(3) {
                ShimmerBox(
                    modifier = Modifier
                        .weight(1f)
                        .height(100.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            }
        }
    }
}


/**
 * Error state for the product detail screen.
 * Displays error message with retry button.
 *
 * Requirements: 11.2
 *
 * @param message Error message to display
 * @param canRetry Whether retry is available
 * @param onRetry Callback when retry button is clicked
 * @param onNavigateBack Callback when back button is clicked
 */
@Composable
fun ProductDetailErrorState(
    message: String,
    canRetry: Boolean,
    onRetry: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SecondaryLight)
    ) {
        EmptyState(
            icon = Icons.Outlined.Warning,
            title = "خطا در بارگذاری",
            subtitle = message,
            actionButtonText = if (canRetry) "تلاش مجدد" else null,
            onActionClick = onRetry
        )

        // Back button at top
        TopNavigationOverlay(
            onBackClick = onNavigateBack,
            onFavoriteClick = {},
            onShareClick = {},
            isFavorite = false,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

/**
 * Not found state for the product detail screen.
 * Displays "محصول یافت نشد" message with back button.
 *
 * Requirements: 11.3
 *
 * @param onNavigateBack Callback when back button is clicked
 */
@Composable
fun ProductDetailNotFoundState(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SecondaryLight)
    ) {
        EmptyState(
            icon = Icons.Outlined.Search,
            title = "محصول یافت نشد",
            subtitle = "محصول مورد نظر شما یافت نشد یا حذف شده است",
            actionButtonText = "بازگشت",
            onActionClick = onNavigateBack
        )

        // Back button at top
        TopNavigationOverlay(
            onBackClick = onNavigateBack,
            onFavoriteClick = {},
            onShareClick = {},
            isFavorite = false,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

