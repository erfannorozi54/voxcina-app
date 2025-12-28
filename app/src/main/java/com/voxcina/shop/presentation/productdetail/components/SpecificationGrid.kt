package com.voxcina.shop.presentation.productdetail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.voxcina.shop.domain.model.ProductAttribute
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.PrimaryDark
import com.voxcina.shop.ui.theme.VoxcinaTheme

/**
 * Modern specification grid with gradient header and colorful cards.
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
            // Modern section header with gradient accent
            ModernSectionHeader(
                title = "مشخصات محصول",
                icon = Icons.Rounded.Info
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 3-column grid with consistent styling
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(calculateGridHeight(attributes.size)),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                userScrollEnabled = false
            ) {
                items(attributes) { attribute ->
                    val localizedLabel = getLocalizedLabel(attribute.name)
                    SpecificationCard(
                        icon = getIconForAttribute(attribute.name),
                        label = localizedLabel,
                        value = attribute.value
                    )
                }
            }
        }
    }
}

/**
 * Modern section header with gradient background accent.
 */
@Composable
private fun ModernSectionHeader(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon with gradient background
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
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = Color.White
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Text(
            text = title,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryDark
        )
    }
}

/**
 * Calculates the grid height based on number of items.
 * Each row needs ~105dp (card height + spacing).
 */
private fun calculateGridHeight(itemCount: Int): androidx.compose.ui.unit.Dp {
    val rows = (itemCount + 2) / 3
    return (rows * 105 + 10).dp // Added extra padding to prevent cropping
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
        "care" -> "قابلیت شستشو"
        "origin" -> "کشور سازنده"
        "color" -> "رنگ"
        "size" -> "سایز"
        "fit" -> "نوع قواره"
        "season" -> "فصل‌ها"
        "thickness" -> "ضخامت"
        else -> attributeName
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFCFAF8)
@Composable
private fun SpecificationGridPreview() {
    VoxcinaTheme {
        SpecificationGrid(
            attributes = listOf(
                ProductAttribute(name = "material", value = "نخ‌پنبه"),
                ProductAttribute(name = "care", value = "دارد"),
                ProductAttribute(name = "fit", value = "استاندارد\n(Regular Fit)"),
                ProductAttribute(name = "season", value = "تابستان، بهار"),
                ProductAttribute(name = "thickness", value = "نازک")
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
