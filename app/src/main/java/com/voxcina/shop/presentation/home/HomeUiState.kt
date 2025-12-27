package com.voxcina.shop.presentation.home

import com.voxcina.shop.domain.model.*

/**
 * Sealed class representing the state of individual sections on the home screen.
 * Each section can independently be in Loading, Success, or Error state.
 * 
 * Requirements: 7.1, 7.2
 */
sealed class HomeSectionState<out T> {
    /**
     * Section is currently loading data.
     */
    data object Loading : HomeSectionState<Nothing>()
    
    /**
     * Section data loaded successfully.
     */
    data class Success<T>(val data: T) : HomeSectionState<T>()
    
    /**
     * Section failed to load with an error message.
     */
    data class Error(val message: String) : HomeSectionState<Nothing>()
    
    val isLoading: Boolean get() = this is Loading
    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error
    
    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }
}

/**
 * Sealed class representing all possible UI states for the home screen.
 * 
 * Requirements: 7.1, 7.2, 7.3
 */
sealed class HomeUiState {
    
    /**
     * Initial loading state when the home screen first loads.
     * All sections display skeleton/shimmer placeholders.
     * 
     * Requirements: 7.1
     */
    data object Loading : HomeUiState()
    
    /**
     * Success state containing all section data.
     * Individual sections may still have their own loading/error states.
     * 
     * Requirements: 7.1, 7.2, 7.4
     */
    data class Success(
        val heroImages: HomeSectionState<List<HeroImage>> = HomeSectionState.Loading,
        val categories: HomeSectionState<List<Category>> = HomeSectionState.Loading,
        val flashSaleProducts: HomeSectionState<List<Product>> = HomeSectionState.Loading,
        val flashSaleEndTime: Long = 0L,
        val recommendedProducts: HomeSectionState<List<Product>> = HomeSectionState.Loading,
        val recentlyViewedProducts: List<RecentlyViewedProduct> = emptyList(),
        val isRefreshing: Boolean = false,
        val notificationCount: Int = 0,
        val cartItemCount: Int = 0
    ) : HomeUiState() {
        
        /**
         * Returns true if any section is still loading.
         */
        val hasLoadingSections: Boolean
            get() = heroImages.isLoading || 
                    categories.isLoading || 
                    flashSaleProducts.isLoading || 
                    recommendedProducts.isLoading
        
        /**
         * Returns true if all sections have loaded (success or error).
         */
        val allSectionsLoaded: Boolean
            get() = !hasLoadingSections
        
        /**
         * Returns true if flash sale section should be visible.
         * Only visible when products are loaded and list is not empty.
         * 
         * Requirements: 4.7
         */
        val showFlashSale: Boolean
            get() = flashSaleProducts is HomeSectionState.Success && 
                    (flashSaleProducts as HomeSectionState.Success<List<Product>>).data.isNotEmpty()
        
        /**
         * Returns true if recently viewed section should be visible.
         * Only visible when there are recently viewed products.
         * 
         * Requirements: 10.6
         */
        val showRecentlyViewed: Boolean
            get() = recentlyViewedProducts.isNotEmpty()
    }
    
    /**
     * Error state when the entire screen fails to load.
     * Displays a full-screen error with retry option.
     * 
     * Requirements: 7.3
     */
    data class Error(
        val message: String,
        val canRetry: Boolean = true
    ) : HomeUiState()
}

/**
 * Events that can be triggered from the home screen UI.
 */
sealed class HomeEvent {
    /**
     * User triggered pull-to-refresh.
     * Requirements: 7.4
     */
    data object Refresh : HomeEvent()
    
    /**
     * User tapped retry on a section error.
     * Requirements: 7.2
     */
    data class RetrySection(val section: HomeSection) : HomeEvent()
    
    /**
     * User tapped retry on full screen error.
     * Requirements: 7.3
     */
    data object RetryAll : HomeEvent()
    
    /**
     * User tapped on a category.
     * Requirements: 3.4
     */
    data class CategoryClicked(val categoryId: String) : HomeEvent()
    
    /**
     * User tapped on a product card.
     * Requirements: 5.7
     */
    data class ProductClicked(val productId: String, val colorHex: String) : HomeEvent()
    
    /**
     * User tapped on a recently viewed product.
     * Requirements: 10.7
     */
    data class RecentlyViewedClicked(val productId: String, val colorHex: String) : HomeEvent()
    
    /**
     * User tapped "View All" in flash sale section.
     * Requirements: 4.6
     */
    data object ViewAllFlashSale : HomeEvent()
    
    /**
     * User tapped "View All" in categories section.
     * Requirements: 3.5
     */
    data object ViewAllCategories : HomeEvent()
    
    /**
     * User tapped on search bar.
     * Requirements: 1.4
     */
    data object SearchClicked : HomeEvent()
    
    /**
     * User tapped on notification icon.
     * Requirements: 1.2
     */
    data object NotificationClicked : HomeEvent()
    
    /**
     * User tapped on cart icon.
     * Requirements: 1.2
     */
    data object CartClicked : HomeEvent()
    
    /**
     * User tapped on a bottom navigation item.
     * Requirements: 6.5
     */
    data class BottomNavClicked(val destination: BottomNavDestination) : HomeEvent()
    
    /**
     * Track a product as recently viewed.
     * Requirements: 10.1
     */
    data class TrackProductView(val product: RecentlyViewedProduct) : HomeEvent()
    
    /**
     * User added a product to cart with selected size.
     */
    data class AddToCart(val product: Product, val size: String) : HomeEvent()
}

/**
 * Enum representing home screen sections for retry functionality.
 */
enum class HomeSection {
    HERO_IMAGES,
    CATEGORIES,
    FLASH_SALE,
    RECOMMENDED_PRODUCTS
}

/**
 * Enum representing bottom navigation destinations.
 */
enum class BottomNavDestination {
    HOME,
    CATEGORIES,
    CART,
    PROFILE
}
