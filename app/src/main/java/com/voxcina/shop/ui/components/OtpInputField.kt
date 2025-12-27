package com.voxcina.shop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.voxcina.shop.ui.theme.Destructive
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.VoxcinaTheme
import com.voxcina.shop.util.PersianDigitConverter

private const val OTP_LENGTH = 5

/**
 * OTP input field with 5 individual boxes for digit entry.
 * Supports auto-focus to next field and auto-fill from SMS.
 *
 * @param otp Current OTP value (up to 5 digits)
 * @param onOtpChange Callback when OTP changes
 * @param modifier Modifier for the component
 * @param error Error message to display (null if no error)
 * @param enabled Whether the input is enabled
 */
@Composable
fun OtpInputField(
    otp: String,
    onOtpChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    error: String? = null,
    enabled: Boolean = true
) {
    val hasError = error != null
    val focusRequester = remember { FocusRequester() }

    // Request focus when component is first displayed
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BasicTextField(
                value = otp,
                onValueChange = { newValue ->
                    // Only accept digits and limit to OTP_LENGTH
                    val normalized = PersianDigitConverter.convertPersianToLatin(newValue)
                    val filtered = normalized.filter { it.isDigit() }.take(OTP_LENGTH)
                    onOtpChange(filtered)
                },
                enabled = enabled,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                modifier = Modifier.focusRequester(focusRequester),
                decorationBox = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                    ) {
                        repeat(OTP_LENGTH) { index ->
                            OtpDigitBox(
                                digit = otp.getOrNull(index)?.toString() ?: "",
                                isFocused = otp.length == index,
                                hasError = hasError,
                                enabled = enabled
                            )
                        }
                    }
                }
            )

            // Error message
            if (hasError) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Text(
                        text = error!!,
                        color = Destructive,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun OtpDigitBox(
    digit: String,
    isFocused: Boolean,
    hasError: Boolean,
    enabled: Boolean
) {
    val borderColor = when {
        hasError -> Destructive
        isFocused -> Primary
        digit.isNotEmpty() -> Primary.copy(alpha = 0.5f)
        else -> Color(0xFFE5E7EB)
    }

    val backgroundColor = when {
        !enabled -> Color(0xFFF3F4F6)
        else -> Color.White
    }

    Box(
        modifier = Modifier
            .size(48.dp)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = if (isFocused) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (digit.isNotEmpty()) PersianDigitConverter.convertToPersian(digit) else "",
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = if (hasError) Destructive else Primary,
                textAlign = TextAlign.Center
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OtpInputFieldPreview() {
    VoxcinaTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Empty state
            OtpInputField(
                otp = "",
                onOtpChange = {}
            )

            // Partial entry
            OtpInputField(
                otp = "123",
                onOtpChange = {}
            )

            // Complete entry
            OtpInputField(
                otp = "12345",
                onOtpChange = {}
            )

            // Error state
            OtpInputField(
                otp = "12345",
                onOtpChange = {},
                error = "کد تأیید اشتباه است"
            )

            // Disabled state
            OtpInputField(
                otp = "123",
                onOtpChange = {},
                enabled = false
            )
        }
    }
}
