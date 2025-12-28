package com.voxcina.shop.presentation.home.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
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
import com.voxcina.shop.domain.model.SizeVariant
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.PrimaryDark
import com.voxcina.shop.ui.theme.VazirMatnFamily
import com.voxcina.shop.util.DiscountCalculator
import com.voxcina.shop.util.PersianDigitConverter
import kotlinx.coroutines.delay

@Composable
fun ProductCard(
    product: Product,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit = {},
    onAddToCart: (size: String) -> Unit = {},
    isFavorite: Boolean = false,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    var showSizeSelector by remember { mutableStateOf(false) }
    
    val discountPercentage = product.originalPrice?.let { originalPrice ->
        if (originalPrice > product.price) {
            DiscountCalculator.calculateDiscountPercentage(originalPrice, product.price)
        } else null
    }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = tween(durationMillis = 150),
        label = "cardScale"
    )
    
    val cardShape = RoundedCornerShape(20.dp)
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(cardShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { if (!showSizeSelector) onClick() }
            )
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color.White.copy(alpha = 0.85f))
                .border(1.dp, Color.White.copy(alpha = 0.6f), cardShape)
        )
        
        Column {
            // Product Image
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
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                    contentScale = ContentScale.Crop
                )
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.15f))
                            )
                        )
                )
                
                // Top badges
                Column(
                    modifier = Modifier.align(Alignment.TopEnd).padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (discountPercentage != null) {
                        GlassBadge(
                            text = "${PersianDigitConverter.toPersianDigits(discountPercentage.toString())}٪-",
                            backgroundColor = Color(0xFFEF4444).copy(alpha = 0.9f),
                            textColor = Color.White
                        )
                    }
                    GlassIconButton(onClick = onFavoriteClick, isFavorite = isFavorite)
                }
                
                // Color indicator
                Box(
                    modifier = Modifier.align(Alignment.BottomStart).padding(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.9f))
                            .border(1.5.dp, Color.White, CircleShape)
                            .padding(3.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(parseColor(product.colorVariant.color))
                        )
                    }
                }
            }
            
            // Product info section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.White.copy(alpha = 0.95f), Color.White.copy(alpha = 0.85f))
                        )
                    )
            ) {
                // Default product info - always present to maintain height
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                        .alpha(if (showSizeSelector) 0f else 1f)
                ) {
                    Text(
                        text = product.name,
                        color = PrimaryDark,
                        fontSize = 13.sp,
                        fontFamily = VazirMatnFamily,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 20.sp
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "${product.colorVariant.colorName} • ${product.brand}",
                        color = Color.Gray.copy(alpha = 0.8f),
                        fontSize = 11.sp,
                        fontFamily = VazirMatnFamily,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            if (product.originalPrice != null && product.originalPrice > product.price) {
                                Text(
                                    text = PersianDigitConverter.formatPriceWithSuffix(product.originalPrice),
                                    color = Color.Gray.copy(alpha = 0.6f),
                                    fontSize = 10.sp,
                                    fontFamily = VazirMatnFamily,
                                    style = androidx.compose.ui.text.TextStyle(textDecoration = TextDecoration.LineThrough)
                                )
                            }
                            Text(
                                text = PersianDigitConverter.formatPriceWithSuffix(product.price),
                                color = Primary,
                                fontSize = 14.sp,
                                fontFamily = VazirMatnFamily,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        // Add to cart button
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Primary)
                                .clickable { showSizeSelector = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.ShoppingCart,
                                contentDescription = "افزودن به سبد",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
                
                // Size selector overlay
                androidx.compose.animation.AnimatedVisibility(
                    visible = showSizeSelector,
                    modifier = Modifier.matchParentSize(),
                    enter = slideInVertically(spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessLow)) { it } + fadeIn(tween(200)),
                    exit = slideOutVertically(spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessMediumLow)) { it } + fadeOut(tween(150))
                ) {
                    SizeSelectorOverlay(
                        sizes = product.colorVariant.sizes,
                        onSizeSelected = { size ->
                            showSizeSelector = false
                            onAddToCart(size)
                        },
                        onDismiss = { showSizeSelector = false }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SizeSelectorOverlay(
    sizes: List<SizeVariant>,
    onSizeSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var sizesVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(100)
        sizesVisible = true
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Primary.copy(alpha = 0.95f), Primary)
                ),
                RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
            )
            .padding(12.dp)
    ) {
        // Close button at top end
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(28.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.2f))
                .clickable { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = "بستن",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
        
        // Size buttons centered
        FlowRow(
            modifier = Modifier.align(Alignment.Center),
            horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically),
            maxItemsInEachRow = 4
        ) {
            sizes.forEachIndexed { index, sizeVariant ->
                val isAvailable = sizeVariant.quantity > 0
                var itemVisible by remember { mutableStateOf(false) }
                
                LaunchedEffect(sizesVisible) {
                    if (sizesVisible) {
                        delay(index * 40L)
                        itemVisible = true
                    }
                }
                
                val alpha by animateFloatAsState(
                    targetValue = if (itemVisible) 1f else 0f,
                    animationSpec = tween(150),
                    label = "sizeAlpha$index"
                )
                val itemScale by animateFloatAsState(
                    targetValue = if (itemVisible) 1f else 0.7f,
                    animationSpec = tween(150),
                    label = "sizeScale$index"
                )
                
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .scale(itemScale)
                        .alpha(alpha)
                        .height(28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isAvailable) Color.White else Color.White.copy(alpha = 0.3f)
                        )
                        .then(
                            if (isAvailable) Modifier.clickable { onSizeSelected(sizeVariant.size) }
                            else Modifier
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = sizeVariant.size,
                        color = if (isAvailable) Primary else Color.White.copy(alpha = 0.5f),
                        fontSize = 11.sp,
                        fontFamily = VazirMatnFamily,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun GlassBadge(text: String, backgroundColor: Color, textColor: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .padding(horizontal = 6.dp, vertical = 3.dp)
    ) {
        Text(text = text, color = textColor, fontSize = 10.sp, fontFamily = VazirMatnFamily, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun GlassIconButton(onClick: () -> Unit, isFavorite: Boolean) {
    Box(
        modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.85f))
            .border(1.dp, Color.White.copy(alpha = 0.5f), CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = "Favorite",
            tint = if (isFavorite) Color(0xFFEF4444) else Color.Gray.copy(alpha = 0.7f),
            modifier = Modifier.size(16.dp)
        )
    }
}

private fun parseColor(hexColor: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(if (hexColor.startsWith("#")) hexColor else "#$hexColor"))
    } catch (e: Exception) {
        Color.Gray
    }
}
