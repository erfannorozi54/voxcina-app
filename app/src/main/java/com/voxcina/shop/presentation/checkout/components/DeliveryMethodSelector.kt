package com.voxcina.shop.presentation.checkout.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.voxcina.shop.domain.model.ShippingMethod
import com.voxcina.shop.presentation.checkout.roundToThousand
import com.voxcina.shop.ui.components.PriceText
import com.voxcina.shop.ui.components.SoftShadowCard
import com.voxcina.shop.ui.components.VoxcinaLoadingCompact
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.VoxcinaTheme

/**
 * Delivery method selector component displaying shipping options as selectable radio cards.
 * Highlights selected method with primary border and shows loading state.
 *
 * Requirements: 4.1, 4.2, 4.3, 4.5
 *
 * @param methods List of available shipping methods
 * @param selectedMethod Currently selected shipping method
 * @param isLoading Whether shipping quotes are being loaded
 * @param onMethodSelected Callback when a method is selected
 * @param modifier Modifier for the selector
 */
@Composable
fun DeliveryMethodSelector(
    methods: List<ShippingMethod>,
    selectedMethod: ShippingMethod?,
    isLoading: Boolean,
    onMethodSelected: (ShippingMethod) -> Unit,
    modifier: Modifier = Modifier
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when {
                isLoading -> {
                    // Loading state
                    LoadingState()
                }
                methods.isEmpty() -> {
                    // Empty state
                    EmptyState()
                }
                else -> {
                    // Show shipping methods
                    methods.forEach { method ->
                        DeliveryMethodCard(
                            method = method,
                            isSelected = method.id == selectedMethod?.id,
                            onClick = { onMethodSelected(method) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Individual delivery method card with radio selection.
 */
@Composable
private fun DeliveryMethodCard(
    method: ShippingMethod,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    SelectableMethodCard(
        title = method.name,
        description = method.estimatedDays,
        icon = Icons.Default.LocalShipping,
        isSelected = isSelected,
        onClick = onClick,
        iconContent = if (!method.courierLogo.isNullOrEmpty()) {
            {
                AsyncImage(
                    model = method.courierLogo,
                    contentDescription = method.description,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    contentScale = ContentScale.Fit
                )
            }
        } else null,
        trailingContent = {
            PriceText(
                price = method.price.roundToThousand(),
                priceStyle = MaterialTheme.typography.titleSmall,
                priceColor = Primary,
                suffixStyle = MaterialTheme.typography.labelSmall
            )
        }
    )
}

/**
 * Loading state for delivery methods.
 */
@Composable
private fun LoadingState() {
    SoftShadowCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 12.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            VoxcinaLoadingCompact(size = 24.dp)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "در حال دریافت روش‌های ارسال...",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}

/**
 * Empty state when no shipping methods available.
 */
@Composable
private fun EmptyState() {
    SoftShadowCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 12.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.LocalShipping,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = Color.Gray
            )
            Text(
                text = "روش ارسالی یافت نشد",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFCFAF8)
@Composable
private fun DeliveryMethodSelectorPreview() {
    val methods = listOf(
        ShippingMethod(
            id = "postex_standard",
            name = "پست پیشتاز",
            price = 150000,
            estimatedDays = "۳ تا ۵ روز کاری",
            description = "ارسال استاندارد"
        ),
        ShippingMethod(
            id = "postex_express",
            name = "پست ویژه",
            price = 250000,
            estimatedDays = "۱ تا ۲ روز کاری",
            description = "ارسال سریع"
        )
    )

    VoxcinaTheme {
        DeliveryMethodSelector(
            methods = methods,
            selectedMethod = methods[0],
            isLoading = false,
            onMethodSelected = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFCFAF8)
@Composable
private fun DeliveryMethodSelectorNoSelectionPreview() {
    val methods = listOf(
        ShippingMethod(
            id = "postex_standard",
            name = "پست پیشتاز",
            price = 150000,
            estimatedDays = "۳ تا ۵ روز کاری",
            description = "ارسال استاندارد"
        ),
        ShippingMethod(
            id = "postex_express",
            name = "پست ویژه",
            price = 250000,
            estimatedDays = "۱ تا ۲ روز کاری",
            description = "ارسال سریع"
        )
    )

    VoxcinaTheme {
        DeliveryMethodSelector(
            methods = methods,
            selectedMethod = null,
            isLoading = false,
            onMethodSelected = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFCFAF8)
@Composable
private fun DeliveryMethodSelectorLoadingPreview() {
    VoxcinaTheme {
        DeliveryMethodSelector(
            methods = emptyList(),
            selectedMethod = null,
            isLoading = true,
            onMethodSelected = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFCFAF8)
@Composable
private fun DeliveryMethodSelectorEmptyPreview() {
    VoxcinaTheme {
        DeliveryMethodSelector(
            methods = emptyList(),
            selectedMethod = null,
            isLoading = false,
            onMethodSelected = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
