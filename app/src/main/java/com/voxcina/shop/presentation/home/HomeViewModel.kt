package com.voxcina.shop.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.voxcina.shop.data.local.RecentlyViewedDataSource
import com.voxcina.shop.data.local.TokenManager
import com.voxcina.shop.data.repository.HomeRepository
import com.voxcina.shop.domain.model.*
import com.voxcina.shop.domain.repository.CartRepository
import com.voxcina.shop.util.AppError
import com.voxcina.shop.util.HomeError
import com.voxcina.shop.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
    private val recentlyViewedDataSource: RecentlyViewedDataSource,
    private val tokenManager: TokenManager,
    private val cartRepository: CartRepository
) : ViewModel() {

    companion object {
        private const val FLASH_SALE_DURATION_MS = 24 * 60 * 60 * 1000L
    }

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    val userName: String? get() = tokenManager.getUserName()

    init {
        loadHomeData()
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.Refresh -> refresh()
            is HomeEvent.RetrySection -> retrySection(event.section)
            is HomeEvent.RetryAll -> loadHomeData()
            is HomeEvent.TrackProductView -> addToRecentlyViewed(event.product)
            is HomeEvent.AddToCart -> addToCart(event.product, event.size)
            else -> { }
        }
    }
    
    private fun addToCart(product: Product, size: String) {
        viewModelScope.launch {
            val variant = CartVariant(
                size = size,
                color = product.colorVariant.color,
                colorName = product.colorVariant.colorName,
                sku = product.colorVariant.sizes.find { it.size == size }?.sku ?: ""
            )
            cartRepository.addItem(product.productId, 1, variant)
        }
    }

    /**
     * Loads all home screen data concurrently.
     * Transitions from Loading to Success state, with individual section states.
     *
     * Requirements: 2.1, 3.1, 4.1, 5.1
     */
    fun loadHomeData() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading

            // Start all API calls concurrently
            val heroImagesDeferred = async { homeRepository.getHeroImages() }
            val categoriesDeferred = async { homeRepository.getCategories() }
            val flashSaleDeferred = async { homeRepository.getFlashSaleProducts() }
            val productsDeferred = async { homeRepository.getProducts(page = 1, limit = 20) }
            val recentlyViewedDeferred = async { recentlyViewedDataSource.getRecentProducts() }

            // Await all results
            val heroImagesResult = heroImagesDeferred.await()
            val categoriesResult = categoriesDeferred.await()
            val flashSaleResult = flashSaleDeferred.await()
            val productsResult = productsDeferred.await()
            val recentlyViewed = recentlyViewedDeferred.await()

            // Check if all critical sections failed
            if (heroImagesResult.isError && 
                categoriesResult.isError && 
                productsResult.isError) {
                _uiState.value = HomeUiState.Error(
                    message = "خطا در بارگذاری صفحه اصلی",
                    canRetry = true
                )
                return@launch
            }

            // Build success state with individual section states
            _uiState.value = HomeUiState.Success(
                heroImages = heroImagesResult.toSectionState(),
                categories = categoriesResult.toSectionState(),
                flashSaleProducts = flashSaleResult.toSectionState(),
                flashSaleEndTime = calculateFlashSaleEndTime(),
                recommendedProducts = productsResult.map { it.products }.toSectionState(),
                recentlyViewedProducts = recentlyViewed,
                isRefreshing = false
            )
        }
    }

    /**
     * Refreshes all home screen data (pull-to-refresh).
     *
     * Requirements: 7.4
     */
    fun refresh() {
        val currentState = _uiState.value
        if (currentState !is HomeUiState.Success) {
            loadHomeData()
            return
        }

        // Set refreshing state
        _uiState.update { 
            if (it is HomeUiState.Success) it.copy(isRefreshing = true) else it 
        }

        viewModelScope.launch {
            // Start all API calls concurrently
            val heroImagesDeferred = async { homeRepository.getHeroImages() }
            val categoriesDeferred = async { homeRepository.getCategories() }
            val flashSaleDeferred = async { homeRepository.getFlashSaleProducts() }
            val productsDeferred = async { homeRepository.getProducts(page = 1, limit = 20) }
            val recentlyViewedDeferred = async { recentlyViewedDataSource.getRecentProducts() }

            // Await all results
            val heroImagesResult = heroImagesDeferred.await()
            val categoriesResult = categoriesDeferred.await()
            val flashSaleResult = flashSaleDeferred.await()
            val productsResult = productsDeferred.await()
            val recentlyViewed = recentlyViewedDeferred.await()

            _uiState.update { state ->
                if (state is HomeUiState.Success) {
                    state.copy(
                        heroImages = heroImagesResult.toSectionState(),
                        categories = categoriesResult.toSectionState(),
                        flashSaleProducts = flashSaleResult.toSectionState(),
                        flashSaleEndTime = calculateFlashSaleEndTime(),
                        recommendedProducts = productsResult.map { it.products }.toSectionState(),
                        recentlyViewedProducts = recentlyViewed,
                        isRefreshing = false
                    )
                } else state
            }
        }
    }

    /**
     * Retries loading a specific section that failed.
     *
     * Requirements: 7.2
     */
    private fun retrySection(section: HomeSection) {
        val currentState = _uiState.value
        if (currentState !is HomeUiState.Success) return

        viewModelScope.launch {
            // Set section to loading
            _uiState.update { state ->
                if (state is HomeUiState.Success) {
                    when (section) {
                        HomeSection.HERO_IMAGES -> state.copy(heroImages = HomeSectionState.Loading)
                        HomeSection.CATEGORIES -> state.copy(categories = HomeSectionState.Loading)
                        HomeSection.FLASH_SALE -> state.copy(flashSaleProducts = HomeSectionState.Loading)
                        HomeSection.RECOMMENDED_PRODUCTS -> state.copy(recommendedProducts = HomeSectionState.Loading)
                    }
                } else state
            }

            // Fetch and update the specific section
            when (section) {
                HomeSection.HERO_IMAGES -> {
                    val result = homeRepository.getHeroImages()
                    _uiState.update { state ->
                        if (state is HomeUiState.Success) {
                            state.copy(heroImages = result.toSectionState())
                        } else state
                    }
                }
                HomeSection.CATEGORIES -> {
                    val result = homeRepository.getCategories()
                    _uiState.update { state ->
                        if (state is HomeUiState.Success) {
                            state.copy(categories = result.toSectionState())
                        } else state
                    }
                }
                HomeSection.FLASH_SALE -> {
                    val result = homeRepository.getFlashSaleProducts()
                    _uiState.update { state ->
                        if (state is HomeUiState.Success) {
                            state.copy(flashSaleProducts = result.toSectionState())
                        } else state
                    }
                }
                HomeSection.RECOMMENDED_PRODUCTS -> {
                    val result = homeRepository.getProducts(page = 1, limit = 20)
                    _uiState.update { state ->
                        if (state is HomeUiState.Success) {
                            state.copy(recommendedProducts = result.map { it.products }.toSectionState())
                        } else state
                    }
                }
            }
        }
    }

    /**
     * Adds a product to the recently viewed list.
     *
     * Requirements: 10.1
     */
    fun addToRecentlyViewed(product: RecentlyViewedProduct) {
        viewModelScope.launch {
            recentlyViewedDataSource.addProduct(product)
            
            // Update the UI state with the new recently viewed list
            val updatedList = recentlyViewedDataSource.getRecentProducts()
            _uiState.update { state ->
                if (state is HomeUiState.Success) {
                    state.copy(recentlyViewedProducts = updatedList)
                } else state
            }
        }
    }

    /**
     * Creates a RecentlyViewedProduct from a Product for tracking.
     */
    fun createRecentlyViewedProduct(product: Product): RecentlyViewedProduct {
        return RecentlyViewedProduct(
            productId = product.productId,
            name = product.name,
            price = product.price,
            imageUrl = product.colorVariant.images.firstOrNull() ?: "",
            colorHex = product.colorVariant.color,
            viewedAt = System.currentTimeMillis()
        )
    }

    /**
     * Calculates the flash sale end time.
     * In production, this would come from the API.
     */
    private fun calculateFlashSaleEndTime(): Long {
        return System.currentTimeMillis() + FLASH_SALE_DURATION_MS
    }

    /**
     * Maps AppError to user-friendly Persian error messages.
     */
    private fun mapErrorToMessage(error: AppError): String {
        return when (error) {
            is AppError.NetworkError -> "خطا در اتصال به سرور"
            is HomeError.HeroImagesLoadFailed -> "خطا در بارگذاری بنرها"
            is HomeError.CategoriesLoadFailed -> "خطا در بارگذاری دسته‌بندی‌ها"
            is HomeError.ProductsLoadFailed -> "خطا در بارگذاری محصولات"
            is HomeError.FlashSaleLoadFailed -> "خطا در بارگذاری پیشنهادات شگفت‌انگیز"
            is HomeError.BrandsLoadFailed -> "خطا در بارگذاری برندها"
            is AppError.ServerError -> error.message
            is AppError.UnknownError -> "خطای ناشناخته"
            else -> error.message
        }
    }

    /**
     * Extension function to convert Result to HomeSectionState.
     */
    private fun <T> Result<T>.toSectionState(): HomeSectionState<T> {
        return when (this) {
            is Result.Success -> HomeSectionState.Success(data)
            is Result.Error -> HomeSectionState.Error(mapErrorToMessage(error))
        }
    }
}
