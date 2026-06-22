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
 * Signup screen for new users to enter their name before OTP verification.
 *
 * Requirements: 5.1, 5.9
 */
@Composable
fun SignupScreen(
    phone: String,
    firstName: String,
    lastName: String,
    error: String?,
    isLoading: Boolean,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onContinue: () -> Unit,
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
                text = stringResource(R.string.auth_signup_title),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryDark,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle
            Text(
                text = stringResource(R.string.auth_signup_subtitle),
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

            // First name input field
            VoxcinaTextField(
                value = firstName,
                onValueChange = onFirstNameChange,
                label = stringResource(R.string.auth_first_name),
                keyboardType = KeyboardType.Text,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Last name input field
            VoxcinaTextField(
                value = lastName,
                onValueChange = onLastNameChange,
                label = stringResource(R.string.auth_last_name),
                keyboardType = KeyboardType.Text,
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

            // Continue button - enabled only when both names are filled
            val isFormValid = firstName.isNotBlank() && lastName.isNotBlank()
            VoxcinaPrimaryButton(
                text = stringResource(R.string.auth_continue),
                onClick = onContinue,
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
