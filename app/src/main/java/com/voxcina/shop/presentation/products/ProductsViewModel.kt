package com.voxcina.shop.presentation.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.voxcina.shop.domain.model.Brand
import com.voxcina.shop.domain.model.Category
import com.voxcina.shop.domain.repository.ProductsListRepository
import com.voxcina.shop.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val repository: ProductsListRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductsUiState())
    val uiState: StateFlow<ProductsUiState> = _uiState.asStateFlow()

    init {
        loadFilters()
        loadProducts()
    }

    private fun loadFilters() {
        viewModelScope.launch {
            _uiState.update { it.copy(isFiltersLoading = true) }
            
            val categoriesResult = repository.getCategories()
            val brandsResult = repository.getBrands()
            
            _uiState.update { state ->
                state.copy(
                    isFiltersLoading = false,
                    categories = (categoriesResult as? Result.Success)?.data ?: emptyList(),
                    brands = (brandsResult as? Result.Success)?.data ?: emptyList()
                )
            }
        }
    }

    fun loadProducts(reset: Boolean = true) {
        viewModelScope.launch {
            val state = _uiState.value
            if (reset) {
                _uiState.update { it.copy(isLoading = true, error = null, currentPage = 1) }
            } else {
                _uiState.update { it.copy(isLoadingMore = true) }
            }

            val page = if (reset) 1 else state.currentPage + 1
            val result = repository.getProducts(
                page = page,
                sort = state.selectedSort.apiValue,
                categoryId = state.selectedCategory?.id,
                brandId = state.selectedBrand?.id,
                inStock = if (state.inStockOnly) true else null,
                isFlashSale = if (state.flashSaleOnly) true else null,
                search = state.searchQuery.takeIf { it.isNotBlank() }
            )

            when (result) {
                is Result.Success -> {
                    val data = result.data
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isLoadingMore = false,
                            products = if (reset) data.products else it.products + data.products,
                            currentPage = data.currentPage,
                            totalPages = data.totalPages,
                            hasMore = data.nextPage != null
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isLoadingMore = false,
                            error = result.error.message
                        )
                    }
                }
            }
        }
    }

    fun loadMore() {
        val state = _uiState.value
        if (!state.isLoadingMore && state.hasMore) {
            loadProducts(reset = false)
        }
    }

    fun setSort(sort: SortOption) {
        _uiState.update { it.copy(selectedSort = sort) }
        loadProducts()
    }

    fun setCategory(category: Category?) {
        _uiState.update { it.copy(selectedCategory = category) }
        loadProducts()
    }

    fun setBrand(brand: Brand?) {
        _uiState.update { it.copy(selectedBrand = brand) }
        loadProducts()
    }

    fun setInStockOnly(inStock: Boolean) {
        _uiState.update { it.copy(inStockOnly = inStock) }
        loadProducts()
    }

    fun setFlashSaleOnly(flashSale: Boolean) {
        _uiState.update { it.copy(flashSaleOnly = flashSale) }
        loadProducts()
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun search() {
        loadProducts()
    }

    fun setInitialCategory(categoryId: String) {
        viewModelScope.launch {
            // Wait for categories to load, then find and set the category
            val categories = _uiState.value.categories.ifEmpty {
                (repository.getCategories() as? Result.Success)?.data ?: emptyList()
            }
            val category = categories.find { it.id == categoryId }
            if (category != null && _uiState.value.selectedCategory?.id != categoryId) {
                _uiState.update { it.copy(selectedCategory = category, categories = categories) }
                loadProducts()
            }
        }
    }

    fun clearFilters() {
        _uiState.update {
            it.copy(
                selectedSort = SortOption.NEWEST,
                selectedCategory = null,
                selectedBrand = null,
                inStockOnly = false,
                flashSaleOnly = false,
                searchQuery = ""
            )
        }
        loadProducts()
    }

    fun toggleFilterSheet() {
        _uiState.update { it.copy(showFilterSheet = !it.showFilterSheet) }
    }

    fun dismissFilterSheet() {
        _uiState.update { it.copy(showFilterSheet = false) }
    }
}
