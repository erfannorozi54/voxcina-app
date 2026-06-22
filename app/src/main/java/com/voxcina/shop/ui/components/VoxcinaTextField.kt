package com.voxcina.shop.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.voxcina.shop.R
import com.voxcina.shop.ui.theme.Destructive
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.Secondary
import com.voxcina.shop.ui.theme.VoxcinaTheme

/**
 * Voxcina branded text field with RTL support, error state, and password visibility toggle.
 *
 * @param value Current text value
 * @param onValueChange Callback when text changes
 * @param label Label/placeholder text
 * @param modifier Modifier for the text field
 * @param error Error message to display (null if no error)
 * @param keyboardType Keyboard type for input
 * @param isPassword Whether this is a password field
 * @param passwordVisible Whether password is visible (only used when isPassword is true)
 * @param onPasswordVisibilityToggle Callback to toggle password visibility
 * @param enabled Whether the text field is enabled
 * @param singleLine Whether the text field is single line
 */
@Composable
fun VoxcinaTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    error: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onPasswordVisibilityToggle: (() -> Unit)? = null,
    enabled: Boolean = true,
    singleLine: Boolean = true
) {
    val hasError = error != null

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(modifier = modifier) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                label = { Text(text = label) },
                modifier = Modifier.fillMaxWidth(),
                enabled = enabled,
                singleLine = singleLine,
                isError = hasError,
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                visualTransformation = if (isPassword && !passwordVisible) {
                    PasswordVisualTransformation()
                } else {
                    VisualTransformation.None
                },
                trailingIcon = if (isPassword && onPasswordVisibilityToggle != null) {
                    {
                        IconButton(onClick = onPasswordVisibilityToggle) {
                            Icon(
                                painter = painterResource(
                                    id = if (passwordVisible) {
                                        android.R.drawable.ic_menu_view
                                    } else {
                                        android.R.drawable.ic_secure
                                    }
                                ),
                                contentDescription = stringResource(
                                    if (passwordVisible) R.string.auth_password_hide
                                    else R.string.auth_password_show
                                ),
                                tint = if (hasError) Destructive else Primary
                            )
                        }
                    }
                } else null,
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = Color(0xFFE5E7EB),
                    errorBorderColor = Destructive,
                    focusedLabelColor = Primary,
                    unfocusedLabelColor = Color(0xFF9CA3AF),
                    errorLabelColor = Destructive,
                    cursorColor = Primary,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    errorContainerColor = Color.White,
                    disabledContainerColor = Secondary
                )
            )

            // Error message
            if (hasError) {
                Text(
                    text = error!!,
                    color = Destructive,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                )
            }
        }
    }
}
