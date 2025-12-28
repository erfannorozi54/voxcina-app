package com.voxcina.shop.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.voxcina.shop.R
import com.voxcina.shop.domain.model.Category
import com.voxcina.shop.domain.model.HeroImage
import com.voxcina.shop.domain.model.Product
import com.voxcina.shop.domain.model.RecentlyViewedProduct
import com.voxcina.shop.presentation.home.components.BottomNavBar
import com.voxcina.shop.presentation.home.components.BottomNavDestination
import com.voxcina.shop.presentation.home.components.CategorySection
import com.voxcina.shop.presentation.home.components.CompactSectionErrorState
import com.voxcina.shop.presentation.home.components.FlashSaleSection
import com.voxcina.shop.presentation.home.components.FullScreenErrorState
import com.voxcina.shop.presentation.home.components.HeroCarousel
import com.voxcina.shop.presentation.home.components.HomeHeader
import com.voxcina.shop.presentation.home.components.RecentlyViewedSection
import com.voxcina.shop.presentation.home.components.RecommendedProductsSection
import com.voxcina.shop.presentation.home.components.SearchBar
import com.voxcina.shop.presentation.home.components.ShimmerCategorySection
import com.voxcina.shop.presentation.home.components.ShimmerFlashSaleSection
import com.voxcina.shop.presentation.home.components.ShimmerHeroCarousel
import com.voxcina.shop.presentation.home.components.ShimmerHomeContent
import com.voxcina.shop.presentation.home.components.ShimmerProductGrid
import com.voxcina.shop.presentation.home.components.ShimmerRecentlyViewedSection
import com.voxcina.shop.ui.components.GlassNotification
import com.voxcina.shop.ui.theme.Secondary
import com.voxcina.shop.ui.theme.VoxcinaTheme

