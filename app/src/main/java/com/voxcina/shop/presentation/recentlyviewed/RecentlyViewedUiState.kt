package com.voxcina.shop.presentation.recentlyviewed

import com.voxcina.shop.domain.model.RecentlyViewedProduct

sealed class RecentlyViewedUiState {
    data object Loading : RecentlyViewedUiState()
    data class Success(val products: List<RecentlyViewedProduct>) : RecentlyViewedUiState()
    data object Empty : RecentlyViewedUiState()
}
