package com.voxcina.shop.presentation.auth.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.voxcina.shop.R
import com.voxcina.shop.ui.components.VoxcinaPrimaryButton
import com.voxcina.shop.ui.components.VoxcinaTextButton
import com.voxcina.shop.ui.components.VoxcinaTextField
import com.voxcina.shop.ui.theme.Destructive
import com.voxcina.shop.ui.theme.PrimaryDark
import com.voxcina.shop.ui.theme.SecondaryLight
import com.voxcina.shop.ui.theme.VoxcinaTheme
import com.voxcina.shop.util.PersianDigitConverter

/**
 * Login screen for existing users with password authentication.
 * Provides options for OTP login and forgot password.
 *
 * Requirements: 3.1, 3.5, 3.6, 4.1, 10.2
 */
@Composable
fun LoginScreen(
    phone: String,
    password: String,
    passwordVisible: Boolean,
    error: String?,
    isLoading: Boolean,
    onPasswordChange: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onLogin: () -> Unit,
    onLoginWithOtp: () -> Unit,
    onForgotPassword: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(SecondaryLight)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(80.dp))

            // Title
            Text(
                text = stringResource(R.string.auth_login_title),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryDark,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle with phone number
            Text(
                text = stringResource(R.string.auth_login_subtitle),
                fontSize = 14.sp,
                color = PrimaryDark.copy(alpha = 0.7f),
                modifier = Modifier.fillMaxWidth()
            )

            // Display phone number in Persian digits
            Text(
                text = PersianDigitConverter.toPersianDigits(phone),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = PrimaryDark,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Password input field
            VoxcinaTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = stringResource(R.string.auth_password_placeholder),
                keyboardType = KeyboardType.Password,
                isPassword = true,
                passwordVisible = passwordVisible,
                onPasswordVisibilityToggle = onTogglePasswordVisibility,
                modifier = Modifier.fillMaxWidth()
            )

            // Error message
            if (error != null) {
                Text(
                    text = error,
                    color = Destructive,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Login button
            VoxcinaPrimaryButton(
                text = stringResource(R.string.auth_login),
                onClick = onLogin,
                enabled = password.isNotEmpty(),
                isLoading = isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Secondary options row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Login with OTP option
                VoxcinaTextButton(
                    text = stringResource(R.string.auth_login_with_otp),
                    onClick = onLoginWithOtp,
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.weight(1f))

                // Forgot password link
                VoxcinaTextButton(
                    text = stringResource(R.string.auth_forgot_password),
                    onClick = onForgotPassword,
                    enabled = !isLoading
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Back button
            VoxcinaTextButton(
                text = stringResource(R.string.auth_back),
                onClick = onBack,
                enabled = !isLoading,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    VoxcinaTheme {
        LoginScreen(
            phone = "09123456789",
            password = "",
            passwordVisible = false,
            error = null,
            isLoading = false,
            onPasswordChange = {},
            onTogglePasswordVisibility = {},
            onLogin = {},
            onLoginWithOtp = {},
            onForgotPassword = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenWithPasswordPreview() {
    VoxcinaTheme {
        LoginScreen(
            phone = "09123456789",
            password = "password123",
            passwordVisible = false,
            error = null,
            isLoading = false,
            onPasswordChange = {},
            onTogglePasswordVisibility = {},
            onLogin = {},
            onLoginWithOtp = {},
            onForgotPassword = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenWithErrorPreview() {
    VoxcinaTheme {
        LoginScreen(
            phone = "09123456789",
            password = "wrongpass",
            passwordVisible = false,
            error = "شماره تلفن یا رمز عبور اشتباه است",
            isLoading = false,
            onPasswordChange = {},
            onTogglePasswordVisibility = {},
            onLogin = {},
            onLoginWithOtp = {},
            onForgotPassword = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenLoadingPreview() {
    VoxcinaTheme {
        LoginScreen(
            phone = "09123456789",
            password = "password123",
            passwordVisible = false,
            error = null,
            isLoading = true,
            onPasswordChange = {},
            onTogglePasswordVisibility = {},
            onLogin = {},
            onLoginWithOtp = {},
            onForgotPassword = {},
            onBack = {}
        )
    }
}
