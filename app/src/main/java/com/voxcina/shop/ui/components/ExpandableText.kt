package com.voxcina.shop.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.VoxcinaTheme

/**
 * A reusable expandable text component with maxLines truncation,
 * gradient fade overlay when truncated, and "بیشتر بخوانید" button.
 * Used for product descriptions and reviews.
 *
 * Requirements: 8.1, 8.2, 8.3
 *
 * @param text The text content to display
 * @param modifier Modifier for the component
 * @param maxLines Maximum lines to show when collapsed (default: 4)
 * @param isExpanded Whether the text is currently expanded
 * @param onExpandToggle Callback when expand/collapse is toggled
 * @param textStyle Text style for the content (default: bodyLarge)
 * @param textColor Color for the text (default: Gray)
 * @param expandButtonText Text for the expand button (default: "بیشتر بخوانید")
 * @param collapseButtonText Text for the collapse button (default: "کمتر")
 * @param buttonColor Color for the expand/collapse button (default: Primary)
 */
@Composable
fun ExpandableText(
    text: String,
    modifier: Modifier = Modifier,
    maxLines: Int = 4,
    isExpanded: Boolean = false,
    onExpandToggle: () -> Unit = {},
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    textColor: Color = Color.Gray,
    expandButtonText: String = "بیشتر بخوانید",
    collapseButtonText: String = "کمتر",
    buttonColor: Color = Primary
) {
    var isTextTruncated by remember { mutableStateOf(false) }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .animateContentSize(animationSpec = tween(300))
        ) {
            Box {
                Text(
                    text = text,
                    style = textStyle,
                    color = textColor,
                    maxLines = if (isExpanded) Int.MAX_VALUE else maxLines,
                    overflow = TextOverflow.Ellipsis,
                    onTextLayout = { textLayoutResult ->
                        isTextTruncated = textLayoutResult.hasVisualOverflow
                    }
                )
                
                // Gradient fade overlay when truncated and not expanded
                if (!isExpanded && isTextTruncated) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .align(Alignment.BottomCenter)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0f),
                                        Color.White.copy(alpha = 0.8f),
                                        Color.White
                                    )
                                )
                            )
                    )
                }
            }
            
            // Show expand/collapse button only if text is truncatable
            if (isTextTruncated || isExpanded) {
                Spacer(modifier = Modifier.height(4.dp))
                TextButton(
                    onClick = onExpandToggle,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = if (isExpanded) collapseButtonText else expandButtonText,
                        style = MaterialTheme.typography.labelLarge,
                        color = buttonColor
                    )
                }
            }
        }
    }
}

/**
 * Stateful version of ExpandableText that manages its own expanded state.
 * Useful when the parent doesn't need to control the expanded state.
 */
@Composable
fun ExpandableTextStateful(
    text: String,
    modifier: Modifier = Modifier,
    maxLines: Int = 4,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    textColor: Color = Color.Gray,
    expandButtonText: String = "بیشتر بخوانید",
    collapseButtonText: String = "کمتر",
    buttonColor: Color = Primary
) {
    var isExpanded by remember { mutableStateOf(false) }
    
    ExpandableText(
        text = text,
        modifier = modifier,
        maxLines = maxLines,
        isExpanded = isExpanded,
        onExpandToggle = { isExpanded = !isExpanded },
        textStyle = textStyle,
        textColor = textColor,
        expandButtonText = expandButtonText,
        collapseButtonText = collapseButtonText,
        buttonColor = buttonColor
    )
}

@Preview(showBackground = true)
@Composable
private fun ExpandableTextCollapsedPreview() {
    VoxcinaTheme {
        var isExpanded by remember { mutableStateOf(false) }
        
        ExpandableText(
            text = "این یک متن طولانی برای تست است که باید در چند خط نمایش داده شود. " +
                    "این متن شامل توضیحات کامل محصول است و ممکن است بیش از چهار خط باشد. " +
                    "کاربر می‌تواند با کلیک روی دکمه بیشتر بخوانید، متن کامل را مشاهده کند. " +
                    "این قابلیت برای نمایش توضیحات محصول و نظرات کاربران استفاده می‌شود. " +
                    "متن می‌تواند شامل جزئیات فنی، ویژگی‌ها و مزایای محصول باشد.",
            isExpanded = isExpanded,
            onExpandToggle = { isExpanded = !isExpanded },
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ExpandableTextExpandedPreview() {
    VoxcinaTheme {
        var isExpanded by remember { mutableStateOf(true) }
        
        ExpandableText(
            text = "این یک متن طولانی برای تست است که باید در چند خط نمایش داده شود. " +
                    "این متن شامل توضیحات کامل محصول است و ممکن است بیش از چهار خط باشد. " +
                    "کاربر می‌تواند با کلیک روی دکمه بیشتر بخوانید، متن کامل را مشاهده کند.",
            isExpanded = isExpanded,
            onExpandToggle = { isExpanded = !isExpanded },
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ExpandableTextShortPreview() {
    VoxcinaTheme {
        ExpandableTextStateful(
            text = "این یک متن کوتاه است که نیاز به گسترش ندارد.",
            modifier = Modifier.padding(16.dp)
        )
    }
}
