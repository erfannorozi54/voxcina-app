package com.voxcina.shop.presentation.home.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.voxcina.shop.domain.model.Product
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.PrimaryDark
import com.voxcina.shop.util.DiscountCalculator
import com.voxcina.shop.util.PersianDigitConverter

/**
 * Unified product card component for displaying products across the app.
 * Shows product image, discount badge, favorite button, color indicator, and pricing.
 *
 * @param product The product to display
 * @param onClick Callback when the card is clicked
 * @param onFavoriteClick Callback when favorite button is clicked
 * @param isFavorite Whether the product is favorited
 * @param modifier Modifier for the card
 */
@Composable
fun ProductCard(
    product: Product,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit = {},
    isFavorite: Boolean = false,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val discountPercentage = product.originalPrice?.let { originalPrice ->
        if (originalPrice > product.price) {
            DiscountCalculator.calculateDiscountPercentage(originalPrice, product.price)
        } else null
    }
    
    // Scale animation for press feedback
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = tween(durationMillis = 100),
        label = "cardScale"
    )
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column {
            // Product image with badges
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f / 5f)
            ) {
                val imageUrl = product.colorVariant.images.firstOrNull()?.let {
                    if (it.startsWith("http")) it else "https://voxcina.com$it"
                }
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = product.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                    contentScale = ContentScale.Crop
                )
                
                // Discount badge and favorite button (top left in RTL)
                Column(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Discount percentage badge
                    discountPercentage?.let { discount ->
                        Box(
                            modifier = Modifier
                                .background(
                                    color = Color(0xFFEF4444),
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "${PersianDigitConverter.toPersianDigits(discount.toString())}٪",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    
                    // Favorite button
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.9f))
                            .clickable { onFavoriteClick() }
                            .padding(6.dp)
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Add to favorites",
                            tint = if (isFavorite) Color(0xFFEF4444) else Color.Gray,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                
                // Color indicator badge (bottom right in RTL)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .padding(2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(parseColor(product.colorVariant.color))
                    )
                }
            }
            
            // Product info
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                // Product name (max 2 lines)
                Text(
                    text = product.name,
                    color = PrimaryDark,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Brand and color name
                Text(
                    text = "${product.colorVariant.colorName} - ${product.brand}",
                    color = Color.Gray,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Price section
                if (product.originalPrice != null && product.originalPrice > product.price) {
                    // Show both original and discounted price
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = PersianDigitConverter.formatPriceWithSuffix(product.price),
                            color = Primary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = PersianDigitConverter.formatPriceWithSuffix(product.originalPrice),
                            color = Color.Gray,
                            fontSize = 11.sp,
                            style = androidx.compose.ui.text.TextStyle(
                                textDecoration = TextDecoration.LineThrough
                            )
                        )
                    }
                } else {
                    // Show only current price
                    Text(
                        text = PersianDigitConverter.formatPriceWithSuffix(product.price),
                        color = Primary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Parses a hex color string to a Compose Color.
 */
private fun parseColor(hexColor: String): Color {
    return try {
        val colorString = if (hexColor.startsWith("#")) hexColor else "#$hexColor"
        Color(android.graphics.Color.parseColor(colorString))
    } catch (e: Exception) {
        Color.Gray
    }
}
