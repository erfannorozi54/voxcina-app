package com.voxcina.shop.presentation.checkout.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.voxcina.shop.domain.model.PaymentMethod
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.Primary100
import com.voxcina.shop.ui.theme.VoxcinaTheme

/**
 * Payment method selector component displaying horizontal scrollable tabs.
 * Supports three payment methods with icons and highlights selected method.
 *
 * Requirements: 5.1, 5.2, 5.3, 5.4
 *
 * @param selectedMethod Currently selected payment method
 * @param onMethodSelected Callback when a method is selected
 * @param modifier Modifier for the selector
 */
@Composable
fun PaymentMethodSelector(
    selectedMethod: PaymentMethod,
    onMethodSelected: (PaymentMethod) -> Unit,
    modifier: Modifier = Modifier
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PaymentMethod.entries.forEach { method ->
                PaymentMethodTab(
                    method = method,
                    isSelected = method == selectedMethod,
                    onClick = { onMethodSelected(method) }
                )
            }
        }
    }
}

/**
 * Individual payment method tab with icon and label.
 */
@Composable
private fun PaymentMethodTab(
    method: PaymentMethod,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) Primary else Color.White
    val contentColor = if (isSelected) Color.White else Primary
    val borderColor = if (isSelected) Primary else Color(0xFFE5E7EB)

    Box(
        modifier = Modifier
            .widthIn(min = 110.dp)
            .height(90.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icon in circle
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) Color.White.copy(alpha = 0.2f)
                        else Primary100
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = method.icon,
                    contentDescription = method.displayName,
                    modifier = Modifier.size(22.dp),
                    tint = contentColor
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Label
            Text(
                text = method.displayName,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = contentColor,
                textAlign = TextAlign.Center,
                maxLines = 1
            )

            // Selection indicator
            if (isSelected) {
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFCFAF8)
@Composable
private fun PaymentMethodSelectorBankCardPreview() {
    VoxcinaTheme {
        PaymentMethodSelector(
            selectedMethod = PaymentMethod.BANK_CARD,
            onMethodSelected = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFCFAF8)
@Composable
private fun PaymentMethodSelectorWalletPreview() {
    VoxcinaTheme {
        PaymentMethodSelector(
            selectedMethod = PaymentMethod.WALLET,
            onMethodSelected = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFCFAF8)
@Composable
private fun PaymentMethodSelectorCashPreview() {
    VoxcinaTheme {
        PaymentMethodSelector(
            selectedMethod = PaymentMethod.CASH_ON_DELIVERY,
            onMethodSelected = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
