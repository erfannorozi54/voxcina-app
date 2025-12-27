package com.voxcina.shop.presentation.auth.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
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
import com.voxcina.shop.presentation.auth.PasswordFlow
import com.voxcina.shop.ui.components.VoxcinaPrimaryButton
import com.voxcina.shop.ui.components.VoxcinaTextButton
import com.voxcina.shop.ui.components.VoxcinaTextField
import com.voxcina.shop.ui.theme.Destructive
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.PrimaryDark
import com.voxcina.shop.ui.theme.SecondaryLight
import com.voxcina.shop.ui.theme.VoxcinaTheme

/**
 * Password creation screen for signup and password reset flows.
 * Includes password strength validation and confirmation.
 *
 * Requirements: 5.4, 5.5, 5.6, 7.3
 */
@Composable
fun PasswordCreationScreen(
    flow: PasswordFlow,
    password: String,
    confirmPassword: String,
    passwordVisible: Boolean,
    passwordError: String?,
    error: String?,
    isLoading: Boolean,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val title = when (flow) {
        PasswordFlow.SIGNUP -> stringResource(R.string.auth_password_creation_title)
        PasswordFlow.RESET -> stringResource(R.string.auth_reset_password_title)
    }

    val subtitle = when (flow) {
        PasswordFlow.SIGNUP -> stringResource(R.string.auth_password_creation_subtitle)
        PasswordFlow.RESET -> stringResource(R.string.auth_reset_password_subtitle)
    }

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
                text = title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryDark,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle
            Text(
                text = subtitle,
                fontSize = 14.sp,
                color = PrimaryDark.copy(alpha = 0.7f),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            // New password input field
            VoxcinaTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = stringResource(R.string.auth_new_password),
                keyboardType = KeyboardType.Password,
                isPassword = true,
                passwordVisible = passwordVisible,
                onPasswordVisibilityToggle = onTogglePasswordVisibility,
                error = passwordError,
                modifier = Modifier.fillMaxWidth()
            )

            // Password hint
            Text(
                text = stringResource(R.string.auth_password_hint),
                fontSize = 12.sp,
                color = Primary.copy(alpha = 0.7f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, start = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm password input field
            VoxcinaTextField(
                value = confirmPassword,
                onValueChange = onConfirmPasswordChange,
                label = stringResource(R.string.auth_confirm_password),
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

            // Submit button - enabled only when both passwords are filled
            val isFormValid = password.isNotEmpty() && confirmPassword.isNotEmpty()
            VoxcinaPrimaryButton(
                text = stringResource(R.string.auth_submit),
                onClick = onSubmit,
                enabled = isFormValid,
                isLoading = isLoading,
                modifier = Modifier.fillMaxWidth()
            )

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
private fun PasswordCreationScreenSignupPreview() {
    VoxcinaTheme {
        PasswordCreationScreen(
            flow = PasswordFlow.SIGNUP,
            password = "",
            confirmPassword = "",
            passwordVisible = false,
            passwordError = null,
            error = null,
            isLoading = false,
            onPasswordChange = {},
            onConfirmPasswordChange = {},
            onTogglePasswordVisibility = {},
            onSubmit = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PasswordCreationScreenResetPreview() {
    VoxcinaTheme {
        PasswordCreationScreen(
            flow = PasswordFlow.RESET,
            password = "",
            confirmPassword = "",
            passwordVisible = false,
            passwordError = null,
            error = null,
            isLoading = false,
            onPasswordChange = {},
            onConfirmPasswordChange = {},
            onTogglePasswordVisibility = {},
            onSubmit = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PasswordCreationScreenWithDataPreview() {
    VoxcinaTheme {
        PasswordCreationScreen(
            flow = PasswordFlow.SIGNUP,
            password = "Password123",
            confirmPassword = "Password123",
            passwordVisible = false,
            passwordError = null,
            error = null,
            isLoading = false,
            onPasswordChange = {},
            onConfirmPasswordChange = {},
            onTogglePasswordVisibility = {},
            onSubmit = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PasswordCreationScreenWithPasswordErrorPreview() {
    VoxcinaTheme {
        PasswordCreationScreen(
            flow = PasswordFlow.SIGNUP,
            password = "weak",
            confirmPassword = "",
            passwordVisible = false,
            passwordError = "رمز عبور باید حداقل ۸ کاراکتر با حروف بزرگ، کوچک و عدد باشد",
            error = null,
            isLoading = false,
            onPasswordChange = {},
            onConfirmPasswordChange = {},
            onTogglePasswordVisibility = {},
            onSubmit = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PasswordCreationScreenWithMismatchErrorPreview() {
    VoxcinaTheme {
        PasswordCreationScreen(
            flow = PasswordFlow.SIGNUP,
            password = "Password123",
            confirmPassword = "Password456",
            passwordVisible = false,
            passwordError = null,
            error = "رمز عبور و تکرار آن یکسان نیستند",
            isLoading = false,
            onPasswordChange = {},
            onConfirmPasswordChange = {},
            onTogglePasswordVisibility = {},
            onSubmit = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PasswordCreationScreenLoadingPreview() {
    VoxcinaTheme {
        PasswordCreationScreen(
            flow = PasswordFlow.SIGNUP,
            password = "Password123",
            confirmPassword = "Password123",
            passwordVisible = false,
            passwordError = null,
            error = null,
            isLoading = true,
            onPasswordChange = {},
            onConfirmPasswordChange = {},
            onTogglePasswordVisibility = {},
            onSubmit = {},
            onBack = {}
        )
    }
}
