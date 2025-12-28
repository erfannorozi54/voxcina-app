package com.voxcina.shop.presentation.productdetail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.voxcina.shop.domain.model.ProductAttribute
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.VoxcinaTheme

/**
 * Specification grid component displaying product attributes in a 3-column grid.
 * Includes a section header with icon.
 *
 * Requirements: 7.1, 7.2, 7.3
 *
 * @param attributes List of product attributes to display
 * @param modifier Modifier for the component
 */
@Composable
fun SpecificationGrid(
    attributes: List<ProductAttribute>,
    modifier: Modifier = Modifier
) {
    if (attributes.isEmpty()) return

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = modifier.fillMaxWidth()
        ) {
            // Section header
            SectionHeader(
                title = "مشخصات محصول",
                icon = Icons.Outlined.Info
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 3-column grid of specification cards
            // Using fixed height to avoid nested scrolling issues
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(calculateGridHeight(attributes.size)),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                userScrollEnabled = false
            ) {
                items(attributes) { attribute ->
                    SpecificationCard(
                        icon = getIconForAttribute(attribute.name),
                        label = getLocalizedLabel(attribute.name),
                        value = attribute.value
                    )
                }
            }
        }
    }
}

/**
 * Section header with icon and title.
 */
@Composable
private fun SectionHeader(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = Primary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Primary
        )
    }
}

/**
 * Calculates the grid height based on number of items.
 * Each row is approximately 120dp (card height + spacing).
 */
private fun calculateGridHeight(itemCount: Int): androidx.compose.ui.unit.Dp {
    val rows = (itemCount + 2) / 3 // Ceiling division for 3 columns
    return (rows * 130).dp
}

/**
 * Maps attribute names to localized Persian labels.
 */
private fun getLocalizedLabel(attributeName: String): String {
    return when (attributeName.lowercase()) {
        "material" -> "جنس"
        "weight" -> "وزن"
        "warranty" -> "گارانتی"
        "height" -> "ارتفاع"
        "width" -> "عرض"
        "care" -> "نگهداری"
        "origin" -> "کشور سازنده"
        "color" -> "رنگ"
        "size" -> "سایز"
        else -> attributeName
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFCFAF8)
@Composable
private fun SpecificationGridPreview() {
    VoxcinaTheme {
        SpecificationGrid(
            attributes = listOf(
                ProductAttribute(name = "height", value = "۱۲۰ سانتی‌متر"),
                ProductAttribute(name = "weight", value = "۱۵ کیلوگرم"),
                ProductAttribute(name = "warranty", value = "۲ ساله"),
                ProductAttribute(name = "material", value = "نخ ۱۰۰٪"),
                ProductAttribute(name = "origin", value = "ایران"),
                ProductAttribute(name = "care", value = "قابل شستشو")
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFCFAF8)
@Composable
private fun SpecificationGridFewItemsPreview() {
    VoxcinaTheme {
        SpecificationGrid(
            attributes = listOf(
                ProductAttribute(name = "material", value = "پنبه"),
                ProductAttribute(name = "warranty", value = "۱ ساله")
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}
