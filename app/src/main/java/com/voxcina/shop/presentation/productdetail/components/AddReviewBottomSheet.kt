package com.voxcina.shop.presentation.productdetail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.voxcina.shop.ui.components.VoxcinaPrimaryButton
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.SecondaryLight
import com.voxcina.shop.ui.theme.VoxcinaTheme
import com.voxcina.shop.ui.theme.Warning

/**
 * Bottom sheet for adding a product review.
 * Contains star rating selector, comment input, and recommendation checkbox.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReviewBottomSheet(
    onDismiss: () -> Unit,
    onSubmit: (rating: Int, comment: String, isRecommended: Boolean) -> Unit,
    isSubmitting: Boolean,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var rating by remember { mutableIntStateOf(0) }
    var comment by remember { mutableStateOf("") }
    var isRecommended by remember { mutableStateOf(true) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SecondaryLight,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        modifier = modifier
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Title
                Text(
                    text = "ثبت نظر",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Star Rating
                Text(
                    text = "امتیاز شما",
                    style = MaterialTheme.typography.titleMedium,
                    color = Primary,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                StarRatingSelector(
                    rating = rating,
                    onRatingChanged = { rating = it }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Rating label
                Text(
                    text = getRatingLabel(rating),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Comment Input
                Text(
                    text = "نظر شما",
                    style = MaterialTheme.typography.titleMedium,
                    color = Primary,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    placeholder = {
                        Text(
                            text = "نظر خود را درباره این محصول بنویسید...",
                            color = Color.Gray
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Recommendation Checkbox
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .clickable { isRecommended = !isRecommended }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isRecommended,
                        onCheckedChange = { isRecommended = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Primary,
                            uncheckedColor = Color.Gray
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Icon(
                        imageVector = Icons.Outlined.ThumbUp,
                        contentDescription = null,
                        tint = if (isRecommended) Primary else Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "این محصول را پیشنهاد می‌کنم",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isRecommended) Primary else Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Submit Button
                VoxcinaPrimaryButton(
                    text = "ثبت نظر",
                    onClick = { onSubmit(rating, comment, isRecommended) },
                    enabled = rating > 0 && !isSubmitting,
                    isLoading = isSubmitting,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/**
 * Interactive star rating selector.
 */
@Composable
private fun StarRatingSelector(
    rating: Int,
    onRatingChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(5) { index ->
            val starIndex = index + 1
            Icon(
                imageVector = if (starIndex <= rating) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = "امتیاز $starIndex",
                modifier = Modifier
                    .size(40.dp)
                    .clickable { onRatingChanged(starIndex) },
                tint = if (starIndex <= rating) Warning else Color.Gray.copy(alpha = 0.4f)
            )
        }
    }
}

/**
 * Returns Persian label for rating value.
 */
private fun getRatingLabel(rating: Int): String = when (rating) {
    1 -> "خیلی بد"
    2 -> "بد"
    3 -> "متوسط"
    4 -> "خوب"
    5 -> "عالی"
    else -> "امتیاز خود را انتخاب کنید"
}

@Preview(showBackground = true)
@Composable
private fun AddReviewBottomSheetPreview() {
    VoxcinaTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(SecondaryLight)
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "ثبت نظر",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )

                Spacer(modifier = Modifier.height(24.dp))

                StarRatingSelector(
                    rating = 4,
                    onRatingChanged = {}
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "خوب",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    }
}
