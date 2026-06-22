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
                    .padding(vertical = 10.dp, horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icon with gradient background
                Box(
                    modifier = Modifier
                        .size(32.dp)
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
                        modifier = Modifier.size(16.dp),
                        tint = Primary
                    )
                }
                
                androidx.compose.foundation.layout.Spacer(
                    modifier = Modifier.size(6.dp)
                )
                
                // Label
                Text(
                    text = label,
                    fontSize = 10.sp,
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
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryDark,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    lineHeight = 14.sp
                )
            }
        }
    }
}

/**
 * Maps common attribute names to appropriate icons.
 * Using Info icon for all attributes for consistency.
 */
fun getIconForAttribute(attributeName: String): ImageVector {
    return Icons.Outlined.Info
}
