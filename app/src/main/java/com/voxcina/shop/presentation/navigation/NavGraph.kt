package com.voxcina.shop.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.voxcina.shop.BuildConfig
import com.voxcina.shop.data.local.TokenManager
import com.voxcina.shop.presentation.auth.AuthScreen
import com.voxcina.shop.presentation.onboarding.OnboardingScreen
import com.voxcina.shop.presentation.splash.SplashScreen
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.SecondaryLight
import com.voxcina.shop.util.OnboardingManager
import com.voxcina.shop.util.SmsRetrieverHelper

/**
 * Sealed class defining all navigation routes in the app.
 */
sealed class Screen(val route: String) {
    /** Splash screen - initial destination */
    data object Splash : Screen("splash")
    
    /** Onboarding screen - shown to first-time users after splash */
    data object Onboarding : Screen("onboarding")
    
    /** Authentication screen - login/signup flows */
    data object Auth : Screen("auth")
    
    /** Main screen - displays after authentication */
    data object Main : Screen("main")
}

/**
 * Main navigation graph for the app.
 * 
 * @param navController The navigation controller to manage navigation
 * @param onboardingManager Manager for checking/setting onboarding completion state
 * @param tokenManager Manager for checking authentication state
 * @param modifier Optional modifier for the NavHost
 */
@Composable
fun NavGraph(
    navController: NavHostController,
    onboardingManager: OnboardingManager,
    tokenManager: TokenManager,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val smsRetrieverHelper = remember { SmsRetrieverHelper(context) }
    
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {
        composable(route = Screen.Splash.route) {
            SplashScreen(
                onSplashComplete = {
                    // Determine destination based on onboarding and auth state
                    val destination = when {
                        // If user is already authenticated, go to main
                        tokenManager.isLoggedIn() -> Screen.Main.route
                        // If onboarding not completed, show onboarding
                        !onboardingManager.isOnboardingCompleted() -> Screen.Onboarding.route
                        // Otherwise, go to auth screen
                        else -> Screen.Auth.route
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
                    // Navigate to auth screen (not main) and remove onboarding from back stack
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                },
                appVersion = BuildConfig.VERSION_NAME
            )
        }
        
        composable(route = Screen.Auth.route) {
            AuthScreen(
                onAuthSuccess = {
                    // Navigate to main screen and remove auth from back stack
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                },
                smsRetrieverHelper = smsRetrieverHelper
            )
        }
        
        composable(route = Screen.Main.route) {
            MainScreen()
        }
    }
}

/**
 * Main screen composable - placeholder for future home screen.
 * Requirements: 11.2
 */
@Composable
fun MainScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SecondaryLight),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "خوش آمدید به وکسینا",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Primary
        )
    }
}
