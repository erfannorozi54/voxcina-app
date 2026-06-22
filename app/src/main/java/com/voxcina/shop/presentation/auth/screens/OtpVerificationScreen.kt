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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.voxcina.shop.R
import com.voxcina.shop.domain.usecase.OtpFlow
import com.voxcina.shop.ui.components.OtpInputField
import com.voxcina.shop.ui.components.VoxcinaPrimaryButton
import com.voxcina.shop.ui.components.VoxcinaTextButton
import com.voxcina.shop.ui.theme.Destructive
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.PrimaryDark
import com.voxcina.shop.ui.theme.SecondaryLight
import com.voxcina.shop.ui.theme.VoxcinaTheme
import com.voxcina.shop.util.PersianDigitConverter
import com.voxcina.shop.util.SmsRetrieverHelper

/**
 * OTP verification screen with 5-digit input, countdown timer, and SMS auto-fill.
 *
 * Requirements: 4.4, 4.7, 4.8, 6.4, 6.5, 6.6, 6.7, 6.9
 */
@Composable
fun OtpVerificationScreen(
    phone: String,
    otp: String,
    flow: OtpFlow,
    error: String?,
    isLoading: Boolean,
    resendCountdown: Int,
    onOtpChange: (String) -> Unit,
    onOtpAutoFilled: (String) -> Unit,
    onSubmit: () -> Unit,
    onResend: () -> Unit,
    onBack: () -> Unit,
    smsRetrieverHelper: SmsRetrieverHelper?,
    modifier: Modifier = Modifier
) {
    // Start SMS Retriever when screen is displayed
    DisposableEffect(Unit) {
        smsRetrieverHelper?.startSmsRetriever(
            onOtpReceived = { extractedOtp ->
                onOtpAutoFilled(extractedOtp)
            },
            onFailure = {
                // Silent failure - user can still enter OTP manually
            }
        )
        
        onDispose {
            smsRetrieverHelper?.stopSmsRetriever()
        }
    }

    // Auto-submit when OTP is complete
    LaunchedEffect(otp) {
        if (otp.length == 5 && !isLoading) {
            onSubmit()
        }
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
                text = stringResource(R.string.auth_otp_title),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryDark,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle with phone number
            Text(
                text = stringResource(
                    R.string.auth_otp_subtitle,
                    PersianDigitConverter.toPersianDigits(phone)
                ),
                fontSize = 14.sp,
                color = PrimaryDark.copy(alpha = 0.7f),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(40.dp))

            // OTP input field
            OtpInputField(
                otp = otp,
                onOtpChange = onOtpChange,
                error = error,
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Resend button with countdown
            if (resendCountdown > 0) {
                val minutes = resendCountdown / 60
                val seconds = resendCountdown % 60
                val timeString = String.format("%d:%02d", minutes, seconds)
                Text(
                    text = stringResource(
                        R.string.auth_resend_otp_countdown,
                        PersianDigitConverter.toPersianDigits(timeString)
                    ),
                    fontSize = 14.sp,
                    color = Primary,
                    fontWeight = FontWeight.Medium
                )
            } else {
                VoxcinaTextButton(
                    text = stringResource(R.string.auth_resend_otp),
                    onClick = onResend,
                    enabled = !isLoading
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Submit button
            VoxcinaPrimaryButton(
                text = stringResource(R.string.auth_continue),
                onClick = onSubmit,
                enabled = otp.length == 5,
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
