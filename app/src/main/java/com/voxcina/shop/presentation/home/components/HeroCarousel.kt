package com.voxcina.shop.presentation.home.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.voxcina.shop.R
import com.voxcina.shop.domain.model.HeroImage
import com.voxcina.shop.domain.model.defaultHeroContent
import com.voxcina.shop.domain.model.normalized
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.VoxcinaTheme

/**
 * Data class for local hero banner images.
 * Used when displaying banners from local resources instead of API.
 */
data class LocalHeroBanner(
    val id: String,
    val imageFileName: String,
    val showGradient: Boolean = true
)

/**
 * Default local hero banners from res/raw/images/banners/ directory.
 * These are used for design/development when API is not available.
 */
val defaultLocalBanners = listOf(
    LocalHeroBanner(id = "1", imageFileName = "FinalB1.webp"),
    LocalHeroBanner(id = "2", imageFileName = "FinalB2.webp"),
    LocalHeroBanner(id = "3", imageFileName = "heroheader.jpeg"),
    LocalHeroBanner(id = "4", imageFileName = "sidecover.webp")
)

/**
 * Hero carousel component displaying promotional banners in a horizontally swipeable carousel.
 * Implements Requirements 2.2, 2.3, 2.4 from the home screen spec.
 *
 * Renders the desktop hero variant: a 16:9 slide with the authored content
 * (badge, headings, paragraphs, buttons) layered on the image.
 *
 * @param heroImages List of hero images to display
 * @param modifier Modifier for the carousel container
 * @param onImageClick Callback when a hero image is clicked
 * @param onLinkClick Callback with a button href when a hero CTA is clicked
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HeroCarousel(
    heroImages: List<HeroImage>,
    modifier: Modifier = Modifier,
    onImageClick: (HeroImage) -> Unit = {},
    onLinkClick: (String) -> Unit = {}
) {
    if (heroImages.isEmpty()) return
    
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { heroImages.size }
    )
    
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Hero image pager with 16:9 aspect ratio (desktop variant)
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) { page ->
                HeroImageCard(
                    heroImage = heroImages[page],
                    onClick = { onImageClick(heroImages[page]) },
                    onLinkClick = onLinkClick
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Page indicator dots
            PageIndicator(
                pagerState = pagerState,
                pageCount = heroImages.size
            )
        }
    }
}

/**
 * Individual hero image card rendering the desktop hero slide.
 * Shows the image, authored background/overlay gradients and the text content.
 *
 * @param heroImage The hero image data to display
 * @param onClick Callback when the card is clicked
 * @param onLinkClick Callback with a button href when a hero CTA is clicked
 * @param modifier Modifier for the card
 */
@Composable
private fun HeroImageCard(
    heroImage: HeroImage,
    onClick: () -> Unit,
    onLinkClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val imageUrl = if (heroImage.imageUrl.startsWith("http")) {
        heroImage.imageUrl
    } else {
        "https://voxcina.com${heroImage.imageUrl}"
    }

    val hasAuthoredContent = heroImage.content != null
    val content = heroImage.content?.normalized() ?: defaultHeroContent()

    BoxWithConstraints(
        modifier = modifier.fillMaxWidth()
    ) {
        // Phones get a taller hero so all authored content stays visible;
        // wide screens keep the 16:9 desktop aspect-video ratio.
        val aspect = if (maxWidth < 480.dp) 0.8f else 16f / 9f

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(aspect)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF111827), Color(0xFF1E3A8A), Color(0xFF111827))
                    )
                )
                .clickable(onClick = onClick)
        ) {
            // Authored section background gradient behind the image
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = heroGradientBrush(
                            from = content.background.from,
                            via = content.background.via,
                            to = content.background.to,
                            direction = content.background.direction
                        )
                    )
            ) {
                // Hero image using Coil with crossfade
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    alpha = if (hasAuthoredContent) content.imageOpacity / 100f else 0.3f
                )

                // Authored overlay gradient, or the legacy bottom-dark gradient
                if (hasAuthoredContent) {
                    if (content.overlay.enabled) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = heroGradientBrush(
                                        from = content.overlay.from,
                                        via = content.overlay.via,
                                        to = content.overlay.to,
                                        direction = content.overlay.direction
                                    ),
                                    alpha = content.overlay.opacity / 100f
                                )
                        )
                    }
                } else if (!heroImage.noGradient) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.3f),
                                        Color.Black.copy(alpha = 0.6f)
                                    ),
                                    startY = 0f,
                                    endY = Float.POSITIVE_INFINITY
                                )
                            )
                    )
                }
            }

            if (content.showDecorations) {
                HeroDecorations()
            }

            // Authored text content (badge, headings, paragraphs, buttons)
            HeroContentOverlay(
                content = content,
                onLinkClick = onLinkClick
            )
        }
    }
}

