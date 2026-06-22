package com.voxcina.shop.presentation.products

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.voxcina.shop.domain.model.Brand
import com.voxcina.shop.domain.model.Category
import com.voxcina.shop.presentation.home.components.BottomNavBar
import com.voxcina.shop.presentation.home.components.BottomNavDestination
import com.voxcina.shop.presentation.home.components.ProductCard
import com.voxcina.shop.ui.components.EmptyState
import com.voxcina.shop.ui.components.SearchTextField
import com.voxcina.shop.ui.components.VoxcinaLoading
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.SecondaryLight
import kotlinx.coroutines.flow.distinctUntilChanged

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    onProductClick: (productId: String, colorHex: String) -> Unit,
    onBottomNavClick: (BottomNavDestination) -> Unit,
    initialCategoryId: String? = null,
    cartItemCount: Int = 0,
    viewModel: ProductsViewModel = hiltViewModel()
) {
    // Set initial category filter
    LaunchedEffect(initialCategoryId) {
        initialCategoryId?.let { viewModel.setInitialCategory(it) }
    }
    
    val uiState by viewModel.uiState.collectAsState()
    val gridState = rememberLazyGridState()
    val sheetState = rememberModalBottomSheetState()

    // Load more when reaching end
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleItem = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisibleItem >= gridState.layoutInfo.totalItemsCount - 4
        }
    }

    LaunchedEffect(shouldLoadMore) {
        snapshotFlow { shouldLoadMore }
            .distinctUntilChanged()
            .collect { if (it) viewModel.loadMore() }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SecondaryLight)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header with search
                ProductsHeader(
                    searchQuery = uiState.searchQuery,
                    onSearchChange = viewModel::setSearchQuery,
                    onSearch = viewModel::search,
                    activeFiltersCount = countActiveFilters(uiState),
                    onFilterClick = viewModel::toggleFilterSheet
                )

                // Sort chips
                SortChipsRow(
                    selectedSort = uiState.selectedSort,
                    onSortSelected = viewModel::setSort
                )

                // Active filters chips
                if (hasActiveFilters(uiState)) {
                    ActiveFiltersRow(
                        uiState = uiState,
                        onClearCategory = { viewModel.setCategory(null) },
                        onClearBrand = { viewModel.setBrand(null) },
                        onClearInStock = { viewModel.setInStockOnly(false) },
                        onClearFlashSale = { viewModel.setFlashSaleOnly(false) },
                        onClearAll = viewModel::clearFilters
                    )
                }

                // Content
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    when {
                        uiState.isLoading -> {
                            VoxcinaLoading(modifier = Modifier.align(Alignment.Center))
                        }
                        uiState.error != null -> {
                            EmptyState(
                                icon = Icons.Outlined.Search,
                                title = "خطا در بارگذاری",
                                subtitle = uiState.error,
                                actionButtonText = "تلاش مجدد",
                                onActionClick = { viewModel.loadProducts() }
                            )
                        }
                        uiState.products.isEmpty() -> {
                            EmptyState(
                                icon = Icons.Outlined.Search,
                                title = "محصولی یافت نشد",
                                subtitle = "فیلترها را تغییر دهید یا عبارت دیگری جستجو کنید",
                                actionButtonText = "پاک کردن فیلترها",
                                onActionClick = viewModel::clearFilters
                            )
                        }
                        else -> {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                state = gridState,
                                contentPadding = PaddingValues(
                                    start = 16.dp,
                                    end = 16.dp,
                                    top = 8.dp,
                                    bottom = 100.dp
                                ),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(
                                    items = uiState.products,
                                    key = { "${it.productId}_${it.colorVariant.color}_${it.colorVariant.colorName}" }
                                ) { product ->
                                    ProductCard(
                                        product = product,
                                        onClick = {
                                            onProductClick(
                                                product.productId,
                                                product.colorVariant.color.removePrefix("#")
                                            )
                                        }
                                    )
                                }

                                // Loading more indicator
                                if (uiState.isLoadingMore) {
                                    item(span = { GridItemSpan(2) }) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(32.dp),
                                                color = Primary,
                                                strokeWidth = 3.dp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Bottom navigation
            BottomNavBar(
                selectedDestination = BottomNavDestination.PRODUCTS,
                cartItemCount = cartItemCount,
                onDestinationSelected = onBottomNavClick,
                modifier = Modifier.align(Alignment.BottomCenter)
            )

            // Filter bottom sheet
            if (uiState.showFilterSheet) {
                ModalBottomSheet(
                    onDismissRequest = viewModel::dismissFilterSheet,
                    sheetState = sheetState,
                    containerColor = Color.White
                ) {
                    FilterSheetContent(
                        uiState = uiState,
                        onCategorySelected = viewModel::setCategory,
                        onBrandSelected = viewModel::setBrand,
                        onInStockChanged = viewModel::setInStockOnly,
                        onFlashSaleChanged = viewModel::setFlashSaleOnly,
                        onApply = viewModel::dismissFilterSheet
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductsHeader(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onSearch: () -> Unit,
    activeFiltersCount: Int,
    onFilterClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SecondaryLight)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = "محصولات",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Primary
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SearchTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = "جستجو در محصولات...",
                onSearch = onSearch,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            FilterButton(
                activeCount = activeFiltersCount,
                onClick = onFilterClick
            )
        }
    }
}

@Composable
private fun FilterButton(
    activeCount: Int,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (activeCount > 0) Primary else Color.White)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Filter icon (3 horizontal lines)
            repeat(3) { index ->
                Box(
                    modifier = Modifier
                        .width(if (index == 1) 14.dp else 18.dp)
                        .height(2.dp)
                        .background(
                            if (activeCount > 0) Color.White else Primary,
                            RoundedCornerShape(1.dp)
                        )
                )
                if (index < 2) Spacer(modifier = Modifier.height(3.dp))
            }
        }
        if (activeCount > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .size(16.dp)
                    .background(Color.Red, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = activeCount.toString(),
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun SortChipsRow(
    selectedSort: SortOption,
    onSortSelected: (SortOption) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SortOption.entries.forEach { sort ->
            FilterChip(
                selected = selectedSort == sort,
                onClick = { onSortSelected(sort) },
                label = { Text(sort.displayName, fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Primary,
                    selectedLabelColor = Color.White
                )
            )
        }
    }
}

@Composable
private fun ActiveFiltersRow(
    uiState: ProductsUiState,
    onClearCategory: () -> Unit,
    onClearBrand: () -> Unit,
    onClearInStock: () -> Unit,
    onClearFlashSale: () -> Unit,
    onClearAll: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Clear all chip
        FilterChip(
            selected = true,
            onClick = onClearAll,
            label = { Text("پاک کردن همه", fontSize = 11.sp) },
            trailingIcon = {
                Icon(Icons.Default.Close, null, modifier = Modifier.size(14.dp))
            },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = Color.Red.copy(alpha = 0.1f),
                selectedLabelColor = Color.Red
            )
        )

        uiState.selectedCategory?.let { category ->
            ActiveFilterChip(text = category.name, onClear = onClearCategory)
        }
        uiState.selectedBrand?.let { brand ->
            ActiveFilterChip(text = brand.name, onClear = onClearBrand)
        }
        if (uiState.inStockOnly) {
            ActiveFilterChip(text = "موجود", onClear = onClearInStock)
        }
        if (uiState.flashSaleOnly) {
            ActiveFilterChip(text = "تخفیف ویژه", onClear = onClearFlashSale)
        }
    }
}

@Composable
private fun ActiveFilterChip(
    text: String,
    onClear: () -> Unit
) {
    FilterChip(
        selected = true,
        onClick = onClear,
        label = { Text(text, fontSize = 11.sp) },
        trailingIcon = {
            Icon(Icons.Default.Close, null, modifier = Modifier.size(14.dp))
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = Primary.copy(alpha = 0.1f),
            selectedLabelColor = Primary
        )
    )
}

@Composable
private fun FilterSheetContent(
    uiState: ProductsUiState,
    onCategorySelected: (Category?) -> Unit,
    onBrandSelected: (Brand?) -> Unit,
    onInStockChanged: (Boolean) -> Unit,
    onFlashSaleChanged: (Boolean) -> Unit,
    onApply: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "فیلترها",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Primary
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Toggle filters
        Text("وضعیت", fontWeight = FontWeight.SemiBold, color = Primary)
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = uiState.inStockOnly,
                onClick = { onInStockChanged(!uiState.inStockOnly) },
                label = { Text("فقط موجود") },
                leadingIcon = if (uiState.inStockOnly) {
                    { Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp)) }
                } else null,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Primary,
                    selectedLabelColor = Color.White
                )
            )
            FilterChip(
                selected = uiState.flashSaleOnly,
                onClick = { onFlashSaleChanged(!uiState.flashSaleOnly) },
                label = { Text("تخفیف ویژه") },
                leadingIcon = if (uiState.flashSaleOnly) {
                    { Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp)) }
                } else null,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Primary,
                    selectedLabelColor = Color.White
                )
            )
        }

        // Categories
        if (uiState.categories.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("دسته‌بندی", fontWeight = FontWeight.SemiBold, color = Primary)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = uiState.selectedCategory == null,
                    onClick = { onCategorySelected(null) },
                    label = { Text("همه") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Primary,
                        selectedLabelColor = Color.White
                    )
                )
                uiState.categories.forEach { category ->
                    FilterChip(
                        selected = uiState.selectedCategory?.id == category.id,
                        onClick = { onCategorySelected(category) },
                        label = { Text(category.name) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Primary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // Brands
        if (uiState.brands.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("برند", fontWeight = FontWeight.SemiBold, color = Primary)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = uiState.selectedBrand == null,
                    onClick = { onBrandSelected(null) },
                    label = { Text("همه") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Primary,
                        selectedLabelColor = Color.White
                    )
                )
                uiState.brands.forEach { brand ->
                    FilterChip(
                        selected = uiState.selectedBrand?.id == brand.id,
                        onClick = { onBrandSelected(brand) },
                        label = { Text(brand.name) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Primary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

private fun countActiveFilters(uiState: ProductsUiState): Int {
    var count = 0
    if (uiState.selectedCategory != null) count++
    if (uiState.selectedBrand != null) count++
    if (uiState.inStockOnly) count++
    if (uiState.flashSaleOnly) count++
    return count
}

private fun hasActiveFilters(uiState: ProductsUiState): Boolean =
    uiState.selectedCategory != null ||
    uiState.selectedBrand != null ||
    uiState.inStockOnly ||
    uiState.flashSaleOnly
