package com.voxcina.shop.presentation.productdetail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.voxcina.shop.ui.components.ExpandableText
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.PrimaryDark
import com.voxcina.shop.ui.theme.VoxcinaTheme

/**
 * Modern description section with gradient header and styled container.
 *
 * @param description The product description text
 * @param isExpanded Whether the description is currently expanded
 * @param onExpandToggle Callback when expand/collapse is toggled
 * @param modifier Modifier for the component
 */
@Composable
fun DescriptionSection(
    description: String,
    isExpanded: Boolean,
    onExpandToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (description.isBlank()) return

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = modifier.fillMaxWidth()
        ) {
            // Modern section header
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Primary,
                                    Primary.copy(alpha = 0.7f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Info,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = Color.White
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = "توضیحات محصول",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryDark
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Description container with subtle styling
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Primary.copy(alpha = 0.04f),
                                Primary.copy(alpha = 0.01f)
                            )
                        )
                    )
                    .border(
                        width = 1.dp,
                        color = Primary.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp)
            ) {
                ExpandableText(
                    text = description,
                    isExpanded = isExpanded,
                    onExpandToggle = onExpandToggle,
                    maxLines = 4
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFCFAF8)
@Composable
private fun DescriptionSectionCollapsedPreview() {
    VoxcinaTheme {
        DescriptionSection(
            description = "این تیشرت مردانه از جنس نخ ۱۰۰٪ با کیفیت بالا ساخته شده است. " +
                    "طراحی کلاسیک و راحت آن برای استفاده روزمره مناسب است. " +
                    "این محصول قابل شستشو با ماشین بوده و رنگ آن پس از شستشو ثابت می‌ماند. " +
                    "سایزبندی استاندارد و مناسب برای تمام اندام‌ها. " +
                    "گارانتی ۲ ساله تعویض در صورت وجود عیب تولید.",
            isExpanded = false,
            onExpandToggle = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFCFAF8)
@Composable
private fun DescriptionSectionExpandedPreview() {
    VoxcinaTheme {
        DescriptionSection(
            description = "پیراهن مردانه آستین‌بلند ساده نخ‌پنبه",
            isExpanded = true,
            onExpandToggle = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFCFAF8)
@Composable
private fun DescriptionSectionShortPreview() {
    VoxcinaTheme {
        DescriptionSection(
            description = "تیشرت مردانه با کیفیت بالا.",
            isExpanded = false,
            onExpandToggle = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
