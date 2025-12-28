package com.voxcina.shop.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.voxcina.shop.ui.theme.VazirMatnFamily
import kotlinx.coroutines.delay

enum class NotificationType {
    Success, Error, Warning, Info
}

data class NotificationState(
    val message: String = "",
    val type: NotificationType = NotificationType.Success,
    val isVisible: Boolean = false
)

@Composable
fun GlassNotification(
    state: NotificationState,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    durationMillis: Long = 2500
) {
    LaunchedEffect(state.isVisible) {
        if (state.isVisible) {
            delay(durationMillis)
            onDismiss()
        }
    }
    
    AnimatedVisibility(
        visible = state.isVisible,
        enter = slideInVertically(tween(300)) { -it } + fadeIn(tween(300)),
        exit = slideOutVertically(tween(250)) { -it } + fadeOut(tween(200)),
        modifier = modifier
    ) {
        val (icon, accentColor) = when (state.type) {
            NotificationType.Success -> Icons.Rounded.Check to Color(0xFF10B981)
            NotificationType.Error -> Icons.Rounded.Close to Color(0xFFEF4444)
            NotificationType.Warning -> Icons.Rounded.Warning to Color(0xFFF59E0B)
            NotificationType.Info -> Icons.Rounded.Info to Color(0xFF3B82F6)
        }
        
        GlassNotificationContent(
            message = state.message,
            icon = icon,
            accentColor = accentColor
        )
    }
}

@Composable
private fun GlassNotificationContent(
    message: String,
    icon: ImageVector,
    accentColor: Color
) {
    val shape = RoundedCornerShape(16.dp)
    
    Box(
        modifier = Modifier
            .clip(shape)
            .background(Color.Black.copy(alpha = 0.6f))
            .border(1.dp, Color.White.copy(alpha = 0.2f), shape)
            .blur(0.5.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(accentColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(16.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Text(
                text = message,
                color = Color.White,
                fontSize = 13.sp,
                fontFamily = VazirMatnFamily,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

class NotificationManager {
    var state by mutableStateOf(NotificationState())
        private set
    
    fun show(message: String, type: NotificationType = NotificationType.Success) {
        state = NotificationState(message, type, true)
    }
    
    fun dismiss() {
        state = state.copy(isVisible = false)
    }
}

@Composable
fun rememberNotificationManager(): NotificationManager {
    return remember { NotificationManager() }
}
