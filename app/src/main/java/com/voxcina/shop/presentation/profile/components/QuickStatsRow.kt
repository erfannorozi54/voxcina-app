package com.voxcina.shop.presentation.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.Wallet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.voxcina.shop.ui.theme.VoxcinaTheme
import com.voxcina.shop.util.PersianDigitConverter

/**
 * A horizontal row displaying three quick stat cards for the profile screen.
 * Shows wallet balance, loyalty points, and active coupons.
 *
 * @param walletBalance User's wallet balance in Tomans
 * @param loyaltyPoints User's loyalty points
 * @param activeCoupons Number of active coupons
 * @param onWalletClick Callback when wallet card is clicked
 * @param onLoyaltyClick Callback when loyalty card is clicked
 * @param onCouponsClick Callback when coupons card is clicked
 * @param modifier Modifier for the row
 */
@Composable
fun QuickStatsRow(
    walletBalance: Long,
    loyaltyPoints: Int,
    activeCoupons: Int,
    onWalletClick: () -> Unit,
    onLoyaltyClick: () -> Unit,
    onCouponsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Wallet balance card
        QuickStatCard(
            icon = Icons.Outlined.Wallet,
            value = PersianDigitConverter.formatPrice(walletBalance),
            label = "تومان موجودی",
            onClick = onWalletClick,
            modifier = Modifier.weight(1f)
        )
        
        // Loyalty points card
        QuickStatCard(
            icon = Icons.Outlined.CardGiftcard,
            value = PersianDigitConverter.formatPrice(loyaltyPoints.toLong()),
            label = "امتیاز باشگاه",
            onClick = onLoyaltyClick,
            modifier = Modifier.weight(1f)
        )
        
        // Active coupons card
        QuickStatCard(
            icon = Icons.Outlined.ConfirmationNumber,
            value = activeCoupons.toString(),
            label = "کوپن فعال",
            onClick = onCouponsClick,
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFCFAF8)
@Composable
private fun QuickStatsRowPreview() {
    VoxcinaTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            QuickStatsRow(
                walletBalance = 250000,
                loyaltyPoints = 1500,
                activeCoupons = 3,
                onWalletClick = {},
                onLoyaltyClick = {},
                onCouponsClick = {}
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFCFAF8)
@Composable
private fun QuickStatsRowLargeValuesPreview() {
    VoxcinaTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            QuickStatsRow(
                walletBalance = 1500000,
                loyaltyPoints = 12500,
                activeCoupons = 15,
                onWalletClick = {},
                onLoyaltyClick = {},
                onCouponsClick = {}
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFCFAF8)
@Composable
private fun QuickStatsRowZeroValuesPreview() {
    VoxcinaTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            QuickStatsRow(
                walletBalance = 0,
                loyaltyPoints = 0,
                activeCoupons = 0,
                onWalletClick = {},
                onLoyaltyClick = {},
                onCouponsClick = {}
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFCFAF8, name = "RTL Layout")
@Composable
private fun QuickStatsRowRtlPreview() {
    VoxcinaTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Column(modifier = Modifier.padding(16.dp)) {
                QuickStatsRow(
                    walletBalance = 250000,
                    loyaltyPoints = 1500,
                    activeCoupons = 3,
                    onWalletClick = {},
                    onLoyaltyClick = {},
                    onCouponsClick = {}
                )
            }
        }
    }
}
