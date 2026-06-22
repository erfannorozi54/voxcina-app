package com.voxcina.shop.presentation.cart.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.voxcina.shop.domain.model.Discount
import com.voxcina.shop.domain.model.DiscountType
import com.voxcina.shop.ui.components.SoftShadowCard
import com.voxcina.shop.ui.theme.Destructive
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.Success
import com.voxcina.shop.ui.theme.VoxcinaTheme

/**
 * Sealed class representing the state of discount code validation.
 */
sealed class DiscountState {
    data object Idle : DiscountState()
    data object Loading : DiscountState()
    data class Applied(val discount: Discount) : DiscountState()
    data class Error(val message: String) : DiscountState()
}

/**
 * Discount code input component with text field and submit button.
 * Shows loading, success, and error states.
 *
 * @param code Current discount code text
 * @param onCodeChange Callback when code text changes
 * @param onSubmit Callback when submit button is clicked
 * @param state Current discount validation state
 * @param modifier Modifier for the component
 */
@Composable
fun DiscountCodeInput(
    code: String,
    onCodeChange: (String) -> Unit,
    onSubmit: () -> Unit,
    state: DiscountState,
    modifier: Modifier = Modifier
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(modifier = modifier) {
            SoftShadowCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .padding(start = 16.dp, end = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Text field
                    BasicTextField(
                        value = code,
                        onValueChange = onCodeChange,
                        modifier = Modifier.weight(1f),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            textAlign = TextAlign.Start,
                            color = Primary
                        ),
                        singleLine = true,
                        enabled = state !is DiscountState.Applied,
                        decorationBox = { innerTextField ->
                            Box {
                                if (code.isEmpty() && state !is DiscountState.Applied) {
                                    Text(
                                        text = "کد تخفیف دارید؟",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.Gray
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )
                    
                    // Submit button or success indicator
                    when (state) {
                        is DiscountState.Applied -> {
                            // Show success indicator
                            Box(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .height(40.dp)
                                    .background(
                                        color = Success.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(6.dp)
                                    )
                                    .padding(horizontal = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = Success
                                    )
                                    Text(
                                        text = "اعمال شد",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Success
                                    )
                                }
                            }
                        }
                        else -> {
                            // Submit button
                            Button(
                                onClick = onSubmit,
                                modifier = Modifier
                                    .padding(4.dp)
                                    .height(40.dp),
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Primary.copy(alpha = 0.1f),
                                    contentColor = Primary,
                                    disabledContainerColor = Color.Gray.copy(alpha = 0.1f),
                                    disabledContentColor = Color.Gray
                                ),
                                enabled = code.isNotBlank() && state !is DiscountState.Loading
                            ) {
                                if (state is DiscountState.Loading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp,
                                        color = Primary
                                    )
                                } else {
                                    Text(
                                        text = "ثبت",
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            // Error message
            if (state is DiscountState.Error) {
                Text(
                    text = state.message,
                    style = MaterialTheme.typography.bodySmall,
                    color = Destructive,
                    modifier = Modifier.padding(top = 4.dp, start = 8.dp)
                )
            }
            
            // Success message with discount info
            if (state is DiscountState.Applied) {
                val discountText = when (state.discount.type) {
                    DiscountType.PERCENTAGE -> "${state.discount.value}٪ تخفیف"
                    DiscountType.FIXED -> "${state.discount.value} تومان تخفیف"
                }
                Text(
                    text = "کد ${state.discount.code}: $discountText",
                    style = MaterialTheme.typography.bodySmall,
                    color = Success,
                    modifier = Modifier.padding(top = 4.dp, start = 8.dp)
                )
            }
        }
    }
}
