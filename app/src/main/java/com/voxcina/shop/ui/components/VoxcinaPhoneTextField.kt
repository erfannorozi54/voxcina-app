package com.voxcina.shop.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.voxcina.shop.ui.theme.Destructive
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.Secondary
import com.voxcina.shop.ui.theme.VazirMatnFamily
import com.voxcina.shop.ui.theme.VoxcinaTheme
import com.voxcina.shop.util.PersianDigitConverter

/**
 * Specialized phone number text field for Voxcina.
 * 
 * Features:
 * - Displays numbers in Persian digits (۰-۹)
 * - Text direction is LTR for proper phone number display
 * - Stores and returns Latin digits (0-9) for API calls
 * - RTL container for label alignment
 *
 * @param value Current phone value in Latin digits (0-9)
 * @param onValueChange Callback with Latin digits when text changes
 * @param label Label/placeholder text
 * @param modifier Modifier for the text field
 * @param error Error message to display (null if no error)
 * @param enabled Whether the text field is enabled
 */
@Composable
fun VoxcinaPhoneTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    error: String? = null,
    enabled: Boolean = true
) {
    val hasError = error != null
    
    // Convert Latin digits to Persian for display
    val displayValue = PersianDigitConverter.toPersianDigits(value)
    
    // Track text field value with cursor position
    var textFieldValue by remember(displayValue) {
        mutableStateOf(TextFieldValue(displayValue, TextRange(displayValue.length)))
    }

    // RTL container for label alignment, but LTR text direction for numbers
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(modifier = modifier) {
            OutlinedTextField(
                value = textFieldValue,
                onValueChange = { newValue ->
                    // Convert Persian digits to Latin for storage
                    val latinValue = PersianDigitConverter.convertPersianToLatin(newValue.text)
                    // Filter to only allow digits
                    val filteredValue = latinValue.filter { it.isDigit() }
                    
                    // Convert back to Persian for display
                    val persianDisplay = PersianDigitConverter.toPersianDigits(filteredValue)
                    
                    // Update text field value with proper cursor position
                    textFieldValue = TextFieldValue(
                        text = persianDisplay,
                        selection = TextRange(persianDisplay.length)
                    )
                    
                    // Callback with Latin digits
                    onValueChange(filteredValue)
                },
                label = { Text(text = label) },
                modifier = Modifier.fillMaxWidth(),
                enabled = enabled,
                singleLine = true,
                isError = hasError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                textStyle = TextStyle(
                    fontFamily = VazirMatnFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    textDirection = TextDirection.Ltr
                ),
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
