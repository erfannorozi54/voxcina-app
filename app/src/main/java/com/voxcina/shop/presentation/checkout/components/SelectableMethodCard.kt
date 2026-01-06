package com.voxcina.shop.presentation.checkout.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.voxcina.shop.ui.components.SoftShadowCard
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.Primary100

/**
 * Reusable selectable method card for shipping and payment methods.
 */
@Composable
fun SelectableMethodCard(
    title: String,
    description: String,
    icon: ImageVector,
    isSelected: Boolean,
    isEnabled: Boolean = true,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    trailingContent: @Composable (() -> Unit)? = null,
    iconContent: @Composable (() -> Unit)? = null
) {
    val alpha = if (isEnabled) 1f else 0.5f
    val borderColor = if (isSelected) Primary else Color.Transparent
    val backgroundColor = if (isSelected) Primary100.copy(alpha = 0.5f) else Color.White

    SoftShadowCard(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 2.dp,
                color = borderColor.copy(alpha = alpha),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(enabled = true, onClick = onClick),
        cornerRadius = 12.dp,
        backgroundColor = backgroundColor.copy(alpha = alpha)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Radio button
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                enabled = isEnabled,
                colors = RadioButtonDefaults.colors(
                    selectedColor = Primary,
                    unselectedColor = Color(0xFFE5E7EB)
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                if (iconContent != null) {
                    iconContent()
                } else {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = Primary.copy(alpha = alpha)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Primary.copy(alpha = alpha)
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray.copy(alpha = alpha)
                )
            }

            // Trailing content (e.g., price)
            trailingContent?.invoke()
        }
    }
}
