package com.voxcina.shop.presentation.profile.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.voxcina.shop.ui.components.SoftShadowCard
import com.voxcina.shop.ui.theme.VoxcinaTheme

/**
 * A container component for grouping profile menu items.
 * Provides white background with soft shadow and rounded corners (16dp).
 *
 * @param modifier Modifier for the group container
 * @param content Content to display inside the group (typically ProfileMenuItem components)
 */
@Composable
fun ProfileMenuGroup(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    SoftShadowCard(
        modifier = modifier.fillMaxWidth(),
        cornerRadius = 16.dp
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            content = content
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFCFAF8)
@Composable
private fun ProfileMenuGroupPreview() {
    VoxcinaTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            ProfileMenuGroup {
                ProfileMenuItem(
                    icon = Icons.Default.LocationOn,
                    label = "آدرس‌های من",
                    onClick = {},
                    showDivider = true
                )
                
                ProfileMenuItem(
                    icon = Icons.Default.Favorite,
                    label = "علاقه‌مندی‌ها",
                    onClick = {},
                    showDivider = true
                )
                
                ProfileMenuItem(
                    icon = Icons.Default.History,
                    label = "بازدیدهای اخیر",
                    onClick = {},
                    showDivider = false
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFCFAF8)
@Composable
private fun ProfileMenuGroupSettingsPreview() {
    VoxcinaTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            ProfileMenuGroup {
                ProfileMenuItem(
                    icon = Icons.Default.Settings,
                    label = "تنظیمات",
                    onClick = {},
                    showDivider = true
                )
                
                ProfileMenuItem(
                    icon = Icons.Default.HeadsetMic,
                    label = "پشتیبانی و سوالات متداول",
                    onClick = {},
                    showDivider = false
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFCFAF8, name = "RTL Layout")
@Composable
private fun ProfileMenuGroupRtlPreview() {
    VoxcinaTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Column(modifier = Modifier.padding(16.dp)) {
                ProfileMenuGroup {
                    ProfileMenuItem(
                        icon = Icons.Default.LocationOn,
                        label = "آدرس‌های من",
                        onClick = {},
                        showDivider = true
                    )
                    
                    ProfileMenuItem(
                        icon = Icons.Default.Favorite,
                        label = "علاقه‌مندی‌ها",
                        onClick = {},
                        showDivider = true
                    )
                    
                    ProfileMenuItem(
                        icon = Icons.Default.History,
                        label = "بازدیدهای اخیر",
                        onClick = {},
                        showDivider = false
                    )
                }
            }
        }
    }
}
