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
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.voxcina.shop.R
import com.voxcina.shop.ui.components.VoxcinaPhoneTextField
import com.voxcina.shop.ui.components.VoxcinaPrimaryButton
import com.voxcina.shop.ui.theme.PrimaryDark
import com.voxcina.shop.ui.theme.SecondaryLight
import com.voxcina.shop.ui.theme.VoxcinaTheme

/**
 * Phone entry screen for starting the authentication flow.
 * Users enter their phone number to check if they're a new or existing user.
 *
 * Requirements: 1.1, 1.4, 1.5, 9.1, 9.5
 */
@Composable
fun PhoneEntryScreen(
    phone: String,
    phoneError: String?,
    isLoading: Boolean,
    onPhoneChange: (String) -> Unit,
    onContinue: () -> Unit,
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
                text = stringResource(R.string.auth_phone_entry_title),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryDark,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle
            Text(
                text = stringResource(R.string.auth_phone_entry_subtitle),
                fontSize = 14.sp,
                color = PrimaryDark.copy(alpha = 0.7f),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Phone input field
            VoxcinaPhoneTextField(
                value = phone,
                onValueChange = onPhoneChange,
                label = stringResource(R.string.auth_phone_placeholder),
                error = phoneError,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Continue button - enabled only when phone is valid (no error and not empty)
            val isPhoneValid = phone.isNotEmpty() && phoneError == null
            VoxcinaPrimaryButton(
                text = stringResource(R.string.auth_continue),
                onClick = onContinue,
                enabled = isPhoneValid,
                isLoading = isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
