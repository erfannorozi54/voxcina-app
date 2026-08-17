package com.voxcina.shop.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.voxcina.shop.R
import com.voxcina.shop.domain.model.Category
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.PrimaryDark
import com.voxcina.shop.ui.theme.VoxcinaTheme

/**
 * Category section component displaying categories in a horizontally scrollable row.
 * Implements Requirements 3.2, 3.5, 3.6 from the home screen spec.
 *
 * @param categories List of categories to display
 * @param modifier Modifier for the section container
 * @param onCategoryClick Callback when a category is clicked with category ID
 * @param onViewAllClick Callback when "View All" button is clicked
 */
@Composable
fun CategorySection(
    categories: List<Category>,
    modifier: Modifier = Modifier,
    onCategoryClick: (String) -> Unit = {},
    onViewAllClick: () -> Unit = {}
) {
    if (categories.isEmpty()) return
    
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = modifier.fillMaxWidth()
        ) {
            // Section header with title and "View All" button
            SectionHeader(
                title = stringResource(R.string.home_categories_title),
                onViewAllClick = onViewAllClick,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Horizontally scrollable category row without visible scrollbar
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(
                    items = categories,
                    key = { _, item -> item.id }
                ) { index, category ->
                    CategoryItem(
                        category = category,
                        index = index,
                        onClick = { onCategoryClick(category.id) }
                    )
                }
            }
        }
    }
}


/**
 * Section header with title and "View All" button.
 * Reusable component for all home screen sections.
 *
 * @param title Section title text
 * @param modifier Modifier for the header row
 * @param onViewAllClick Callback when "View All" button is clicked
 */
@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    onViewAllClick: () -> Unit = {}
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = PrimaryDark,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        
        TextButton(
            onClick = onViewAllClick
        ) {
            Text(
                text = stringResource(R.string.home_view_all),
                color = Primary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Filled.KeyboardArrowLeft,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

/**
 * Individual category item with a gradient-ringed avatar and name.
 * Mirrors the storefront's ModernCategoriesSection treatment: a white circle
 * holding the category avatar (fetched from the backend), wrapped in a
 * per-index gradient ring with a soft coloured shadow.
 *
 * @param category The category data to display
 * @param index Position in the list, used to cycle the ring gradient palette
 * @param onClick Callback when the category is clicked
 * @param modifier Modifier for the item container
 */
@Composable
fun CategoryItem(
    category: Category,
    index: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val palette = CATEGORY_RING_PALETTE[index % CATEGORY_RING_PALETTE.size]
    val avatarUrl = category.avatarUrl?.let {
        if (it.startsWith("http")) it else "https://voxcina.com$it"
    }
    // White-variant avatars are invisible on a white circle, so back them
    // with the brand blue; colored avatars sit on a plain white circle.
    val isWhiteVariant = category.avatarUrl?.endsWith("-white.svg") == true

    Column(
        modifier = modifier
            .width(64.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Gradient ring around a white circle holding the category avatar
        Box(
            modifier = Modifier
                .size(58.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = CircleShape,
                    clip = false,
                    ambientColor = palette.shadowColor.copy(alpha = 0.6f),
                    spotColor = palette.shadowColor.copy(alpha = 0.6f)
                )
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(palette.startColor, palette.endColor)
                    ),
                    shape = CircleShape
                )
                .padding(2.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(
                        if (isWhiteVariant) {
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF2563EB),
                                    Color(0xFF3B82F6).copy(alpha = 0.85f)
                                )
                            )
                        } else {
                            Brush.linearGradient(colors = listOf(Color.White, Color.White))
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (avatarUrl != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(avatarUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = category.name,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Sell,
                        contentDescription = category.name,
                        tint = palette.startColor.copy(alpha = 0.4f),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Category name below icon
        Text(
            text = category.name,
            color = PrimaryDark,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/** Gradient ring palette cycled by index, mirroring the storefront's avatar rings. */
private data class CategoryRingPalette(
    val startColor: Color,
    val endColor: Color,
    val shadowColor: Color
)

private val CATEGORY_RING_PALETTE = listOf(
    CategoryRingPalette(Color(0xFF2563EB), Color(0xFF3B82F6), Color(0xFFBFDBFE)),
    CategoryRingPalette(Color(0xFFFB7185), Color(0xFFEC4899), Color(0xFFFBCFE8)),
    CategoryRingPalette(Color(0xFFFBBF24), Color(0xFFF97316), Color(0xFFFDE68A)),
    CategoryRingPalette(Color(0xFF34D399), Color(0xFF14B8A6), Color(0xFFA7F3D0)),
    CategoryRingPalette(Color(0xFFA78BFA), Color(0xFFA855F7), Color(0xFFDDD6FE)),
    CategoryRingPalette(Color(0xFF38BDF8), Color(0xFF06B6D4), Color(0xFFBAE6FD)),
    CategoryRingPalette(Color(0xFFE879F9), Color(0xFFEC4899), Color(0xFFF5D0FE)),
    CategoryRingPalette(Color(0xFF818CF8), Color(0xFF3B82F6), Color(0xFFC7D2FE))
)

