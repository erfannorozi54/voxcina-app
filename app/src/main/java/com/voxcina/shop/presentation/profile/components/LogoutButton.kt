package com.voxcina.shop.presentation.profile.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.voxcina.shop.ui.theme.Destructive
import com.voxcina.shop.ui.theme.VoxcinaTheme

/**
 * A logout button with red text and border on transparent background.
 * Used at the bottom of the profile screen for user logout action.
 *
 * @param onClick Callback when the button is clicked (should show confirmation dialog)
 * @param modifier Modifier for the button
 * @param enabled Whether the button is enabled
 */
@Composable
fun LogoutButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            width = 1.dp,
            color = if (enabled) Destructive else Destructive.copy(alpha = 0.5f)
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent,
            contentColor = Destructive,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = Destructive.copy(alpha = 0.5f)
        )
    ) {
        Text(
            text = "خروج از حساب کاربری",
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFCFAF8)
@Composable
private fun LogoutButtonPreview() {
    VoxcinaTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            LogoutButton(
                onClick = {}
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFCFAF8)
@Composable
private fun LogoutButtonDisabledPreview() {
    VoxcinaTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            LogoutButton(
                onClick = {},
                enabled = false
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun LogoutButtonOnWhitePreview() {
    VoxcinaTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            LogoutButton(
                onClick = {}
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFCFAF8, name = "RTL Layout")
@Composable
private fun LogoutButtonRtlPreview() {
    VoxcinaTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Column(modifier = Modifier.padding(16.dp)) {
                LogoutButton(
                    onClick = {}
                )
            }
        }
    }
}
