package com.voxcina.shop.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.VoxcinaTheme

/**
 * Voxcina branded text button for secondary actions and links.
 *
 * @param text Button text
 * @param onClick Click callback
 * @param modifier Modifier for the button
 * @param enabled Whether the button is enabled
 */
@Composable
fun VoxcinaTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        TextButton(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled
        ) {
            Text(
                text = text,
                color = if (enabled) Primary else Color(0xFF9CA3AF),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun VoxcinaTextButtonPreview() {
    VoxcinaTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            VoxcinaTextButton(
                text = "ورود با کد تأیید",
                onClick = {},
                modifier = Modifier.padding(bottom = 8.dp)
            )

            VoxcinaTextButton(
                text = "فراموشی رمز عبور",
                onClick = {},
                modifier = Modifier.padding(bottom = 8.dp)
            )

            VoxcinaTextButton(
                text = "غیرفعال",
                onClick = {},
                enabled = false
            )
        }
    }
}
