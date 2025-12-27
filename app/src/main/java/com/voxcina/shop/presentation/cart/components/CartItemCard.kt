package com.voxcina.shop.presentation.cart.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.voxcina.shop.domain.model.CartColorVariant
import com.voxcina.shop.domain.model.CartItem
import com.voxcina.shop.domain.model.CartProduct
import com.voxcina.shop.domain.model.CartVariant
import com.voxcina.shop.ui.components.PriceText
import com.voxcina.shop.ui.components.QuantitySelector
import com.voxcina.shop.ui.components.SoftShadowCard
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.VoxcinaTheme

/**
 * Cart item card component displaying product info with quantity controls.
 * Uses SoftShadowCard, QuantitySelector, PriceText, and save for later button.
 *
 * @param item The cart item to display
 * @param onQuantityChange Callback when quantity changes
 * @param onSaveForLater Callback when save for later is clicked
 * @param modifier Modifier for the card
 * @param isUpdating Whether the item is being updated (shows loading in quantity selector)
 */
@Composable
fun CartItemCard(
    item: CartItem,
    onQuantityChange: (Int) -> Unit,
    onSaveForLater: () -> Unit,
    modifier: Modifier = Modifier,
    isUpdating: Boolean = false
) {
    val context = LocalContext.current
    
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        SoftShadowCard(
            modifier = modifier.fillMaxWidth(),
            cornerRadius = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                // Product info row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Product image - use color variant's first image
                    val variantImage = item.product.colorVariants
                        .find { it.color == item.variant.color }
                        ?.images?.firstOrNull()
                    val imageUrl = (variantImage ?: item.product.mainImages.firstOrNull())?.let {
                        if (it.startsWith("http")) it else "https://voxcina.com$it"
                    }
                    
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = item.product.name,
                        modifier = Modifier
                            .size(96.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    
                    // Product details
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Product name (max 1 line, truncated)
                        Text(
                            text = item.product.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Primary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        
                        // Variant info (color name)
                        Text(
                            text = "رنگ: ${item.variant.colorName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        // Price
                        PriceText(
                            price = item.product.price,
                            priceStyle = MaterialTheme.typography.titleMedium,
                            priceColor = Primary
                        )
                    }
                }
                
                // Divider
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = Color.Gray.copy(alpha = 0.1f)
                )
                
                // Bottom section: Quantity selector and Save for later button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Quantity selector
                    QuantitySelector(
                        quantity = item.quantity,
                        onQuantityChange = onQuantityChange,
                        isLoading = isUpdating
                    )
                    
                    // Save for later button
                    SaveForLaterButton(onClick = onSaveForLater)
                }
            }
        }
    }
}

/**
 * Save for later button with bookmark icon.
 */
@Composable
private fun SaveForLaterButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.FavoriteBorder,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = Color.Gray
            )
            Text(
                text = "ذخیره برای بعد",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFCFAF8)
@Composable
private fun CartItemCardPreview() {
    VoxcinaTheme {
        CartItemCard(
            item = CartItem(
                product = CartProduct(
                    id = "1",
                    name = "تیشرت مردانه نایکی اسپرت",
                    price = 450000,
                    originalPrice = 550000,
                    mainImages = listOf("/uploads/products/sample.jpg"),
                    colorVariants = listOf(
                        CartColorVariant("#FF0000", listOf("/uploads/products/red.jpg"))
                    ),
                    brand = "Nike",
                    inStock = true
                ),
                variant = CartVariant(
                    size = "L",
                    color = "#FF0000",
                    colorName = "قرمز",
                    sku = "SKU123"
                ),
                quantity = 2
            ),
            onQuantityChange = {},
            onSaveForLater = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFCFAF8)
@Composable
private fun CartItemCardLoadingPreview() {
    VoxcinaTheme {
        CartItemCard(
            item = CartItem(
                product = CartProduct(
                    id = "1",
                    name = "شلوار جین مردانه لیوایز کلاسیک",
                    price = 1250000,
                    originalPrice = null,
                    mainImages = listOf("/uploads/products/sample.jpg"),
                    colorVariants = emptyList(),
                    brand = "Levi's",
                    inStock = true
                ),
                variant = CartVariant(
                    size = "32",
                    color = "#000080",
                    colorName = "سرمه‌ای",
                    sku = "SKU456"
                ),
                quantity = 1
            ),
            onQuantityChange = {},
            onSaveForLater = {},
            isUpdating = true,
            modifier = Modifier.padding(16.dp)
        )
    }
}
