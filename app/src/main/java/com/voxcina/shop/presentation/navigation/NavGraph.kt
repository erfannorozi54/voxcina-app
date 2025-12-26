package com.voxcina.shop.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.voxcina.shop.BuildConfig
import com.voxcina.shop.Greeting
import com.voxcina.shop.presentation.onboarding.OnboardingScreen
import com.voxcina.shop.presentation.splash.SplashScreen
import com.voxcina.shop.util.OnboardingManager

/**
 * Sealed class defining all navigation routes in the app.
 */
sealed class Screen(val route: String) {
    /** Splash screen - initial destination */
    data object Splash : Screen("splash")
    
    /** Onboarding screen - shown to first-time users after splash */
    data object Onboarding : Screen("onboarding")
    
    /** Main screen - displays after splash/onboarding completion */
    data object Main : Screen("main")
}

/**
 * Main navigation graph for the app.
 * 
 * @param navController The navigation controller to manage navigation
 * @param onboardingManager Manager for checking/setting onboarding completion state
 * @param modifier Optional modifier for the NavHost
 */
@Composable
fun NavGraph(
    navController: NavHostController,
    onboardingManager: OnboardingManager,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {
        composable(route = Screen.Splash.route) {
            SplashScreen(
                onSplashComplete = {
                    // Check if onboarding has been completed
                    val destination = if (onboardingManager.isOnboardingCompleted()) {
                        Screen.Main.route
                    } else {
                        Screen.Onboarding.route
                    }
                    navController.navigate(destination) {
                        // Remove splash from back stack so user can't navigate back to it
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(route = Screen.Onboarding.route) {
            OnboardingScreen(
                onOnboardingComplete = {
                    // Mark onboarding as completed
                    onboardingManager.setOnboardingCompleted()
                    // Navigate to main screen and remove onboarding from back stack
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                },
                appVersion = BuildConfig.VERSION_NAME
            )
        }
        
        composable(route = Screen.Main.route) {
            MainScreen()
        }
    }
}

/**
 * Main screen composable that displays the existing Greeting content.
 */
@Composable
fun MainScreen() {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Greeting(
            name = "Android",
            modifier = Modifier.padding(innerPadding)
        )
    }
}
