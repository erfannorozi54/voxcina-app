package com.voxcina.shop.presentation.checkout.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.voxcina.shop.domain.model.UserAddress
import com.voxcina.shop.ui.components.SoftShadowCard
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.Primary100
import com.voxcina.shop.ui.theme.VoxcinaTheme

/**
 * Shipping address card component displaying the selected delivery address.
 * Shows recipient name with label badge, address with truncation, and change button.
 * Handles empty address state with prompt to add address.
 *
 * Requirements: 3.1, 3.2, 3.3, 3.5, 3.6
 *
 * @param address Selected shipping address (null if no address)
 * @param onChangeClick Callback when change/add button is clicked
 * @param modifier Modifier for the card
 */
@Composable
fun ShippingAddressCard(
    address: UserAddress?,
    onChangeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        SoftShadowCard(
            modifier = modifier.fillMaxWidth(),
            cornerRadius = 16.dp
        ) {
            if (address != null) {
                // Address exists - show address details
                AddressContent(
                    address = address,
                    onChangeClick = onChangeClick
                )
            } else {
                // No address - show empty state
                EmptyAddressContent(
                    onAddClick = onChangeClick
                )
            }
        }
    }
}

/**
 * Content when an address is available.
 */
@Composable
private fun AddressContent(
    address: UserAddress,
    onChangeClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Location icon in rounded container
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Primary100),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = Primary
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Address details
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Recipient name with label badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Recipient name
                val recipientName = buildString {
                    address.firstName?.let { append(it) }
                    address.lastName?.let {
                        if (isNotEmpty()) append(" ")
                        append(it)
                    }
                }.ifEmpty { "گیرنده" }

                Text(
                    text = recipientName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )

                // Label badge (title like "خانه", "محل کار")
                address.title?.let { title ->
                    Box(
                        modifier = Modifier
                            .background(
                                color = Primary.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelSmall,
                            color = Primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Full address text with truncation
            val fullAddress = buildString {
                address.province?.let { append("$it، ") }
                append("${address.city}، ")
                address.street?.let { append("$it، ") }
                address.address?.let { append(it) }
            }.trimEnd('،', ' ')

            Text(
                text = fullAddress,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Phone number if available
            address.phoneNumber?.let { phone ->
                Text(
                    text = phone,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }

        // Change button
        TextButton(onClick = onChangeClick) {
            Text(
                text = "تغییر",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Primary
            )
        }
    }
}

/**
 * Content when no address is available.
 */
@Composable
private fun EmptyAddressContent(
    onAddClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Location icon in rounded container
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Primary100),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = Primary
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Empty state message
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = "آدرسی ثبت نشده است",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Primary
            )
            Text(
                text = "لطفاً آدرس تحویل را اضافه کنید",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }

        // Add button
        TextButton(onClick = onAddClick) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = Primary
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "افزودن",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Primary
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFCFAF8)
@Composable
private fun ShippingAddressCardPreview() {
    VoxcinaTheme {
        ShippingAddressCard(
            address = UserAddress(
                title = "خانه",
                firstName = "علی",
                lastName = "محمدی",
                phoneNumber = "۰۹۱۲۳۴۵۶۷۸۹",
                province = "تهران",
                provinceCode = 8,
                city = "تهران",
                cityCode = 301,
                street = "خیابان ولیعصر",
                address = "پلاک ۱۲۳، واحد ۴",
                postalCode = "1234567890",
                latitude = 35.6892,
                longitude = 51.3890,
                isDefault = true
            ),
            onChangeClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFCFAF8)
@Composable
private fun ShippingAddressCardLongAddressPreview() {
    VoxcinaTheme {
        ShippingAddressCard(
            address = UserAddress(
                title = "محل کار",
                firstName = "محمد",
                lastName = "احمدی",
                phoneNumber = "۰۹۱۲۳۴۵۶۷۸۹",
                province = "تهران",
                provinceCode = 8,
                city = "تهران",
                cityCode = 301,
                street = "خیابان آزادی، بعد از میدان انقلاب، کوچه شهید رجایی",
                address = "پلاک ۴۵۶، طبقه سوم، واحد ۱۲، ساختمان نور",
                postalCode = "1234567890",
                latitude = 35.6892,
                longitude = 51.3890,
                isDefault = false
            ),
            onChangeClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFCFAF8)
@Composable
private fun ShippingAddressCardEmptyPreview() {
    VoxcinaTheme {
        ShippingAddressCard(
            address = null,
            onChangeClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
