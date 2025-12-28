package com.voxcina.shop.presentation.productdetail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.voxcina.shop.ui.components.SoftShadowCard
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.Primary100
import com.voxcina.shop.ui.theme.VoxcinaTheme

/**
 * Specification card component displaying an icon in a colored circle,
 * label, and value. Uses SoftShadowCard for card styling.
 *
 * Requirements: 7.2, 7.3
 *
 * @param icon Icon to display in the colored circle
 * @param label Specification label (e.g., "جنس", "وزن")
 * @param value Specification value (e.g., "نخ", "۱۵ کیلوگرم")
 * @param modifier Modifier for the component
 * @param iconBackgroundColor Background color for the icon circle (default: Primary100)
 * @param iconTint Tint color for the icon (default: Primary)
 */
@Composable
fun SpecificationCard(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    iconBackgroundColor: Color = Primary100,
    iconTint: Color = Primary
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        SoftShadowCard(
            modifier = modifier,
            cornerRadius = 16.dp
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Icon in colored circle
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = iconBackgroundColor,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = iconTint
                    )
                }
                
                // Label
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                // Value
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Primary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * Maps common attribute names to appropriate icons.
 */
fun getIconForAttribute(attributeName: String): ImageVector {
    return when (attributeName.lowercase()) {
        "material", "جنس" -> Icons.Outlined.Build
        "weight", "وزن" -> Icons.Outlined.Info
        "warranty", "گارانتی" -> Icons.Outlined.Star
        "height", "ارتفاع" -> Icons.Outlined.Info
        "width", "عرض" -> Icons.Outlined.Info
        "care", "نگهداری" -> Icons.Outlined.Info
        "origin", "کشور سازنده" -> Icons.Outlined.Info
        "date", "تاریخ" -> Icons.Outlined.DateRange
        else -> Icons.Outlined.Info
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFCFAF8)
@Composable
private fun SpecificationCardPreview() {
    VoxcinaTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SpecificationCard(
                icon = Icons.Outlined.Info,
                label = "ارتفاع",
                value = "۱۲۰ سانتی‌متر"
            )
            
            SpecificationCard(
                icon = Icons.Outlined.Build,
                label = "جنس",
                value = "نخ ۱۰۰٪"
            )
            
            SpecificationCard(
                icon = Icons.Outlined.Star,
                label = "گارانتی",
                value = "۲ ساله"
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFCFAF8)
@Composable
private fun SpecificationCardLongValuePreview() {
    VoxcinaTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SpecificationCard(
                icon = Icons.Outlined.Info,
                label = "نگهداری",
                value = "قابل شستشو با ماشین"
            )
        }
    }
}
