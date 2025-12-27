package com.voxcina.shop.presentation.home.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.voxcina.shop.ui.theme.Secondary
import com.voxcina.shop.ui.theme.VoxcinaTheme

/**
 * Shimmer effect colors for loading placeholders.
 */
private val ShimmerColorShades = listOf(
    Color(0xFFE0E0E0),
    Color(0xFFF5F5F5),
    Color(0xFFE0E0E0)
)

/**
 * Creates an animated shimmer brush effect for loading placeholders.
 * Implements Requirement 7.1 from the home screen spec.
 */
@Composable
fun shimmerBrush(): Brush {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )
    
    return Brush.linearGradient(
        colors = ShimmerColorShades,
        start = Offset(translateAnim - 500f, translateAnim - 500f),
        end = Offset(translateAnim, translateAnim)
    )
}

/**
 * Basic shimmer box component for creating loading placeholders.
 *
 * @param modifier Modifier for the shimmer box
 */
@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.background(brush = shimmerBrush())
    )
}


/**
 * Shimmer placeholder for the Hero Carousel section.
 * Matches the dimensions and layout of HeroCarousel.
 * Implements Requirement 7.1 from the home screen spec.
 *
 * @param modifier Modifier for the shimmer container
 */
@Composable
fun ShimmerHeroCarousel(
    modifier: Modifier = Modifier
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Shimmer hero image with 2:1 aspect ratio
            ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .aspectRatio(2f)
                    .clip(RoundedCornerShape(16.dp))
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Shimmer page indicator dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(4) { index ->
                    ShimmerBox(
                        modifier = Modifier
                            .size(if (index == 0) 10.dp else 8.dp)
                            .clip(CircleShape)
                    )
                }
            }
        }
    }
}

/**
 * Shimmer placeholder for the Category Section.
 * Matches the dimensions and layout of CategorySection.
 * Implements Requirement 7.1 from the home screen spec.
 *
 * @param modifier Modifier for the shimmer container
 * @param itemCount Number of category items to show
 */
@Composable
fun ShimmerCategorySection(
    modifier: Modifier = Modifier,
    itemCount: Int = 6
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = modifier.fillMaxWidth()
        ) {
            // Shimmer section header
            ShimmerSectionHeader(
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Shimmer category items row
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                userScrollEnabled = false
            ) {
                items(itemCount) {
                    ShimmerCategoryItem()
                }
            }
        }
    }
}

/**
 * Shimmer placeholder for a single category item.
 */
@Composable
private fun ShimmerCategoryItem(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.width(72.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Circular icon placeholder
        ShimmerBox(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Category name placeholder
        ShimmerBox(
            modifier = Modifier
                .width(56.dp)
                .height(12.dp)
                .clip(RoundedCornerShape(4.dp))
        )
    }
}

/**
 * Shimmer placeholder for section headers.
 */
@Composable
fun ShimmerSectionHeader(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Title placeholder
        ShimmerBox(
            modifier = Modifier
                .width(100.dp)
                .height(20.dp)
                .clip(RoundedCornerShape(4.dp))
        )
        
        // View all button placeholder
        ShimmerBox(
            modifier = Modifier
                .width(80.dp)
                .height(16.dp)
                .clip(RoundedCornerShape(4.dp))
        )
    }
}


/**
 * Shimmer placeholder for the Flash Sale Section.
 * Matches the dimensions and layout of FlashSaleSection.
 * Implements Requirement 7.1 from the home screen spec.
 *
 * @param modifier Modifier for the shimmer container
 * @param itemCount Number of product items to show
 */
@Composable
fun ShimmerFlashSaleSection(
    modifier: Modifier = Modifier,
    itemCount: Int = 4
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFE8E8E8))
                .padding(vertical = 16.dp)
        ) {
            Column {
                // Shimmer flash sale header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Fire icon and title placeholder
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ShimmerBox(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                        )
                        ShimmerBox(
                            modifier = Modifier
                                .width(80.dp)
                                .height(20.dp)
                                .clip(RoundedCornerShape(4.dp))
                        )
                    }
                    
                    // Countdown timer placeholder
                    ShimmerBox(
                        modifier = Modifier
                            .width(100.dp)
                            .height(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Shimmer product cards row
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    userScrollEnabled = false
                ) {
                    items(itemCount) {
                        ShimmerFlashSaleProductCard()
                    }
                    
                    // View all card placeholder
                    item {
                        ShimmerBox(
                            modifier = Modifier
                                .width(100.dp)
                                .height(180.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                    }
                }
            }
        }
    }
}

/**
 * Shimmer placeholder for a flash sale product card.
 */
