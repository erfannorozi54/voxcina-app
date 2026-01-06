package com.voxcina.shop.presentation.recentlyviewed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.voxcina.shop.data.local.RecentlyViewedDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecentlyViewedViewModel @Inject constructor(
    private val recentlyViewedDataSource: RecentlyViewedDataSource
) : ViewModel() {

    private val _uiState = MutableStateFlow<RecentlyViewedUiState>(RecentlyViewedUiState.Loading)
    val uiState: StateFlow<RecentlyViewedUiState> = _uiState.asStateFlow()

    init {
        loadRecentlyViewed()
    }

    fun loadRecentlyViewed() {
        viewModelScope.launch {
            _uiState.value = RecentlyViewedUiState.Loading
            val products = recentlyViewedDataSource.getRecentProducts()
            _uiState.value = if (products.isEmpty()) {
                RecentlyViewedUiState.Empty
            } else {
                RecentlyViewedUiState.Success(products)
            }
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            recentlyViewedDataSource.clearAll()
            _uiState.value = RecentlyViewedUiState.Empty
        }
    }
}
