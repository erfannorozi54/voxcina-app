package com.voxcina.shop.presentation.recentlyviewed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.History
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.voxcina.shop.domain.model.ColorVariant
import com.voxcina.shop.domain.model.Product
import com.voxcina.shop.domain.model.RecentlyViewedProduct
import com.voxcina.shop.presentation.home.components.ProductCard
import com.voxcina.shop.ui.components.EmptyState
import com.voxcina.shop.ui.components.ScreenHeader
import com.voxcina.shop.ui.components.VoxcinaLoading
import com.voxcina.shop.ui.theme.Destructive
import com.voxcina.shop.ui.theme.SecondaryLight

@Composable
fun RecentlyViewedScreen(
    onNavigateBack: () -> Unit,
    onProductClick: (productId: String, colorHex: String) -> Unit,
    viewModel: RecentlyViewedViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SecondaryLight)
        ) {
            ScreenHeader(
                title = "بازدیدهای اخیر",
                onBackClick = onNavigateBack,
                actionIcon = if (uiState is RecentlyViewedUiState.Success) Icons.Default.Delete else null,
                actionIconTint = Destructive,
                onActionClick = { viewModel.clearAll() }
            )

            when (val state = uiState) {
                is RecentlyViewedUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        VoxcinaLoading()
                    }
                }
                is RecentlyViewedUiState.Empty -> {
                    EmptyState(
                        icon = Icons.Outlined.History,
                        title = "بازدیدی ثبت نشده",
                        subtitle = "محصولاتی که مشاهده می‌کنید اینجا نمایش داده می‌شوند"
                    )
                }
                is RecentlyViewedUiState.Success -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            items = state.products,
                            key = { "${it.productId}_${it.colorHex}_${it.viewedAt}" }
                        ) { recentProduct ->
                            ProductCard(
                                product = recentProduct.toProduct(),
                                onClick = { onProductClick(recentProduct.productId, recentProduct.colorHex) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Converts RecentlyViewedProduct to Product for use with ProductCard.
 */
private fun RecentlyViewedProduct.toProduct(): Product {
    return Product(
        productId = productId,
        name = name,
        description = null,
        price = price,
        originalPrice = null,
        brand = "",
        brandId = null,
        categoryIds = null,
        collection = null,
        isFlashSale = false,
        inStock = true,
        totalInventory = 1,
        colorVariant = ColorVariant(
            color = colorHex,
            colorName = "",
            swatchImage = null,
            images = listOf(imageUrl),
            tryOnImage = null,
            tryOnGarmentType = null,
            sizes = emptyList()
        ),
        averageRating = null,
        reviewCount = null,
        createdAt = null
    )
}