/**
 * Home screen composable that integrates all sections with ViewModel.
 * Implements Requirements 7.4, 8.1 from the home screen spec.
 *
 * @param viewModel HomeViewModel instance
 * @param onCategoryClick Callback when a category is clicked
 * @param onProductClick Callback when a product is clicked
 * @param onRecentlyViewedClick Callback when a recently viewed product is clicked
 * @param onViewAllFlashSale Callback when "View All" in flash sale is clicked
 * @param onViewAllCategories Callback when "View All" in categories is clicked
 * @param onSearchClick Callback when search bar is clicked
 * @param onNotificationClick Callback when notification icon is clicked
 * @param onCartClick Callback when cart icon is clicked
 * @param onBottomNavClick Callback when bottom navigation item is clicked
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onCategoryClick: (String) -> Unit = {},
    onProductClick: (productId: String, colorHex: String) -> Unit = { _, _ -> },
    onRecentlyViewedClick: (productId: String, colorHex: String) -> Unit = { _, _ -> },
    onViewAllFlashSale: () -> Unit = {},
    onViewAllCategories: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onCartClick: () -> Unit = {},
    onBottomNavClick: (BottomNavDestination) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val notificationState by viewModel.notificationState.collectAsState()
    
    Box(modifier = Modifier.fillMaxSize()) {
        HomeScreenContent(
            uiState = uiState,
            userName = viewModel.userName,
            onRefresh = { viewModel.onEvent(HomeEvent.Refresh) },
            onRetryAll = { viewModel.onEvent(HomeEvent.RetryAll) },
            onRetrySection = { section -> viewModel.onEvent(HomeEvent.RetrySection(section)) },
            onCategoryClick = onCategoryClick,
            onProductClick = onProductClick,
            onRecentlyViewedClick = onRecentlyViewedClick,
            onViewAllFlashSale = onViewAllFlashSale,
            onViewAllCategories = onViewAllCategories,
            onSearchClick = onSearchClick,
            onNotificationClick = onNotificationClick,
            onCartClick = onCartClick,
            onBottomNavClick = onBottomNavClick,
            onAddToCart = { product, size -> 
                viewModel.onEvent(HomeEvent.AddToCart(product, size))
            }
        )
        
        GlassNotification(
            state = notificationState,
            onDismiss = { viewModel.dismissNotification() },
            durationMillis = 6000,
            modifier = Modifier
                .align(androidx.compose.ui.Alignment.TopCenter)
                .padding(top = 48.dp)
        )
    }
}

/**
 * Home screen content composable that handles different UI states.
 * Separated from HomeScreen for easier testing and preview.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    uiState: HomeUiState,
    userName: String? = null,
    onRefresh: () -> Unit,
    onRetryAll: () -> Unit,
    onRetrySection: (HomeSection) -> Unit,
    onCategoryClick: (String) -> Unit,
    onProductClick: (productId: String, colorHex: String) -> Unit,
    onRecentlyViewedClick: (productId: String, colorHex: String) -> Unit,
    onViewAllFlashSale: () -> Unit,
    onViewAllCategories: () -> Unit,
    onSearchClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onCartClick: () -> Unit,
    onBottomNavClick: (BottomNavDestination) -> Unit,
    onAddToCart: (product: Product, size: String) -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Secondary)
        ) {
            when (uiState) {
                is HomeUiState.Loading -> {
                    ShimmerHomeContent()
                }
                
                is HomeUiState.Error -> {
                    FullScreenErrorState(
                        title = stringResource(R.string.error_full_screen_title),
                        message = uiState.message,
                        onRetry = onRetryAll
                    )
                }
                
                is HomeUiState.Success -> {
                    HomeSuccessContent(
                        state = uiState,
                        userName = userName,
                        onRefresh = onRefresh,
                        onRetrySection = onRetrySection,
                        onCategoryClick = onCategoryClick,
                        onProductClick = onProductClick,
                        onRecentlyViewedClick = onRecentlyViewedClick,
                        onViewAllFlashSale = onViewAllFlashSale,
                        onViewAllCategories = onViewAllCategories,
                        onSearchClick = onSearchClick,
                        onNotificationClick = onNotificationClick,
                        onCartClick = onCartClick,
                        onAddToCart = onAddToCart
                    )
                }
            }
            
            if (uiState !is HomeUiState.Error) {
                BottomNavBar(
                    selectedDestination = BottomNavDestination.HOME,
                    cartItemCount = (uiState as? HomeUiState.Success)?.cartItemCount ?: 0,
                    onDestinationSelected = onBottomNavClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(androidx.compose.ui.Alignment.BottomCenter)
                )
            }
        }
    }
}

/**
 * Home screen success content with all sections.
 * Implements pull-to-refresh functionality (Requirement 7.4).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeSuccessContent(
    state: HomeUiState.Success,
    userName: String? = null,
    onRefresh: () -> Unit,
    onRetrySection: (HomeSection) -> Unit,
    onCategoryClick: (String) -> Unit,
    onProductClick: (productId: String, colorHex: String) -> Unit,
    onRecentlyViewedClick: (productId: String, colorHex: String) -> Unit,
    onViewAllFlashSale: () -> Unit,
    onViewAllCategories: () -> Unit,
    onSearchClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onCartClick: () -> Unit,
    onAddToCart: (product: Product, size: String) -> Unit
) {
    val pullToRefreshState = rememberPullToRefreshState()
    val scrollState = rememberScrollState()
    
    PullToRefreshBox(
        isRefreshing = state.isRefreshing,
        onRefresh = onRefresh,
        state = pullToRefreshState,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            HomeHeader(
                userName = userName,
                hasNotifications = state.notificationCount > 0,
                cartItemCount = state.cartItemCount,
                onNotificationClick = onNotificationClick,
                onCartClick = onCartClick
            )
            
            SearchBar(
                value = "",
                onValueChange = {},
                readOnly = true,
                onClick = onSearchClick
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            HeroCarouselSection(
                sectionState = state.heroImages,
                onRetry = { onRetrySection(HomeSection.HERO_IMAGES) }
            )
            
            CategorySectionContent(
                sectionState = state.categories,
                onCategoryClick = onCategoryClick,
                onViewAllClick = onViewAllCategories,
                onRetry = { onRetrySection(HomeSection.CATEGORIES) }
            )
            
            FlashSaleSectionContent(
                sectionState = state.flashSaleProducts,
                endTimeMillis = state.flashSaleEndTime,
                onProductClick = onProductClick,
                onAddToCart = onAddToCart,
                onViewAllClick = onViewAllFlashSale,
                onRetry = { onRetrySection(HomeSection.FLASH_SALE) }
            )
            
            RecommendedProductsSectionContent(
                sectionState = state.recommendedProducts,
                onProductClick = onProductClick,
                onAddToCart = onAddToCart,
                onRetry = { onRetrySection(HomeSection.RECOMMENDED_PRODUCTS) }
            )
            
            if (state.showRecentlyViewed) {
                RecentlyViewedSection(
                    products = state.recentlyViewedProducts,
                    onProductClick = onRecentlyViewedClick
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

/**
 * Hero carousel section with loading/error/success states.
 */