@Composable
private fun ShimmerFlashSaleProductCard(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(140.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
    ) {
        Column {
            // Product image placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            ) {
                ShimmerBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                )
                
                // Discount badge placeholder
                ShimmerBox(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .width(40.dp)
                        .height(20.dp)
                        .clip(RoundedCornerShape(6.dp))
                )
            }
            
            // Product info placeholders
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                // Product name
                ShimmerBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                ShimmerBox(
                    modifier = Modifier
                        .width(80.dp)
                        .height(12.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Original price
                ShimmerBox(
                    modifier = Modifier
                        .width(60.dp)
                        .height(10.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
                
                Spacer(modifier = Modifier.height(2.dp))
                
                // Discounted price
                ShimmerBox(
                    modifier = Modifier
                        .width(80.dp)
                        .height(14.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
            }
        }
    }
}


/**
 * Shimmer placeholder for the Product Grid section.
 * Matches the dimensions and layout of RecommendedProductsSection.
 * Implements Requirement 7.1 from the home screen spec.
 *
 * @param modifier Modifier for the shimmer container
 * @param itemCount Number of product items to show
 */
@Composable
fun ShimmerProductGrid(
    modifier: Modifier = Modifier,
    itemCount: Int = 4
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = modifier.fillMaxWidth()
        ) {
            // Shimmer section header
            ShimmerSectionHeader(
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 2-column grid layout
            val rowCount = (itemCount + 1) / 2
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.height((rowCount * 280).dp),
                userScrollEnabled = false
            ) {
                items(itemCount) {
                    ShimmerProductCard()
                }
            }
        }
    }
}

/**
 * Shimmer placeholder for a product card.
 */
@Composable
fun ShimmerProductCard(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
    ) {
        Column {
            // Product image with 4:5 aspect ratio
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f / 5f)
            ) {
                ShimmerBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(4f / 5f)
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                )
                
                // Rating badge placeholder
                ShimmerBox(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .width(45.dp)
                        .height(20.dp)
                        .clip(RoundedCornerShape(6.dp))
                )
                
                // Color indicator placeholder
                ShimmerBox(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(20.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
            }
            
            // Product info placeholders
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                // Product name (2 lines)
                ShimmerBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(14.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                ShimmerBox(
                    modifier = Modifier
                        .width(100.dp)
                        .height(14.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Brand name
                ShimmerBox(
                    modifier = Modifier
                        .width(80.dp)
                        .height(11.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Price
                ShimmerBox(
                    modifier = Modifier
                        .width(90.dp)
                        .height(16.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
            }
        }
    }
}

/**
 * Shimmer placeholder for the Recently Viewed Section.
 * Matches the dimensions and layout of RecentlyViewedSection.
 * Implements Requirement 7.1 from the home screen spec.
 *
 * @param modifier Modifier for the shimmer container
 * @param itemCount Number of items to show
 */
@Composable
fun ShimmerRecentlyViewedSection(
    modifier: Modifier = Modifier,
    itemCount: Int = 5
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = modifier.fillMaxWidth()
        ) {
            // Shimmer section header
            ShimmerSectionHeader(
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Shimmer recently viewed items row
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                userScrollEnabled = false
            ) {
                items(itemCount) {
                    ShimmerRecentProductCard()
                }
            }
        }
    }
}

/**
 * Shimmer placeholder for a recently viewed product card.
 */
@Composable
private fun ShimmerRecentProductCard(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(120.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
    ) {
        Column {
            // Product image
            ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            )
            
            // Product info
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                // Product name
                ShimmerBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Price
                ShimmerBox(
                    modifier = Modifier
                        .width(70.dp)
                        .height(12.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
            }
        }
    }
}


/**
 * Complete shimmer loading state for the entire home screen.
 * Combines all section shimmers in the correct order.
 * Implements Requirement 7.1 from the home screen spec.
 *
 * @param modifier Modifier for the shimmer container
 * @param showFlashSale Whether to show flash sale shimmer
 * @param showRecentlyViewed Whether to show recently viewed shimmer
 */
@Composable
fun ShimmerHomeContent(
    modifier: Modifier = Modifier,
    showFlashSale: Boolean = true,
    showRecentlyViewed: Boolean = true
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Secondary)
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Hero Carousel shimmer
        ShimmerHeroCarousel()
        
        // Category Section shimmer
        ShimmerCategorySection()
        
        // Flash Sale Section shimmer (conditional)
        if (showFlashSale) {
            ShimmerFlashSaleSection(
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
        
        // Product Grid shimmer
        ShimmerProductGrid()
        
        // Recently Viewed Section shimmer (conditional)
        if (showRecentlyViewed) {
            ShimmerRecentlyViewedSection()
        }
    }
}

// ============ Preview Functions ============

@Preview(showBackground = true)
@Composable
private fun ShimmerHeroCarouselPreview() {
    VoxcinaTheme {
        ShimmerHeroCarousel(
            modifier = Modifier.padding(vertical = 16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ShimmerCategorySectionPreview() {
    VoxcinaTheme {
        ShimmerCategorySection(
            modifier = Modifier.padding(vertical = 16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ShimmerFlashSaleSectionPreview() {
    VoxcinaTheme {
        ShimmerFlashSaleSection(
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ShimmerProductGridPreview() {
    VoxcinaTheme {
        ShimmerProductGrid(
            modifier = Modifier.padding(vertical = 16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ShimmerProductCardPreview() {
    VoxcinaTheme {
        ShimmerProductCard(
            modifier = Modifier
                .width(180.dp)
                .padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ShimmerRecentlyViewedSectionPreview() {
    VoxcinaTheme {
        ShimmerRecentlyViewedSection(
            modifier = Modifier.padding(vertical = 16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ShimmerHomeContentPreview() {
    VoxcinaTheme {
        ShimmerHomeContent()
    }
}

@Preview(showBackground = true)
@Composable
private fun ShimmerHomeContentMinimalPreview() {
    VoxcinaTheme {
        ShimmerHomeContent(
            showFlashSale = false,
            showRecentlyViewed = false
        )
    }
}
