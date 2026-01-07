package com.voxcina.shop.presentation.products

import com.voxcina.shop.domain.model.Brand
import com.voxcina.shop.domain.model.Category
import com.voxcina.shop.domain.model.Product

data class ProductsUiState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val currentPage: Int = 1,
    val totalPages: Int = 1,
    val hasMore: Boolean = false,
    
    // Filters
    val selectedSort: SortOption = SortOption.NEWEST,
    val selectedCategory: Category? = null,
    val selectedBrand: Brand? = null,
    val inStockOnly: Boolean = false,
    val flashSaleOnly: Boolean = false,
    val searchQuery: String = "",
    
    // Filter options
    val categories: List<Category> = emptyList(),
    val brands: List<Brand> = emptyList(),
    val isFiltersLoading: Boolean = false,
    
    // Filter sheet
    val showFilterSheet: Boolean = false
)

enum class SortOption(val apiValue: String, val displayName: String) {
    NEWEST("newest", "جدیدترین"),
    PRICE_LOW_TO_HIGH("price-asc", "ارزان‌ترین"),
    PRICE_HIGH_TO_LOW("price-desc", "گران‌ترین"),
    POPULAR("popular", "محبوب‌ترین"),
    DISCOUNT("discount", "بیشترین تخفیف")
}