@Composable
private fun HeroCarouselSection(
    sectionState: HomeSectionState<List<HeroImage>>,
    onRetry: () -> Unit
) {
    when (sectionState) {
        is HomeSectionState.Loading -> {
            ShimmerHeroCarousel()
        }
        is HomeSectionState.Error -> {
            CompactSectionErrorState(
                message = sectionState.message,
                onRetry = onRetry,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
        is HomeSectionState.Success -> {
            if (sectionState.data.isNotEmpty()) {
                HeroCarousel(heroImages = sectionState.data)
            }
        }
    }
}

/**
 * Category section with loading/error/success states.
 */
@Composable
private fun CategorySectionContent(
    sectionState: HomeSectionState<List<Category>>,
    onCategoryClick: (String) -> Unit,
    onViewAllClick: () -> Unit,
    onRetry: () -> Unit
) {
    when (sectionState) {
        is HomeSectionState.Loading -> {
            ShimmerCategorySection()
        }
        is HomeSectionState.Error -> {
            CompactSectionErrorState(
                message = sectionState.message,
                onRetry = onRetry,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
        is HomeSectionState.Success -> {
            CategorySection(
                categories = sectionState.data,
                onCategoryClick = onCategoryClick,
                onViewAllClick = onViewAllClick
            )
        }
    }
}

/**
 * Flash sale section with loading/error/success states.
 * Hidden when empty (Requirement 4.7).
 */
@Composable
private fun FlashSaleSectionContent(
    sectionState: HomeSectionState<List<Product>>,
    endTimeMillis: Long,
    onProductClick: (productId: String, colorHex: String) -> Unit,
    onAddToCart: (product: Product, size: String) -> Unit,
    onViewAllClick: () -> Unit,
    onRetry: () -> Unit
) {
    when (sectionState) {
        is HomeSectionState.Loading -> {
            ShimmerFlashSaleSection(modifier = Modifier.padding(horizontal = 16.dp))
        }
        is HomeSectionState.Error -> { }
        is HomeSectionState.Success -> {
            if (sectionState.data.isNotEmpty()) {
                FlashSaleSection(
                    products = sectionState.data,
                    endTimeMillis = endTimeMillis,
                    onProductClick = onProductClick,
                    onAddToCart = onAddToCart,
                    onViewAllClick = onViewAllClick,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun RecommendedProductsSectionContent(
    sectionState: HomeSectionState<List<Product>>,
    onProductClick: (productId: String, colorHex: String) -> Unit,
    onAddToCart: (product: Product, size: String) -> Unit,
    onRetry: () -> Unit
) {
    when (sectionState) {
        is HomeSectionState.Loading -> {
            ShimmerProductGrid()
        }
        is HomeSectionState.Error -> {
            CompactSectionErrorState(
                message = sectionState.message,
                onRetry = onRetry,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
        is HomeSectionState.Success -> {
            RecommendedProductsSection(
                products = sectionState.data,
                onProductClick = onProductClick,
                onAddToCart = onAddToCart
            )
        }
    }
}

// ============ Preview Functions ============

@Preview(showBackground = true)
@Composable
private fun HomeScreenLoadingPreview() {
    VoxcinaTheme {
        HomeScreenContent(
            uiState = HomeUiState.Loading,
            onRefresh = {},
            onRetryAll = {},
            onRetrySection = {},
            onCategoryClick = {},
            onProductClick = { _, _ -> },
            onRecentlyViewedClick = { _, _ -> },
            onViewAllFlashSale = {},
            onViewAllCategories = {},
            onSearchClick = {},
            onNotificationClick = {},
            onCartClick = {},
            onBottomNavClick = {},
            onAddToCart = { _, _ -> }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenErrorPreview() {
    VoxcinaTheme {
        HomeScreenContent(
            uiState = HomeUiState.Error(
                message = "خطا در بارگذاری صفحه اصلی",
                canRetry = true
            ),
            onRefresh = {},
            onRetryAll = {},
            onRetrySection = {},
            onCategoryClick = {},
            onProductClick = { _, _ -> },
            onRecentlyViewedClick = { _, _ -> },
            onViewAllFlashSale = {},
            onViewAllCategories = {},
            onSearchClick = {},
            onNotificationClick = {},
            onCartClick = {},
            onBottomNavClick = {},
            onAddToCart = { _, _ -> }
        )
    }
}
