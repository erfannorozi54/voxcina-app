package com.voxcina.shop.presentation.productdetail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.sp
import com.voxcina.shop.ui.components.RatingBadge
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.VoxcinaTheme

/**
 * Product header component displaying product name, brand/subtitle, and rating badge.
 * Uses RTL layout with rating badge positioned on the left side.
 *
 * Requirements: 3.1, 3.2, 3.3, 3.4
 *
 * @param name Product name displayed in bold 24sp
 * @param brand Product brand/subtitle displayed below the name
 * @param rating Average rating value (nullable if no ratings)
 * @param reviewCount Number of reviews (nullable if no reviews)
 * @param modifier Modifier for the component
 */
@Composable
fun ProductHeader(
    name: String,
    brand: String,
    rating: Float?,
    reviewCount: Int?,
    modifier: Modifier = Modifier
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            // Product name and brand (right side in RTL)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                // Product name - 20sp bold
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 20.sp
                    ),
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
                
                // Brand/subtitle - 12sp regular gray
                Text(
                    text = brand,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            
            // Rating badge (left side in RTL)
            if (rating != null && reviewCount != null && reviewCount > 0) {
                RatingBadge(
                    rating = rating,
                    reviewCount = reviewCount,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }
}
