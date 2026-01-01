package com.voxcina.shop.presentation.profile.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.Wallet
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.voxcina.shop.ui.components.SoftShadowCard
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.VoxcinaTheme
import com.voxcina.shop.util.PersianDigitConverter

/**
 * A card component displaying a single statistic with icon, value, and label.
 * Used for wallet balance, loyalty points, and active coupons in the profile screen.
 *
 * @param icon The icon to display
 * @param value The numeric value to display (will be converted to Persian digits)
 * @param label The label text below the value
 * @param onClick Callback when the card is clicked
 * @param modifier Modifier for the card
 * @param iconTint Tint color for the icon (default: Primary)
 */
@Composable
fun QuickStatCard(
    icon: ImageVector,
    value: String,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconTint: Color = Primary
) {
    SoftShadowCard(
        modifier = modifier.clickable(onClick = onClick),
        cornerRadius = 16.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(24.dp),
                tint = iconTint
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = PersianDigitConverter.toPersianDigits(value),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 11.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFCFAF8)
@Composable
private fun QuickStatCardPreview() {
    VoxcinaTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickStatCard(
                icon = Icons.Outlined.Wallet,
                value = "250000",
                label = "تومان موجودی",
                onClick = {},
                modifier = Modifier.width(100.dp)
            )
            
            QuickStatCard(
                icon = Icons.Outlined.CardGiftcard,
                value = "1500",
                label = "امتیاز باشگاه",
                onClick = {},
                modifier = Modifier.width(100.dp)
            )
            
            QuickStatCard(
                icon = Icons.Outlined.ConfirmationNumber,
                value = "3",
                label = "کوپن فعال",
                onClick = {},
                modifier = Modifier.width(100.dp)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFCFAF8)
@Composable
private fun QuickStatCardSinglePreview() {
    VoxcinaTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            QuickStatCard(
                icon = Icons.Outlined.Wallet,
                value = "1500000",
                label = "تومان موجودی",
                onClick = {},
                modifier = Modifier.width(120.dp)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFCFAF8, name = "RTL Layout")
@Composable
private fun QuickStatCardRtlPreview() {
    VoxcinaTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickStatCard(
                    icon = Icons.Outlined.Wallet,
                    value = "250000",
                    label = "تومان موجودی",
                    onClick = {},
                    modifier = Modifier.width(100.dp)
                )
                
                QuickStatCard(
                    icon = Icons.Outlined.CardGiftcard,
                    value = "1500",
                    label = "امتیاز باشگاه",
                    onClick = {},
                    modifier = Modifier.width(100.dp)
                )
                
                QuickStatCard(
                    icon = Icons.Outlined.ConfirmationNumber,
                    value = "3",
                    label = "کوپن فعال",
                    onClick = {},
                    modifier = Modifier.width(100.dp)
                )
            }
        }
    }
}