/**
 * Hero carousel component using local banner images from res/raw/images/banners/.
 * Use this for design/development when API is not available.
 *
 * @param localBanners List of local banner configurations
 * @param modifier Modifier for the carousel container
 * @param onBannerClick Callback when a banner is clicked
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LocalHeroCarousel(
    localBanners: List<LocalHeroBanner> = defaultLocalBanners,
    modifier: Modifier = Modifier,
    onBannerClick: (LocalHeroBanner) -> Unit = {}
) {
    if (localBanners.isEmpty()) return
    
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { localBanners.size }
    )
    
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Hero image pager with 2:1 aspect ratio
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) { page ->
                LocalHeroImageCard(
                    banner = localBanners[page],
                    onClick = { onBannerClick(localBanners[page]) }
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Page indicator dots
            PageIndicator(
                pagerState = pagerState,
                pageCount = localBanners.size
            )
        }
    }
}

/**
 * Individual hero image card using local banner from res/raw/images/banners/.
 *
 * @param banner The local banner configuration
 * @param onClick Callback when the card is clicked
 * @param modifier Modifier for the card
 */
@Composable
private fun LocalHeroImageCard(
    banner: LocalHeroBanner,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    // Build the file path for the local banner image
    val imagePath = "file:///android_asset/../res/raw/images/banners/${banner.imageFileName}"
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(2f) // 2:1 aspect ratio as per requirements
            .clip(RoundedCornerShape(16.dp))
    ) {
        // Load local banner image using Coil
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data("file:///android_res/raw/images/banners/${banner.imageFileName}")
                .crossfade(true)
                .build(),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        // Gradient overlay for text readability
        if (banner.showGradient) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.3f),
                                Color.Black.copy(alpha = 0.6f)
                            ),
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY
                        )
                    )
            )
        }
    }
}

/**
 * Page indicator dots showing current position in the carousel.
 * Implements Requirement 2.5 from the home screen spec.
 *
 * @param pagerState The pager state to track current page
 * @param pageCount Total number of pages
 * @param modifier Modifier for the indicator row
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PageIndicator(
    pagerState: PagerState,
    pageCount: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            val isActive = pagerState.currentPage == index
            Box(
                modifier = Modifier
                    .size(if (isActive) 10.dp else 8.dp)
                    .clip(CircleShape)
                    .background(
                        color = if (isActive) Primary else Color.Gray.copy(alpha = 0.4f)
                    )
            )
        }
    }
}

/**
 * Standalone page indicator for use outside of HeroCarousel.
 * Implements Requirement 2.5 from the home screen spec.
 *
 * @param currentPage Current active page index
 * @param pageCount Total number of pages
 * @param modifier Modifier for the indicator row
 * @param activeColor Color for the active dot
 * @param inactiveColor Color for inactive dots
 */
@Composable
fun PageIndicator(
    currentPage: Int,
    pageCount: Int,
    modifier: Modifier = Modifier,
    activeColor: Color = Primary,
    inactiveColor: Color = Color.Gray.copy(alpha = 0.4f)
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            val isActive = currentPage == index
            Box(
                modifier = Modifier
                    .size(if (isActive) 10.dp else 8.dp)
                    .clip(CircleShape)
                    .background(color = if (isActive) activeColor else inactiveColor)
            )
        }
    }
}

