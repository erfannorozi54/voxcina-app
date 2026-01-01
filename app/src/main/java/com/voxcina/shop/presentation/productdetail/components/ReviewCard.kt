package com.voxcina.shop.presentation.productdetail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.voxcina.shop.domain.model.ProductReview
import com.voxcina.shop.ui.components.SoftShadowCard
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.Success
import com.voxcina.shop.ui.theme.VoxcinaTheme
import com.voxcina.shop.ui.theme.Warning

/**
 * Review card component displaying a single user review with avatar,
 * name, star rating row, comment, and recommendation badge.
 *
 * @param review The product review to display
 * @param modifier Modifier for the component
 */
@Composable
fun ReviewCard(
    review: ProductReview,
    modifier: Modifier = Modifier
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        SoftShadowCard(
            modifier = modifier.widthIn(min = 260.dp, max = 300.dp),
            cornerRadius = 16.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Header row with avatar and name
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(review.userAvatar)
                            .crossfade(true)
                            .build(),
                        contentDescription = "تصویر ${review.userName}",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Primary.copy(alpha = 0.1f)),
                        contentScale = ContentScale.Crop
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        // User name
                        Text(
                            text = review.userName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Primary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        // Star rating row
                        StarRatingRow(rating = review.rating)
                    }
                    
                    // Recommendation badge
                    if (review.isRecommended) {
                        RecommendationBadge()
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Comment text
                Text(
                    text = review.comment,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * Star rating row displaying filled and outlined stars.
 */
@Composable
private fun StarRatingRow(
    rating: Int,
    modifier: Modifier = Modifier,
    maxRating: Int = 5
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        repeat(maxRating) { index ->
            Icon(
                imageVector = if (index < rating) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = if (index < rating) Warning else Color.Gray.copy(alpha = 0.4f)
            )
        }
    }
}

/**
 * Small badge indicating the user recommends this product.
 */
@Composable
private fun RecommendationBadge(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Success.copy(alpha = 0.1f))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.ThumbUp,
            contentDescription = "پیشنهاد می‌کنم",
            modifier = Modifier.size(12.dp),
            tint = Success
        )
        Text(
            text = "پیشنهاد",
            style = MaterialTheme.typography.labelSmall,
            color = Success,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFCFAF8)
@Composable
private fun ReviewCardPreview() {
    VoxcinaTheme {
        ReviewCard(
            review = ProductReview(
                id = "1",
                userId = "user1",
                userName = "علی محمدی",
                userAvatar = null,
                rating = 5,
                comment = "محصول عالی بود! کیفیت پارچه خیلی خوبه و سایزش دقیقا اندازه بود. پیشنهاد میکنم.",
                isRecommended = true,
                createdAt = "2024-01-15"
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFCFAF8)
@Composable
private fun ReviewCardNoRecommendPreview() {
    VoxcinaTheme {
        ReviewCard(
            review = ProductReview(
                id = "2",
                userId = "user2",
                userName = "مریم احمدی",
                userAvatar = null,
                rating = 3,
                comment = "کیفیت متوسط بود. انتظار بیشتری داشتم.",
                isRecommended = false,
                createdAt = "2024-01-10"
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}
