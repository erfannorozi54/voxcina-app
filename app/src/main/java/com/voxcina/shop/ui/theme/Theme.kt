package com.voxcina.shop.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val VoxcinaLightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    primaryContainer = Primary100,
    onPrimaryContainer = PrimaryDark,
    secondary = Secondary,
    onSecondary = PrimaryDark,
    secondaryContainer = SecondaryLight,
    onSecondaryContainer = PrimaryDark,
    background = SecondaryLight,
    onBackground = PrimaryDark,
    surface = Color.White,
    onSurface = PrimaryDark,
    surfaceVariant = Secondary,
    onSurfaceVariant = PrimaryDark.copy(alpha = 0.7f),
    error = Destructive,
    onError = Color.White
)

@Composable
fun VoxcinaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Always use light color scheme - app is designed for light mode
    val colorScheme = VoxcinaLightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = SecondaryLight.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}