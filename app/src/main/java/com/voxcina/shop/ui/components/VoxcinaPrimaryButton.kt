package com.voxcina.shop.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.VoxcinaTheme

/**
 * Voxcina branded primary button with loading state support.
 * Matches the onboarding button style with 28dp radius (pill shape).
 *
 * @param text Button text
 * @param onClick Click callback
 * @param modifier Modifier for the button
 * @param enabled Whether the button is enabled
 * @param isLoading Whether to show loading indicator
 */
@Composable
fun VoxcinaPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Button(
            onClick = onClick,
            modifier = modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = enabled && !isLoading,
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Primary,
                contentColor = Color.White,
                disabledContainerColor = Color(0xFFE5E7EB),
                disabledContentColor = Color(0xFF9CA3AF)
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = text,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun VoxcinaPrimaryButtonPreview() {
    VoxcinaTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            VoxcinaPrimaryButton(
                text = "ادامه",
                onClick = {},
                modifier = Modifier.padding(bottom = 16.dp)
            )

            VoxcinaPrimaryButton(
                text = "ادامه",
                onClick = {},
                enabled = false,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            VoxcinaPrimaryButton(
                text = "ادامه",
                onClick = {},
                isLoading = true
            )
        }
    }
}
