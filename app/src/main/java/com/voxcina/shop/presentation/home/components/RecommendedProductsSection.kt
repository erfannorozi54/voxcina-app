package com.voxcina.shop.presentation.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.voxcina.shop.R
import com.voxcina.shop.domain.model.Product

/**
 * Recommended products section displaying products in a 2-column grid layout.
 * Implements Requirement 5.2 from the home screen spec.
 *
 * @param products List of recommended products to display
 * @param modifier Modifier for the section container
 * @param onProductClick Callback when a product is clicked with productId and colorHex
 * @param onAddToCartClick Callback when add-to-cart button is clicked
 */
@Composable
fun RecommendedProductsSection(
    products: List<Product>,
    modifier: Modifier = Modifier,
    onProductClick: (productId: String, colorHex: String) -> Unit = { _, _ -> },
    onAddToCart: (product: Product, size: String) -> Unit = { _, _ -> }
) {
    if (products.isEmpty()) return
    
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = modifier.fillMaxWidth()
        ) {
            SectionHeader(
                title = stringResource(R.string.recommended_products_title),
                modifier = Modifier.padding(horizontal = 16.dp),
                onViewAllClick = {}
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            val rowCount = (products.size + 1) / 2
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.height((rowCount * 300).dp)
            ) {
                items(
                    items = products,
                    key = { "${it.productId}_${it.colorVariant.color}_${it.colorVariant.colorName}" }
                ) { product ->
                    ProductCard(
                        product = product,
                        onClick = { onProductClick(product.productId, product.colorVariant.color) },
                        onFavoriteClick = { /* TODO: Implement favorite */ },
                        onAddToCart = { size -> onAddToCart(product, size) }
                    )
                }
            }
        }
    }
}
