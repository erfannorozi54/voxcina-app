package com.voxcina.shop.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingCart
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
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.VoxcinaTheme

/**
 * A reusable empty state component for displaying when content is empty.
 * Configurable icon, title, subtitle, and optional action button.
 * 
 * Reusable for empty cart, wishlist, orders, search results, etc.
 *
 * @param icon The icon to display
 * @param title The main title text
 * @param modifier Modifier for the component
 * @param subtitle Optional subtitle/description text
 * @param actionButtonText Optional action button text (shows VoxcinaPrimaryButton if provided)
 * @param onActionClick Callback when action button is clicked
 * @param iconTint Tint color for the icon (default: Gray with 0.5 alpha)
 */
@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    actionButtonText: String? = null,
    onActionClick: () -> Unit = {},
    iconTint: Color = Color.Gray.copy(alpha = 0.5f)
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(96.dp),
                tint = iconTint
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Primary,
                textAlign = TextAlign.Center
            )
            
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
            
            if (actionButtonText != null) {
                Spacer(modifier = Modifier.height(32.dp))
                
                VoxcinaPrimaryButton(
                    text = actionButtonText,
                    onClick = onActionClick,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyStateCartPreview() {
    VoxcinaTheme {
        EmptyState(
            icon = Icons.Outlined.ShoppingCart,
            title = "سبد خرید شما خالی است",
            subtitle = "محصولات مورد علاقه خود را به سبد خرید اضافه کنید",
            actionButtonText = "شروع خرید",
            onActionClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyStateWishlistPreview() {
    VoxcinaTheme {
        EmptyState(
            icon = Icons.Outlined.Favorite,
            title = "لیست علاقه‌مندی‌ها خالی است",
            subtitle = "محصولات مورد علاقه خود را ذخیره کنید",
            actionButtonText = "مشاهده محصولات",
            onActionClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyStateSearchPreview() {
    VoxcinaTheme {
        EmptyState(
            icon = Icons.Outlined.Search,
            title = "نتیجه‌ای یافت نشد",
            subtitle = "عبارت دیگری را جستجو کنید"
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyStateMinimalPreview() {
    VoxcinaTheme {
        EmptyState(
            icon = Icons.Outlined.ShoppingCart,
            title = "سبد خرید خالی است"
        )
    }
}
