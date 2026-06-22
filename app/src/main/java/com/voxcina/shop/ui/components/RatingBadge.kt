package com.voxcina.shop.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.VoxcinaTheme
import com.voxcina.shop.ui.theme.Warning
import com.voxcina.shop.util.PersianDigitConverter

/**
 * A reusable rating badge component displaying a star icon with numeric rating value
 * and review count. Used in product cards and product details.
 *
 * Requirements: 3.3, 3.4
 *
 * @param rating The average rating value (e.g., 4.5)
 * @param reviewCount The number of reviews
 * @param modifier Modifier for the component
 * @param starColor Color for the star icon (default: Warning/Amber)
 * @param ratingColor Color for the rating text (default: Primary)
 * @param reviewCountColor Color for the review count text (default: Gray)
 * @param showBorder Whether to show a subtle border around the badge (default: true)
 */
@Composable
fun RatingBadge(
    rating: Float,
    reviewCount: Int,
    modifier: Modifier = Modifier,
    starColor: Color = Warning,
    ratingColor: Color = Primary,
    reviewCountColor: Color = Color.Gray,
    showBorder: Boolean = true
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        val shape = RoundedCornerShape(8.dp)
        
        Column(
            modifier = modifier
                .then(
                    if (showBorder) {
                        Modifier.border(
                            width = 1.dp,
                            color = Color.Gray.copy(alpha = 0.2f),
                            shape = shape
                        )
                    } else {
                        Modifier
                    }
                )
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            // Rating row with star and value
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = starColor
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = PersianDigitConverter.toPersianDigits(
                        String.format("%.1f", rating)
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = ratingColor
                )
            }
            
            // Review count
            Text(
                text = formatReviewCount(reviewCount),
                style = MaterialTheme.typography.labelSmall,
                color = reviewCountColor
            )
        }
    }
}

/**
 * Formats the review count for display.
 * Shows "120+" format for counts over 99.
 */
private fun formatReviewCount(count: Int): String {
    return if (count > 99) {
        PersianDigitConverter.toPersianDigits("${(count / 10) * 10}+")
    } else {
        PersianDigitConverter.toPersianDigits(count.toString())
    }
}
