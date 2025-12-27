package com.voxcina.shop.presentation.auth

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.voxcina.shop.presentation.auth.screens.LoginScreen
import com.voxcina.shop.presentation.auth.screens.OtpVerificationScreen
import com.voxcina.shop.presentation.auth.screens.PasswordCreationScreen
import com.voxcina.shop.presentation.auth.screens.PhoneEntryScreen
import com.voxcina.shop.presentation.auth.screens.SignupScreen
import com.voxcina.shop.util.SmsRetrieverHelper

/**
 * Main authentication screen that orchestrates navigation between sub-screens
 * based on the current AuthUiState.
 *
 * Requirements: 2.2, 2.3, 4.3, 5.3, 7.2, 7.5
 */
@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    smsRetrieverHelper: SmsRetrieverHelper?,
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Handle back navigation
    BackHandler(enabled = uiState !is AuthUiState.PhoneEntry) {
        viewModel.onEvent(AuthEvent.NavigateBack)
    }

    // Navigate to main screen on success
    if (uiState is AuthUiState.Success) {
        onAuthSuccess()
        return
    }

    AuthScreenContent(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        smsRetrieverHelper = smsRetrieverHelper,
        modifier = modifier
    )
}

@Composable
private fun AuthScreenContent(
    uiState: AuthUiState,
    onEvent: (AuthEvent) -> Unit,
    smsRetrieverHelper: SmsRetrieverHelper?,
    modifier: Modifier = Modifier
) {
    when (uiState) {
        is AuthUiState.PhoneEntry -> {
            PhoneEntryScreen(
                phone = uiState.phone,
                phoneError = uiState.phoneError,
                isLoading = uiState.isLoading,
                onPhoneChange = { onEvent(AuthEvent.PhoneChanged(it)) },
                onContinue = { onEvent(AuthEvent.SubmitPhone) },
                modifier = modifier
            )
        }

        is AuthUiState.Login -> {
            LoginScreen(
                phone = uiState.phone,
                password = uiState.password,
                passwordVisible = uiState.passwordVisible,
                error = uiState.error,
                isLoading = uiState.isLoading,
                onPasswordChange = { onEvent(AuthEvent.PasswordChanged(it)) },
                onTogglePasswordVisibility = { onEvent(AuthEvent.TogglePasswordVisibility) },
                onLogin = { onEvent(AuthEvent.SubmitLogin) },
                onLoginWithOtp = { onEvent(AuthEvent.RequestOtpLogin) },
                onForgotPassword = { onEvent(AuthEvent.ForgotPassword) },
                onBack = { onEvent(AuthEvent.NavigateBack) },
                modifier = modifier
            )
        }

        is AuthUiState.Signup -> {
            SignupScreen(
                phone = uiState.phone,
                firstName = uiState.firstName,
                lastName = uiState.lastName,
                error = uiState.error,
                isLoading = uiState.isLoading,
                onFirstNameChange = { onEvent(AuthEvent.FirstNameChanged(it)) },
                onLastNameChange = { onEvent(AuthEvent.LastNameChanged(it)) },
                onContinue = { onEvent(AuthEvent.SubmitSignup) },
                onBack = { onEvent(AuthEvent.NavigateBack) },
                modifier = modifier
            )
        }

        is AuthUiState.OtpVerification -> {
            OtpVerificationScreen(
                phone = uiState.phone,
                otp = uiState.otp,
                flow = uiState.flow,
                error = uiState.error,
                isLoading = uiState.isLoading,
                resendCountdown = uiState.resendCountdown,
                onOtpChange = { onEvent(AuthEvent.OtpChanged(it)) },
                onOtpAutoFilled = { onEvent(AuthEvent.OtpAutoFilled(it)) },
                onSubmit = { onEvent(AuthEvent.SubmitOtp) },
                onResend = { onEvent(AuthEvent.ResendOtp) },
                onBack = { onEvent(AuthEvent.NavigateBack) },
                smsRetrieverHelper = smsRetrieverHelper,
                modifier = modifier
            )
        }

        is AuthUiState.PasswordCreation -> {
            PasswordCreationScreen(
                flow = uiState.flow,
                password = uiState.password,
                confirmPassword = uiState.confirmPassword,
                passwordVisible = uiState.passwordVisible,
                passwordError = uiState.passwordError,
                error = uiState.error,
                isLoading = uiState.isLoading,
                onPasswordChange = { onEvent(AuthEvent.NewPasswordChanged(it)) },
                onConfirmPasswordChange = { onEvent(AuthEvent.ConfirmPasswordChanged(it)) },
                onTogglePasswordVisibility = { onEvent(AuthEvent.ToggleNewPasswordVisibility) },
                onSubmit = { onEvent(AuthEvent.SubmitNewPassword) },
                onBack = { onEvent(AuthEvent.NavigateBack) },
                modifier = modifier
            )
        }

        is AuthUiState.Success -> {
            // Handled above - navigation to main screen
        }
    }
}
