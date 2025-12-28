package com.voxcina.shop.presentation.productdetail.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.voxcina.shop.ui.components.ExpandableText
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.VoxcinaTheme

/**
 * Description section component with section header and expandable text.
 * Reuses ExpandableText from ui/components/.
 *
 * Requirements: 8.1, 8.2, 8.3
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
            // Section header with description icon
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "توضیحات محصول",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Expandable description text
            ExpandableText(
                text = description,
                isExpanded = isExpanded,
                onExpandToggle = onExpandToggle,
                maxLines = 4
            )
        }
    }
}

@Preview(showBackground = true)
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

@Preview(showBackground = true)
@Composable
private fun DescriptionSectionExpandedPreview() {
    VoxcinaTheme {
        DescriptionSection(
            description = "این تیشرت مردانه از جنس نخ ۱۰۰٪ با کیفیت بالا ساخته شده است. " +
                    "طراحی کلاسیک و راحت آن برای استفاده روزمره مناسب است. " +
                    "این محصول قابل شستشو با ماشین بوده و رنگ آن پس از شستشو ثابت می‌ماند.",
            isExpanded = true,
            onExpandToggle = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
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
