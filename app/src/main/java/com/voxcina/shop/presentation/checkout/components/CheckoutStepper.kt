package com.voxcina.shop.presentation.checkout.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.Primary100
import com.voxcina.shop.ui.theme.VoxcinaTheme

/**
 * Step data for the checkout stepper.
 */
private data class StepData(
    val index: Int,
    val title: String
)

/**
 * Checkout progress stepper showing 3 steps: Cart → Information → Payment.
 * 
 * Requirements: 2.1, 2.2, 2.3, 2.4, 2.5
 *
 * @param currentStep Current step index (0 = Cart, 1 = Information, 2 = Payment)
 * @param modifier Modifier for the stepper container
 */
@Composable
fun CheckoutStepper(
    currentStep: Int,
    modifier: Modifier = Modifier
) {
    val steps = listOf(
        StepData(0, "سبد خرید"),
        StepData(1, "اطلاعات"),
        StepData(2, "پرداخت")
    )

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            steps.forEachIndexed { index, step ->
                // Step indicator with label
                StepIndicator(
                    stepNumber = index + 1,
                    title = step.title,
                    isCompleted = index < currentStep,
                    isCurrent = index == currentStep,
                    modifier = Modifier.weight(1f)
                )

                // Connecting line between steps (not after last step)
                if (index < steps.size - 1) {
                    StepConnector(
                        isCompleted = index < currentStep,
                        modifier = Modifier.weight(0.5f)
                    )
                }
            }
        }
    }
}

/**
 * Individual step indicator with circle and label.
 *
 * @param stepNumber Step number (1, 2, 3)
 * @param title Step title in Persian
 * @param isCompleted Whether this step is completed
 * @param isCurrent Whether this is the current step
 * @param modifier Modifier for the indicator
 */
@Composable
private fun StepIndicator(
    stepNumber: Int,
    title: String,
    isCompleted: Boolean,
    isCurrent: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Circle indicator
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .then(
                    when {
                        isCompleted -> Modifier.background(Primary)
                        isCurrent -> Modifier
                            .background(Primary)
                            .border(3.dp, Primary100, CircleShape)
                        else -> Modifier
                            .background(Color.White)
                            .border(2.dp, Color(0xFFE5E7EB), CircleShape)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            when {
                isCompleted -> {
                    // Checkmark for completed steps
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "تکمیل شده",
                        modifier = Modifier.size(18.dp),
                        tint = Color.White
                    )
                }
                isCurrent -> {
                    // Step number for current step
                    Text(
                        text = stepNumber.toString(),
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                else -> {
                    // Step number for future steps
                    Text(
                        text = stepNumber.toString(),
                        color = Color(0xFF9CA3AF),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Step title
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
            color = when {
                isCompleted || isCurrent -> Primary
                else -> Color(0xFF9CA3AF)
            },
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Connecting line between step indicators.
 *
 * @param isCompleted Whether the preceding step is completed
 * @param modifier Modifier for the connector
 */
@Composable
private fun StepConnector(
    isCompleted: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.padding(bottom = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth(),
            thickness = 2.dp,
            color = if (isCompleted) Primary else Color(0xFFE5E7EB)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFCFAF8)
@Composable
private fun CheckoutStepperStep0Preview() {
    VoxcinaTheme {
        CheckoutStepper(
            currentStep = 0,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFCFAF8)
@Composable
private fun CheckoutStepperStep1Preview() {
    VoxcinaTheme {
        CheckoutStepper(
            currentStep = 1,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFCFAF8)
@Composable
private fun CheckoutStepperStep2Preview() {
    VoxcinaTheme {
        CheckoutStepper(
            currentStep = 2,
            modifier = Modifier.padding(16.dp)
        )
    }
}
