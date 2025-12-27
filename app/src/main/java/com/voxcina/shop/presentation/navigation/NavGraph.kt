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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.voxcina.shop.BuildConfig
import com.voxcina.shop.data.local.TokenManager
import com.voxcina.shop.presentation.auth.AuthScreen
import com.voxcina.shop.presentation.cart.CartScreen
import com.voxcina.shop.presentation.home.HomeScreen
import com.voxcina.shop.presentation.home.components.BottomNavDestination
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
    
    /** Home screen - main landing page after authentication */
    data object Home : Screen("home")
    
    /** Product detail screen with productId and colorHex parameters */
    data object ProductDetail : Screen("product/{productId}?color={colorHex}") {
        fun createRoute(productId: String, colorHex: String): String {
            return "product/$productId?color=$colorHex"
        }
    }
    
    /** Categories list screen */
    data object Categories : Screen("categories")
    
    /** Category products screen with categoryId parameter */
    data object CategoryProducts : Screen("category/{categoryId}") {
        fun createRoute(categoryId: String): String {
            return "category/$categoryId"
        }
    }
    
    /** Cart screen */
    data object Cart : Screen("cart")
    
    /** Profile screen */
    data object Profile : Screen("profile")
    
    /** Search screen */
    data object Search : Screen("search")
    
    /** Flash sale products screen */
    data object FlashSale : Screen("flash-sale")
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
                        // If user is already authenticated, go to home
                        tokenManager.isLoggedIn() -> Screen.Home.route
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
                    // Navigate to auth screen (not home) and remove onboarding from back stack
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
                    // Navigate to home screen and remove auth from back stack
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                },
                smsRetrieverHelper = smsRetrieverHelper
            )
        }
        
        composable(route = Screen.Home.route) {
            HomeScreen(
                onCategoryClick = { categoryId ->
                    // Navigate to category products screen (Requirement 3.4)
                    navController.navigate(Screen.CategoryProducts.createRoute(categoryId))
                },
                onProductClick = { productId, colorHex ->
                    // Navigate to product detail screen (Requirement 5.7)
                    navController.navigate(Screen.ProductDetail.createRoute(productId, colorHex))
                },
                onRecentlyViewedClick = { productId, colorHex ->
                    // Navigate to product detail screen (Requirement 10.7)
                    navController.navigate(Screen.ProductDetail.createRoute(productId, colorHex))
                },
                onViewAllFlashSale = {
                    navController.navigate(Screen.FlashSale.route)
                },
                onViewAllCategories = {
                    navController.navigate(Screen.Categories.route)
                },
                onSearchClick = {
                    navController.navigate(Screen.Search.route)
                },
                onNotificationClick = {
                    // TODO: Navigate to notifications screen
                },
                onCartClick = {
                    navController.navigate(Screen.Cart.route)
                },
                onBottomNavClick = { destination ->
                    when (destination) {
                        BottomNavDestination.HOME -> {
                            // Already on home, do nothing
                        }
                        BottomNavDestination.CATEGORIES -> {
                            navController.navigate(Screen.Categories.route)
                        }
                        BottomNavDestination.CART -> {
                            navController.navigate(Screen.Cart.route)
                        }
                        BottomNavDestination.PROFILE -> {
                            navController.navigate(Screen.Profile.route)
                        }
                    }
                }
            )
        }
        
        // Product detail screen with arguments
        composable(
            route = Screen.ProductDetail.route,
            arguments = listOf(
                navArgument("productId") { type = NavType.StringType },
                navArgument("colorHex") { 
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            val colorHex = backStackEntry.arguments?.getString("colorHex") ?: ""
            // TODO: Implement ProductDetailScreen
            PlaceholderScreen(title = "جزئیات محصول")
        }
        
        // Categories list screen
        composable(route = Screen.Categories.route) {
            // TODO: Implement CategoriesScreen
            PlaceholderScreen(title = "دسته‌بندی‌ها")
        }
        
        // Category products screen with argument
        composable(
            route = Screen.CategoryProducts.route,
            arguments = listOf(
                navArgument("categoryId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""
            // TODO: Implement CategoryProductsScreen
            PlaceholderScreen(title = "محصولات دسته‌بندی")
        }
        
        // Cart screen
        composable(route = Screen.Cart.route) {
            CartScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onCheckout = {
                    // TODO: Navigate to checkout screen
                },
                onStartShopping = {
                    // Navigate to home screen when user clicks "Start Shopping" in empty cart
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Cart.route) { inclusive = true }
                    }
                },
                onBottomNavClick = { destination ->
                    when (destination) {
                        BottomNavDestination.HOME -> {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Cart.route) { inclusive = true }
                            }
                        }
                        BottomNavDestination.CATEGORIES -> {
                            navController.navigate(Screen.Categories.route)
                        }
                        BottomNavDestination.CART -> {
                            // Already on cart, do nothing
                        }
                        BottomNavDestination.PROFILE -> {
                            navController.navigate(Screen.Profile.route)
                        }
                    }
                }
            )
        }
        
        // Profile screen
        composable(route = Screen.Profile.route) {
            // TODO: Implement ProfileScreen
            PlaceholderScreen(title = "پروفایل")
        }
        
        // Search screen
        composable(route = Screen.Search.route) {
            // TODO: Implement SearchScreen
            PlaceholderScreen(title = "جستجو")
        }
        
        // Flash sale products screen
        composable(route = Screen.FlashSale.route) {
            // TODO: Implement FlashSaleScreen
            PlaceholderScreen(title = "پیشنهادات شگفت‌انگیز")
        }
    }
}

/**
 * Placeholder screen for screens not yet implemented.
 * Will be replaced with actual implementations.
 */
@Composable
private fun PlaceholderScreen(title: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SecondaryLight),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Primary
        )
    }
}
