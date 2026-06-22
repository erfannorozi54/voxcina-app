package com.voxcina.shop.presentation.checkout.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.voxcina.shop.domain.model.PaymentMethod

/**
 * Payment method selector component displaying payment options as selectable cards.
 */
@Composable
fun PaymentMethodSelector(
    selectedMethod: PaymentMethod,
    onMethodSelected: (PaymentMethod) -> Unit,
    onComingSoon: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PaymentMethod.entries.forEach { method ->
                val isEnabled = method == PaymentMethod.ZIBAL || method == PaymentMethod.DIGIPAY
                SelectableMethodCard(
                    title = method.displayName,
                    description = getPaymentDescription(method),
                    icon = getPaymentIcon(method),
                    isSelected = method == selectedMethod && isEnabled,
                    isEnabled = isEnabled,
                    onClick = {
                        if (isEnabled) {
                            onMethodSelected(method)
                        } else {
                            onComingSoon()
                        }
                    }
                )
            }
        }
    }
}

private fun getPaymentIcon(method: PaymentMethod): ImageVector {
    return when (method) {
        PaymentMethod.ZIBAL -> Icons.Default.CreditCard
        PaymentMethod.DIGIPAY -> Icons.Default.CreditCard
        PaymentMethod.WALLET -> Icons.Default.Wallet
        PaymentMethod.CASH_ON_DELIVERY -> Icons.Default.LocalShipping
    }
}

private fun getPaymentDescription(method: PaymentMethod): String {
    return when (method) {
        PaymentMethod.ZIBAL -> "پرداخت آنلاین با درگاه زیبال"
        PaymentMethod.DIGIPAY -> "پرداخت آنلاین با درگاه دیجی‌پی"
        PaymentMethod.WALLET -> "پرداخت از کیف پول (به زودی)"
        PaymentMethod.CASH_ON_DELIVERY -> "پرداخت در محل تحویل (به زودی)"
    }
}
