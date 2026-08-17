package com.voxcina.shop.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.voxcina.shop.R
import com.voxcina.shop.domain.model.ColorVariant
import com.voxcina.shop.domain.model.Product
import com.voxcina.shop.domain.model.SizeVariant
import com.voxcina.shop.ui.theme.Destructive
import com.voxcina.shop.ui.theme.VoxcinaTheme
import com.voxcina.shop.ui.theme.Warning
import com.voxcina.shop.util.CountdownFormatter
import com.voxcina.shop.util.CountdownState
import com.voxcina.shop.util.CountdownTimer
import com.voxcina.shop.util.DiscountCalculator
import com.voxcina.shop.util.PersianDigitConverter

// Flash sale gradient colors (orange-amber)
private val FlashSaleGradientStart = Color(0xFFFF6B35)
private val FlashSaleGradientEnd = Color(0xFFF7931E)


/**
 * Flash sale section component displaying time-limited offers with countdown timer.
 * Implements Requirements 4.2, 4.4, 4.7 from the home screen spec.
 *
 * @param products List of flash sale products to display
 * @param endTimeMillis End time of the flash sale in milliseconds
 * @param modifier Modifier for the section container
 * @param onProductClick Callback when a product is clicked with productId and colorHex
 * @param onViewAllClick Callback when "View All" button is clicked
 */
@Composable
fun FlashSaleSection(
    products: List<Product>,
    endTimeMillis: Long,
    modifier: Modifier = Modifier,
    onProductClick: (productId: String, colorHex: String) -> Unit = { _, _ -> },
    onAddToCart: (product: Product, size: String) -> Unit = { _, _ -> },
    onViewAllClick: () -> Unit = {}
) {
    if (products.isEmpty()) return
    
    var countdownState by remember { 
        mutableStateOf(CountdownState(0, 0, 0, 0, false)) 
    }
    
    CountdownTimer(
        endTimeMillis = endTimeMillis,
        onTick = { state -> countdownState = state },
        onFinish = { }
    )
    
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(FlashSaleGradientStart, FlashSaleGradientEnd)
                    )
                )
                .padding(vertical = 16.dp)
        ) {
            Column {
                FlashSaleHeader(
                    countdownState = countdownState,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = products,
                        key = { "${it.productId}_${it.colorVariant.color}_${it.colorVariant.colorName}" }
                    ) { product ->
                        ProductCard(
                            product = product,
                            onClick = { onProductClick(product.productId, product.colorVariant.color) },
                            onFavoriteClick = { },
                            onAddToCart = { size -> onAddToCart(product, size) },
                            modifier = Modifier.width(160.dp)
                        )
                    }
                }
            }
        }
    }
}


/**
 * Flash sale header with fire icon, title, and countdown timer.
 *
 * @param countdownState Current countdown timer state
 * @param modifier Modifier for the header row
 */
@Composable
private fun FlashSaleHeader(
    countdownState: CountdownState,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Fire icon and title
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Fire emoji as icon
            Text(
                text = "🔥",
                fontSize = 24.sp,
                modifier = Modifier.size(28.dp)
            )
            Text(
                text = stringResource(R.string.flash_sale_title),
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        // Glassmorphism countdown timer
        GlassCountdownTimer(
            hours = countdownState.hours,
            minutes = countdownState.minutes,
            seconds = countdownState.seconds
        )
    }
}

/**
 * Glassmorphism styled countdown timer display.
 * Implements Requirements 4.3, 9.2 from the home screen spec.
 *
 * @param hours Hours remaining
 * @param minutes Minutes remaining
 * @param seconds Seconds remaining
 * @param modifier Modifier for the timer container
 */
@Composable
fun GlassCountdownTimer(
    hours: Int,
    minutes: Int,
    seconds: Int,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(8.dp)
    
    Box(
        modifier = modifier.clip(shape)
    ) {
        // Glass background with blur effect
        Box(
            modifier = Modifier
                .matchParentSize()
                .blur(radius = 12.dp)
                .background(
                    color = Color.White.copy(alpha = 0.2f),
                    shape = shape
                )
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.2f),
                    shape = shape
                )
        )
        
        // Content layer
        Box(
            modifier = Modifier
                .background(
                    color = Color.White.copy(alpha = 0.13f),
                    shape = shape
                )
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.17f),
                    shape = shape
                )
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TimeUnit(value = hours)
                TimeSeparator()
                TimeUnit(value = minutes)
                TimeSeparator()
                TimeUnit(value = seconds)
            }
        }
    }
}

/**
 * Individual time unit display (hours, minutes, or seconds).
 */
@Composable
private fun TimeUnit(
    value: Int,
    modifier: Modifier = Modifier
) {
    val formattedValue = PersianDigitConverter.toPersianDigits(
        String.format("%02d", value)
    )
    Text(
        text = formattedValue,
        color = Color.White,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier
    )
}

/**
 * Colon separator between time units.
 */
@Composable
private fun TimeSeparator() {
    Text(
        text = ":",
        color = Color.White,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold
    )
}


/**
 * Flash sale product card with discount badge, prices, and product info.
 * Implements Requirement 4.5 from the home screen spec.
 *
 * @param product The product to display
 * @param onClick Callback when the card is clicked
 * @param modifier Modifier for the card
 */


