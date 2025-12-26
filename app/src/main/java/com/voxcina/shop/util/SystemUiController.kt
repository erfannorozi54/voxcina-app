package com.voxcina.shop.util

import android.app.Activity
import android.os.Build
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

/**
 * Controls system UI visibility for immersive mode experiences.
 * Uses WindowInsetsController for API 30+ with fallback for older APIs.
 */
object SystemUiController {

    /**
     * Hides system bars (status bar and navigation bar) for immersive mode.
     * 
     * @param activity The activity whose window will be modified
     */
    fun hideSystemBars(activity: Activity) {
        val window = activity.window
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // API 30+ - Use WindowInsetsController
            window.insetsController?.let { controller ->
                controller.hide(WindowInsets.Type.systemBars())
                controller.systemBarsBehavior = 
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            // Fallback for older APIs using WindowInsetsControllerCompat
            WindowCompat.setDecorFitsSystemWindows(window, false)
            val controller = WindowInsetsControllerCompat(window, window.decorView)
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = 
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    /**
     * Shows system bars (status bar and navigation bar), restoring normal visibility.
     * 
     * @param activity The activity whose window will be modified
     */
    fun showSystemBars(activity: Activity) {
        val window = activity.window
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // API 30+ - Use WindowInsetsController
            window.insetsController?.show(WindowInsets.Type.systemBars())
        } else {
            // Fallback for older APIs using WindowInsetsControllerCompat
            WindowCompat.setDecorFitsSystemWindows(window, true)
            val controller = WindowInsetsControllerCompat(window, window.decorView)
            controller.show(WindowInsetsCompat.Type.systemBars())
        }
    }
}

/**
 * Composable effect that enables immersive mode by hiding system bars.
 * System bars are automatically restored when the composable leaves composition.
 * 
 * @param enabled Whether immersive mode should be active
 */
@Composable
fun ImmersiveModeEffect(enabled: Boolean = true) {
    val view = LocalView.current
    
    DisposableEffect(enabled) {
        val activity = view.context as? Activity
        
        if (enabled && activity != null) {
            SystemUiController.hideSystemBars(activity)
        }
        
        onDispose {
            activity?.let { SystemUiController.showSystemBars(it) }
        }
    }
}
