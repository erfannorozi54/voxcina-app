package com.voxcina.shop.presentation.profile.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.VoxcinaTheme

/**
 * Edit account button with primary/10 background that transitions to primary
 * background with white text on hover/press.
 *
 * @param onClick Callback when the button is clicked
 * @param modifier Modifier for the button
 * @param enabled Whether the button is enabled
 */
@Composable
fun EditAccountButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    // Animate colors on press
    val backgroundColor by animateColorAsState(
        targetValue = if (isPressed) Primary else Primary.copy(alpha = 0.1f),
        animationSpec = tween(durationMillis = 150),
        label = "backgroundColor"
    )
    
    val contentColor by animateColorAsState(
        targetValue = if (isPressed) Color.White else Primary,
        animationSpec = tween(durationMillis = 150),
        label = "contentColor"
    )
    
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor,
            disabledContainerColor = Primary.copy(alpha = 0.05f),
            disabledContentColor = Primary.copy(alpha = 0.4f)
        ),
        interactionSource = interactionSource,
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp
        )
    ) {
        Text(
            text = "ویرایش حساب کاربری",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
