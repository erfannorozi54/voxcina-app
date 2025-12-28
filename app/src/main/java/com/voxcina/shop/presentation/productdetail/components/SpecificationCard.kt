package com.voxcina.shop.presentation.productdetail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.PrimaryDark
import com.voxcina.shop.ui.theme.VoxcinaTheme

/**
 * Modern specification card with consistent Primary color styling.
 *
 * @param icon Icon to display
 * @param label Specification label
 * @param value Specification value
 * @param modifier Modifier for the component
 */
@Composable
fun SpecificationCard(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(16.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Primary.copy(alpha = 0.06f),
                            Primary.copy(alpha = 0.02f)
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .border(
                    width = 1.dp,
                    color = Primary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 14.dp, horizontal = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icon with gradient background
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Primary.copy(alpha = 0.12f),
                                    Primary.copy(alpha = 0.2f)
                                )
                            ),
                            shape = CircleShape
                        )
                        .border(
                            width = 1.dp,
                            color = Primary.copy(alpha = 0.2f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = Primary
                    )
                }
                
                androidx.compose.foundation.layout.Spacer(
                    modifier = Modifier.size(8.dp)
                )
                
                // Label
                Text(
                    text = label,
                    fontSize = 11.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
                
                androidx.compose.foundation.layout.Spacer(
                    modifier = Modifier.size(2.dp)
                )
                
                // Value
                Text(
                    text = value,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryDark,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
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
        "material", "جنس" -> Icons.Rounded.Settings
        "weight", "وزن" -> Icons.Outlined.Info
        "warranty", "گارانتی" -> Icons.Rounded.CheckCircle
        "height", "ارتفاع" -> Icons.Outlined.Info
        "width", "عرض" -> Icons.Outlined.Info
        "care", "نگهداری", "قابلیت شستشو" -> Icons.Rounded.CheckCircle
        "origin", "کشور سازنده" -> Icons.Outlined.Info
        "date", "تاریخ" -> Icons.Outlined.DateRange
        "fit", "نوع قواره" -> Icons.Rounded.ShoppingCart
        "season", "فصل‌ها" -> Icons.Outlined.Star
        "thickness", "ضخامت پیراهن" -> Icons.Outlined.Info
        else -> Icons.Outlined.Info
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFCFAF8)
@Composable
private fun SpecificationCardPreview() {
    VoxcinaTheme {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SpecificationCard(
                icon = Icons.Rounded.Settings,
                label = "جنس",
                value = "نخ‌پنبه"
            )
            
            SpecificationCard(
                icon = Icons.Rounded.CheckCircle,
                label = "قابلیت شستشو",
                value = "دارد"
            )
            
            SpecificationCard(
                icon = Icons.Rounded.ShoppingCart,
                label = "نوع قواره",
                value = "استاندارد"
            )
        }
    }
}
