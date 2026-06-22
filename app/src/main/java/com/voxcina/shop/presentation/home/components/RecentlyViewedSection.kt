package com.voxcina.shop.presentation.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.voxcina.shop.R
import com.voxcina.shop.domain.model.ColorVariant
import com.voxcina.shop.domain.model.Product
import com.voxcina.shop.domain.model.RecentlyViewedProduct

/**
 * Recently viewed products section displaying products viewed by the user.
 */
@Composable
fun RecentlyViewedSection(
    products: List<RecentlyViewedProduct>,
    modifier: Modifier = Modifier,
    onProductClick: (productId: String, colorHex: String) -> Unit = { _, _ -> },
    onViewAllClick: () -> Unit = {}
) {
    if (products.isEmpty()) return
    
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(modifier = modifier.fillMaxWidth()) {
            SectionHeader(
                title = stringResource(R.string.recently_viewed_title),
                modifier = Modifier.padding(horizontal = 16.dp),
                onViewAllClick = onViewAllClick
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = products,
                    key = { "${it.productId}_${it.colorHex}_${it.viewedAt}" }
                ) { recentProduct ->
                    ProductCard(
                        product = recentProduct.toProduct(),
                        onClick = { onProductClick(recentProduct.productId, recentProduct.colorHex) },
                        modifier = Modifier.width(160.dp)
                    )
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
