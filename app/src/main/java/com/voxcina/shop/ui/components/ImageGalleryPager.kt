package com.voxcina.shop.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.voxcina.shop.ui.theme.VoxcinaTheme
import kotlinx.coroutines.launch

/**
 * A reusable horizontal image gallery pager with pagination dots.
 * Features gradient overlay at top for navigation visibility.
 * 
 * Reusable for product details, product quick view, and any image carousel.
 *
 * Requirements: 1.1, 1.2, 1.3, 1.4, 1.5, 1.6
 *
 * @param images List of image URLs to display
 * @param modifier Modifier for the gallery container
 * @param currentIndex Current page index (for external control)
 * @param onIndexChanged Callback when page changes
 * @param heightFraction Fraction of screen height (default: 0.55 = 55%)
 * @param showGradientOverlay Whether to show gradient overlay at top
 * @param gradientHeight Height of the gradient overlay
 * @param contentDescription Content description for accessibility
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageGalleryPager(
    images: List<String>,
    modifier: Modifier = Modifier,
    currentIndex: Int = 0,
    onIndexChanged: (Int) -> Unit = {},
    heightFraction: Float = 0.55f,
    showGradientOverlay: Boolean = true,
    gradientHeight: Dp = 128.dp,
    contentDescription: String = "تصویر محصول"
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val galleryHeight = screenHeight * heightFraction
    
    val pagerState = rememberPagerState(
        initialPage = currentIndex.coerceIn(0, (images.size - 1).coerceAtLeast(0)),
        pageCount = { images.size }
    )
    val coroutineScope = rememberCoroutineScope()
    
    // Sync external index changes with pager
    LaunchedEffect(currentIndex) {
        if (currentIndex != pagerState.currentPage && currentIndex in images.indices) {
            pagerState.animateScrollToPage(currentIndex)
        }
    }
    
    // Notify parent of page changes
    LaunchedEffect(pagerState.currentPage) {
        onIndexChanged(pagerState.currentPage)
    }
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(galleryHeight)
    ) {
        // Image Pager
        if (images.isNotEmpty()) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(images[page])
                        .crossfade(true)
                        .build(),
                    contentDescription = "$contentDescription ${page + 1}",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        } else {
            // Placeholder when no images
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Gray.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                // Empty state - could add placeholder image here
            }
        }
        
        // Gradient overlay at top for navigation visibility
        if (showGradientOverlay) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(gradientHeight)
                    .align(Alignment.TopCenter)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.4f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }
        
        // Pagination dots at bottom
        if (images.size > 1) {
            PaginationDots(
                totalDots = images.size,
                selectedIndex = pagerState.currentPage,
                onDotClick = { index ->
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            )
        }
    }
}

/**
 * Pagination dots indicator for the image gallery.
 *
 * @param totalDots Total number of dots
 * @param selectedIndex Currently selected dot index
 * @param onDotClick Callback when a dot is clicked
 * @param modifier Modifier for the dots container
 */
@Composable
private fun PaginationDots(
    totalDots: Int,
    selectedIndex: Int,
    onDotClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(totalDots) { index ->
            val isSelected = index == selectedIndex
            Box(
                modifier = Modifier
                    .size(if (isSelected) 10.dp else 8.dp)
                    .clip(CircleShape)
                    .background(
                        color = if (isSelected) {
                            Color.White
                        } else {
                            Color.White.copy(alpha = 0.5f)
                        }
                    )
                    .clickable { onDotClick(index) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ImageGalleryPagerPreview() {
    VoxcinaTheme {
        ImageGalleryPager(
            images = listOf(
                "https://example.com/image1.jpg",
                "https://example.com/image2.jpg",
                "https://example.com/image3.jpg"
            ),
            heightFraction = 0.4f
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ImageGalleryPagerEmptyPreview() {
    VoxcinaTheme {
        ImageGalleryPager(
            images = emptyList(),
            heightFraction = 0.4f
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ImageGalleryPagerSingleImagePreview() {
    VoxcinaTheme {
        ImageGalleryPager(
            images = listOf("https://example.com/image1.jpg"),
            heightFraction = 0.4f
        )
    }
}
