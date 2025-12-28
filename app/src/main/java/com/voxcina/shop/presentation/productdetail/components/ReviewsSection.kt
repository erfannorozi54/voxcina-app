package com.voxcina.shop.presentation.productdetail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.voxcina.shop.domain.model.ProductReview
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.VoxcinaTheme

/**
 * Reviews section component with section header, "مشاهده همه" button,
 * and horizontal scrollable LazyRow of ReviewCards.
 *
 * Requirements: 9.1, 9.2, 9.3
 *
 * @param reviews List of product reviews to display
 * @param onViewAllClick Callback when "مشاهده همه" button is clicked
 * @param modifier Modifier for the component
 */
@Composable
fun ReviewsSection(
    reviews: List<ProductReview>,
    onViewAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (reviews.isEmpty()) return

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = modifier.fillMaxWidth()
        ) {
            // Section header with "مشاهده همه" button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Title with icon
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = Primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "نظرات کاربران",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                }
                
                // "مشاهده همه" button
                TextButton(onClick = onViewAllClick) {
                    Text(
                        text = "مشاهده همه",
                        style = MaterialTheme.typography.labelLarge,
                        color = Primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = Primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Horizontal scrollable list of review cards
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(reviews) { review ->
                    ReviewCard(review = review)
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFCFAF8)
@Composable
private fun ReviewsSectionPreview() {
    VoxcinaTheme {
        ReviewsSection(
            reviews = listOf(
                ProductReview(
                    id = "1",
                    userId = "user1",
                    userName = "علی محمدی",
                    userAvatar = null,
                    rating = 5,
                    comment = "محصول عالی بود! کیفیت پارچه خیلی خوبه و سایزش دقیقا اندازه بود.",
                    createdAt = "2024-01-15"
                ),
                ProductReview(
                    id = "2",
                    userId = "user2",
                    userName = "مریم احمدی",
                    userAvatar = null,
                    rating = 4,
                    comment = "کیفیت خوب بود. ارسال سریع و بسته‌بندی مناسب.",
                    createdAt = "2024-01-10"
                ),
                ProductReview(
                    id = "3",
                    userId = "user3",
                    userName = "رضا کریمی",
                    userAvatar = null,
                    rating = 5,
                    comment = "خیلی راضی هستم. پیشنهاد می‌کنم.",
                    createdAt = "2024-01-05"
                )
            ),
            onViewAllClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFCFAF8)
@Composable
private fun ReviewsSectionSingleReviewPreview() {
    VoxcinaTheme {
        ReviewsSection(
            reviews = listOf(
                ProductReview(
                    id = "1",
                    userId = "user1",
                    userName = "علی محمدی",
                    userAvatar = null,
                    rating = 5,
                    comment = "محصول عالی بود!",
                    createdAt = "2024-01-15"
                )
            ),
            onViewAllClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
