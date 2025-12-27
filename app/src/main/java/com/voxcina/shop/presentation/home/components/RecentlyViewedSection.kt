package com.voxcina.shop.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.voxcina.shop.R
import com.voxcina.shop.domain.model.RecentlyViewedProduct
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.PrimaryDark
import com.voxcina.shop.ui.theme.VoxcinaTheme
import com.voxcina.shop.util.PersianDigitConverter

/**
 * Recently viewed products section displaying the last 5 products viewed by the user.
 * Implements Requirements 10.3, 10.4, 10.5, 10.6 from the home screen spec.
 *
 * @param products List of recently viewed products to display
 * @param modifier Modifier for the section container
 * @param onProductClick Callback when a product is clicked with productId and colorHex
 */
@Composable
fun RecentlyViewedSection(
    products: List<RecentlyViewedProduct>,
    modifier: Modifier = Modifier,
    onProductClick: (productId: String, colorHex: String) -> Unit = { _, _ -> }
) {
    // Conditional visibility: hide if empty (Requirement 10.6)
    if (products.isEmpty()) return
    
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = modifier.fillMaxWidth()
        ) {
            // Section header with "بازدید اخیر شما" title
            SectionHeader(
                title = stringResource(R.string.recently_viewed_title),
                modifier = Modifier.padding(horizontal = 16.dp),
                onViewAllClick = {} // No view all for recently viewed
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Horizontally scrollable row (Requirement 10.4)
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = products,
                    key = { "${it.productId}_${it.colorHex}" }
                ) { product ->
                    RecentProductCard(
                        product = product,
                        onClick = { 
                            onProductClick(product.productId, product.colorHex) 
                        }
                    )
                }
            }
        }
    }
}

/**
 * Compact card for recently viewed products.
 * Implements Requirements 10.3, 10.7, 10.10 from the home screen spec.
 *
 * @param product The recently viewed product to display
 * @param onClick Callback when the card is clicked
 * @param modifier Modifier for the card
 */
@Composable
fun RecentProductCard(
    product: RecentlyViewedProduct,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    Card(
        modifier = modifier
            .width(120.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp // Subtle shadow (Requirement 10.10)
        )
    ) {
        Column {
            // Product image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(product.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = product.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                    contentScale = ContentScale.Crop
                )
                
                // Color indicator badge
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(16.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(parseColorSafe(product.colorHex))
                )
            }
            
            // Product info (compact design)
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                // Product name (max 2 lines)
                Text(
                    text = product.name,
                    color = PrimaryDark,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 14.sp
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Price in Persian digits with "تومان" suffix
                Text(
                    text = PersianDigitConverter.formatPriceWithSuffix(product.price),
                    color = Primary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Safely parses a hex color string to a Compose Color.
 * Returns gray if parsing fails.
 */
private fun parseColorSafe(hexColor: String): Color {
    return try {
        val colorString = if (hexColor.startsWith("#")) hexColor else "#$hexColor"
        Color(android.graphics.Color.parseColor(colorString))
    } catch (e: Exception) {
        Color.Gray
    }
}

// ============ Preview Functions ============

@Preview(showBackground = true)
@Composable
private fun RecentlyViewedSectionPreview() {
    VoxcinaTheme {
        RecentlyViewedSection(
            products = sampleRecentlyViewedProducts,
            modifier = Modifier.padding(vertical = 16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RecentlyViewedSectionEmptyPreview() {
    VoxcinaTheme {
        RecentlyViewedSection(
            products = emptyList(),
            modifier = Modifier.padding(vertical = 16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RecentProductCardPreview() {
    VoxcinaTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            RecentProductCard(
                product = sampleRecentlyViewedProducts.first(),
                onClick = {},
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

// Sample data for previews
private val sampleRecentlyViewedProducts = listOf(
    RecentlyViewedProduct(
        productId = "1",
        name = "تیشرت مردانه نایکی",
        price = 450000,
        imageUrl = "https://example.com/image1.jpg",
        colorHex = "#FF5733",
        viewedAt = System.currentTimeMillis()
    ),
    RecentlyViewedProduct(
        productId = "2",
        name = "شلوار جین مردانه لیوایز کلاسیک",
        price = 850000,
        imageUrl = "https://example.com/image2.jpg",
        colorHex = "#1A3C69",
        viewedAt = System.currentTimeMillis() - 60000
    ),
    RecentlyViewedProduct(
        productId = "3",
        name = "کفش ورزشی آدیداس",
        price = 1500000,
        imageUrl = "https://example.com/image3.jpg",
        colorHex = "#000000",
        viewedAt = System.currentTimeMillis() - 120000
    ),
    RecentlyViewedProduct(
        productId = "4",
        name = "پیراهن مردانه رسمی",
        price = 680000,
        imageUrl = "https://example.com/image4.jpg",
        colorHex = "#FFFFFF",
        viewedAt = System.currentTimeMillis() - 180000
    ),
    RecentlyViewedProduct(
        productId = "5",
        name = "کت مردانه زارا",
        price = 2200000,
        imageUrl = "https://example.com/image5.jpg",
        colorHex = "#333333",
        viewedAt = System.currentTimeMillis() - 240000
    )
)
