package com.voxcina.shop.presentation.checkout.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.voxcina.shop.domain.model.CardDetails
import com.voxcina.shop.presentation.checkout.CheckoutValidationFields
import com.voxcina.shop.ui.components.SoftShadowCard
import com.voxcina.shop.ui.theme.Destructive
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.SecondaryLight
import com.voxcina.shop.ui.theme.VoxcinaTheme

/**
 * Card details form component for bank card information entry.
 * Includes card number, expiry date, CVV2 fields with validation.
 *
 * Requirements: 6.1, 6.2, 6.3, 6.4, 6.5
 *
 * @param cardDetails Current card details
 * @param onCardDetailsChange Callback when card details change
 * @param validationErrors Map of field validation errors
 * @param modifier Modifier for the form
 */
@Composable
fun CardDetailsForm(
    cardDetails: CardDetails,
    onCardDetailsChange: (CardDetails) -> Unit,
    validationErrors: Map<String, String>,
    modifier: Modifier = Modifier
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        SoftShadowCard(
            modifier = modifier.fillMaxWidth(),
            cornerRadius = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Card number field (full width, LTR for numeric entry)
                CardNumberField(
                    value = cardDetails.formattedCardNumber,
                    onValueChange = { newValue ->
                        // Remove spaces and non-digits, limit to 16 digits
                        val digitsOnly = newValue.filter { it.isDigit() }.take(16)
                        onCardDetailsChange(cardDetails.copy(cardNumber = digitsOnly))
                    },
                    error = validationErrors[CheckoutValidationFields.CARD_NUMBER]
                )

                // Expiry and CVV fields (side by side)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Expiry date field
                    ExpiryDateField(
                        value = cardDetails.expiryDate,
                        onValueChange = { newValue ->
                            val formatted = CardDetails.formatExpiryDate(newValue)
                            onCardDetailsChange(cardDetails.copy(expiryDate = formatted))
                        },
                        error = validationErrors[CheckoutValidationFields.CARD_EXPIRY],
                        modifier = Modifier.weight(1f)
                    )

                    // CVV2 field
                    CvvField(
                        value = cardDetails.cvv,
                        onValueChange = { newValue ->
                            val digitsOnly = newValue.filter { it.isDigit() }.take(4)
                            onCardDetailsChange(cardDetails.copy(cvv = digitsOnly))
                        },
                        error = validationErrors[CheckoutValidationFields.CARD_CVV],
                        modifier = Modifier.weight(1f)
                    )
                }

                // Save card checkbox
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = cardDetails.saveCard,
                        onCheckedChange = { checked ->
                            onCardDetailsChange(cardDetails.copy(saveCard = checked))
                        },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Primary,
                            uncheckedColor = Color(0xFFE5E7EB)
                        )
                    )
                    Text(
                        text = "ذخیره کارت برای خریدهای بعدی",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

/**
 * Card number input field with LTR direction and space formatting.
 */
@Composable
private fun CardNumberField(
    value: String,
    onValueChange: (String) -> Unit,
    error: String?,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text("شماره کارت") },
            placeholder = { Text("۱۲۳۴ ۵۶۷۸ ۹۰۱۲ ۳۴۵۶") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = error != null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                textDirection = TextDirection.Ltr,
                letterSpacing = 2.sp
            ),
            shape = RoundedCornerShape(8.dp),
            colors = cardFieldColors(error != null)
        )
        if (error != null) {
            Text(
                text = error,
                color = Destructive,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )
        }
    }
}

/**
 * Expiry date input field (MM/YY format).
 */
@Composable
private fun ExpiryDateField(
    value: String,
    onValueChange: (String) -> Unit,
    error: String?,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text("تاریخ انقضا") },
            placeholder = { Text("MM/YY") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = error != null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                textDirection = TextDirection.Ltr
            ),
            shape = RoundedCornerShape(8.dp),
            colors = cardFieldColors(error != null)
        )
        if (error != null) {
            Text(
                text = error,
                color = Destructive,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )
        }
    }
}

/**
 * CVV2 input field with password masking.
 */
@Composable
private fun CvvField(
    value: String,
    onValueChange: (String) -> Unit,
    error: String?,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text("CVV2") },
            placeholder = { Text("•••") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = error != null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            visualTransformation = PasswordVisualTransformation(),
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                textDirection = TextDirection.Ltr
            ),
            shape = RoundedCornerShape(8.dp),
            colors = cardFieldColors(error != null)
        )
        if (error != null) {
            Text(
                text = error,
                color = Destructive,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )
        }
    }
}

/**
 * Common colors for card input fields.
 */
@Composable
private fun cardFieldColors(isError: Boolean) = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = if (isError) Destructive else Primary,
    unfocusedBorderColor = if (isError) Destructive else Color(0xFFE5E7EB),
    errorBorderColor = Destructive,
    focusedLabelColor = if (isError) Destructive else Primary,
    unfocusedLabelColor = Color(0xFF9CA3AF),
    errorLabelColor = Destructive,
    cursorColor = Primary,
    focusedContainerColor = SecondaryLight,
    unfocusedContainerColor = SecondaryLight,
    errorContainerColor = SecondaryLight
)
