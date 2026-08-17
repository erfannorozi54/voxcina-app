package com.voxcina.shop.presentation.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
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
import com.voxcina.shop.presentation.addresses.AddressesScreen
import com.voxcina.shop.presentation.auth.AuthScreen
import com.voxcina.shop.presentation.cart.CartScreen
import com.voxcina.shop.presentation.checkout.CheckoutScreen
import com.voxcina.shop.presentation.home.HomeScreen
import com.voxcina.shop.presentation.home.components.BottomNavDestination
import com.voxcina.shop.presentation.onboarding.OnboardingScreen
import com.voxcina.shop.presentation.orders.OrderDetailScreen
import com.voxcina.shop.presentation.orders.OrdersScreen
import com.voxcina.shop.presentation.productdetail.ProductDetailScreen
import com.voxcina.shop.presentation.products.ProductsScreen
import com.voxcina.shop.presentation.profile.OrderStatus
import com.voxcina.shop.presentation.profile.ProfileScreen
import com.voxcina.shop.presentation.promotions.PromotionsScreen
import com.voxcina.shop.presentation.recentlyviewed.RecentlyViewedScreen
import com.voxcina.shop.presentation.splash.SplashScreen
import com.voxcina.shop.presentation.support.SupportScreen
import com.voxcina.shop.presentation.tickets.TicketsScreen
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
            // Remove # prefix as it's a URL fragment identifier
            val cleanColor = colorHex.removePrefix("#")
            return "product/$productId?color=$cleanColor"
        }
    }
    
    /** Categories list screen */
    data object Categories : Screen("categories")
    
    /** Products list screen with optional category filter */
    data object Products : Screen("products?categoryId={categoryId}") {
        fun createRoute(categoryId: String? = null): String {
            return if (categoryId != null) "products?categoryId=$categoryId" else "products"
        }
    }
    
    /** Category products screen with categoryId parameter */
    data object CategoryProducts : Screen("category/{categoryId}") {
        fun createRoute(categoryId: String): String {
            return "category/$categoryId"
        }
    }
    
    /** Cart screen */
    data object Cart : Screen("cart")
    
    /** Checkout screen */
    data object Checkout : Screen("checkout")
    
    /** Payment result screen with parameters */
    data object PaymentResult : Screen("payment-result?orderId={orderId}&trackId={trackId}&success={success}&gateway={gateway}") {
        fun createRoute(orderId: String, trackId: String, success: Boolean, gateway: String): String =
            "payment-result?orderId=$orderId&trackId=$trackId&success=$success&gateway=$gateway"
    }
    
    /** Profile screen */
    data object Profile : Screen("profile")
    
    /** Edit profile screen */
    data object EditProfile : Screen("profile/edit")
    
    /** Edit account screen */
    data object EditAccount : Screen("account/edit")
    
    /** Orders screen with optional status filter */
    data object Orders : Screen("orders?status={status}") {
        fun createRoute(status: OrderStatus? = null): String {
            return if (status != null) {
                "orders?status=${status.apiValue}"
            } else {
                "orders"
            }
        }
    }
    
    /** Order detail screen */
    data object OrderDetail : Screen("orders/{orderId}") {
        fun createRoute(orderId: String): String = "orders/$orderId"
    }
    
    /** Addresses screen */
    data object Addresses : Screen("addresses")
    
    /** Favorites screen */
    data object Favorites : Screen("favorites")
    
    /** Recently viewed screen */
    data object RecentlyViewed : Screen("recently-viewed")
    
    /** Promotions screen */
    data object Promotions : Screen("promotions")
    
    /** Settings screen */
    data object Settings : Screen("settings")
    
    /** Support screen */
    data object Support : Screen("support")
    
    /** Wallet screen */
    data object Wallet : Screen("wallet")
    
    /** Loyalty points screen */
    data object Loyalty : Screen("loyalty")
    
    /** Coupons screen */
    data object Coupons : Screen("coupons")
    
    /** Search screen */
    data object Search : Screen("search")
    
    /** Flash sale products screen */
    data object FlashSale : Screen("flash-sale")
    
    /** Tickets screen */
    data object Tickets : Screen("tickets")
    
    /** Ticket detail screen */
    data object TicketDetail : Screen("tickets/{ticketId}") {
        fun createRoute(ticketId: String): String = "tickets/$ticketId"
    }
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
    smsRetrieverHelper: SmsRetrieverHelper? = null,
    modifier: Modifier = Modifier
) {
    
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None }
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
                    // Navigate to products screen with category filter
                    navController.navigate(Screen.Products.createRoute(categoryId))
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
                    navController.navigate(Screen.Products.route)
                },
                onViewAllRecentlyViewed = {
                    navController.navigate(Screen.RecentlyViewed.route)
                },
                onHeroLinkClick = { href ->
                    // Hero CTA buttons link to web routes; map them to the
                    // closest in-app destination.
                    when {
                        href.startsWith("/products") -> navController.navigate(Screen.Products.route)
                        href.startsWith("/categories/") ||
                            href.startsWith("/category/") ||
                            href.startsWith("/collection/") -> navController.navigate(Screen.Products.route)
                        else -> { /* unknown href — ignore */ }
                    }
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
                        BottomNavDestination.PRODUCTS -> {
                            navController.navigate(Screen.Products.route)
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
        // Requirements: 2.2, 12.1, 12.2, 12.3
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
            
            // Check if previous destination is Home for optimized back navigation
            val previousRoute = navController.previousBackStackEntry?.destination?.route
            val isFromHome = previousRoute == Screen.Home.route
            
            ProductDetailScreen(
                productId = productId,
                initialColorHex = colorHex.ifEmpty { null },
                onNavigateBack = {
                    if (isFromHome) {
                        // Navigate to Home for instant transition with loading skeleton
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    } else {
                        // Normal back navigation for other screens
                        navController.popBackStack()
                    }
                },
                onNavigateToReviews = { reviewProductId ->
                    // TODO: Navigate to reviews screen when implemented
                    // navController.navigate(Screen.Reviews.createRoute(reviewProductId))
                },
                onBottomNavClick = { destination ->
                    // Requirements 12.1, 12.2, 12.3: Bottom navigation handling
                    when (destination) {
                        BottomNavDestination.HOME -> {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                        }
                        BottomNavDestination.PRODUCTS -> {
                            navController.navigate(Screen.Products.route)
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
        
        // Products list screen
        composable(
            route = Screen.Products.route,
            arguments = listOf(
                navArgument("categoryId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId")
            ProductsScreen(
                initialCategoryId = categoryId,
                onProductClick = { productId, colorHex ->
                    navController.navigate(Screen.ProductDetail.createRoute(productId, colorHex))
                },
                onBottomNavClick = { destination ->
                    when (destination) {
                        BottomNavDestination.HOME -> {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Products.route) { inclusive = true }
                            }
                        }
                        BottomNavDestination.PRODUCTS -> {
                            // Already on products, do nothing
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
                    // Navigate to checkout screen (Requirement 1.2, 8.7)
                    navController.navigate(Screen.Checkout.route)
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
                        BottomNavDestination.PRODUCTS -> {
                            navController.navigate(Screen.Products.route)
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
        
        // Checkout screen
        // Requirements: 1.2, 3.4, 8.7
        composable(route = Screen.Checkout.route) {
            CheckoutScreen(
                onNavigateBack = {
                    // Requirement 1.2: Navigate back to Cart screen
                    navController.popBackStack()
                },
                onNavigateToAddresses = {
                    // Requirement 3.4: Navigate to address selection screen
                    navController.navigate(Screen.Addresses.route)
                },
                onNavigateToPaymentResult = { orderId, trackId, gateway ->
                    // Navigate to payment result screen after returning from browser
                    navController.navigate(Screen.PaymentResult.createRoute(orderId, trackId, false, gateway)) {
                        // Clear checkout from back stack
                        popUpTo(Screen.Cart.route) { inclusive = true }
                    }
                },
                onBottomNavClick = { destination ->
                    when (destination) {
                        BottomNavDestination.HOME -> {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Checkout.route) { inclusive = true }
                            }
                        }
                        BottomNavDestination.PRODUCTS -> {
                            navController.navigate(Screen.Products.route)
                        }
                        BottomNavDestination.CART -> {
                            // Navigate back to cart
                            navController.popBackStack()
                        }
                        BottomNavDestination.PROFILE -> {
                            navController.navigate(Screen.Profile.route)
                        }
                    }
                }
            )
        }
        
        // Payment result screen
        composable(
            route = Screen.PaymentResult.route,
            arguments = listOf(
                navArgument("orderId") { type = NavType.StringType; defaultValue = "" },
                navArgument("trackId") { type = NavType.StringType; defaultValue = "" },
                navArgument("success") { type = NavType.BoolType; defaultValue = false },
                navArgument("gateway") { type = NavType.StringType; defaultValue = "zibal" }
            )
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            val trackId = backStackEntry.arguments?.getString("trackId") ?: ""
            val success = backStackEntry.arguments?.getBoolean("success") ?: false
            val gateway = backStackEntry.arguments?.getString("gateway") ?: "zibal"
            
            com.voxcina.shop.presentation.payment.PaymentResultScreen(
                orderId = orderId,
                trackId = trackId,
                gateway = gateway,
                isSuccess = success,
                onNavigateToOrders = {
                    navController.navigate(Screen.Orders.createRoute()) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                    }
                },
                onRetryPayment = {
                    navController.navigate(Screen.Checkout.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                    }
                }
            )
        }
        
        // Profile screen
        // Requirements: 1.6, 2.3, 4.8, 4.9, 5.7, 6.5, 6.6, 7.4
        composable(route = Screen.Profile.route) {
            ProfileScreen(
                onNavigateToEditProfile = {
                    // Requirement 1.6: Navigate to profile edit screen
                    navController.navigate(Screen.EditProfile.route)
                },
                onNavigateToEditAccount = {
                    // Requirement 2.3: Navigate to account edit screen
                    navController.navigate(Screen.EditAccount.route)
                },
                onNavigateToAddresses = {
                    navController.navigate(Screen.Addresses.route)
                },
                onNavigateToFavorites = {
                    navController.navigate(Screen.Favorites.route)
                },
                onNavigateToRecentlyViewed = {
                    navController.navigate(Screen.RecentlyViewed.route)
                },
                onNavigateToPromotions = {
                    navController.navigate(Screen.Promotions.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                onNavigateToSupport = {
                    navController.navigate(Screen.Support.route)
                },
                onNavigateToTickets = {
                    navController.navigate(Screen.Tickets.route)
                },
                onNavigateToOrders = { status ->
                    navController.navigate(Screen.Orders.createRoute(status))
                },
                onLogout = {
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onBottomNavClick = { destination ->
                    when (destination) {
                        BottomNavDestination.HOME -> {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Profile.route) { inclusive = true }
                            }
                        }
                        BottomNavDestination.PRODUCTS -> {
                            navController.navigate(Screen.Products.route)
                        }
                        BottomNavDestination.CART -> {
                            navController.navigate(Screen.Cart.route)
                        }
                        BottomNavDestination.PROFILE -> {
                            // Already on profile, do nothing
                        }
                    }
                }
            )
        }
        
        // Edit profile screen
        composable(route = Screen.EditProfile.route) {
            // TODO: Implement EditProfileScreen
            PlaceholderScreen(title = "ویرایش پروفایل")
        }
        
        // Edit account screen
        composable(route = Screen.EditAccount.route) {
            // TODO: Implement EditAccountScreen
            PlaceholderScreen(title = "ویرایش حساب کاربری")
        }
        

        // Orders screen with optional status filter
        composable(
            route = Screen.Orders.route,
            arguments = listOf(
                navArgument("status") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) {
            OrdersScreen(
                onNavigateBack = { navController.popBackStack() },
                onOrderClick = { orderId ->
                    navController.navigate(Screen.OrderDetail.createRoute(orderId))
                }
            )
        }
        
        // Order detail screen
        composable(
            route = Screen.OrderDetail.route,
            arguments = listOf(
                navArgument("orderId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: return@composable
            OrderDetailScreen(
                orderId = orderId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(route = Screen.Addresses.route) {
            AddressesScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Favorites screen
        composable(route = Screen.Favorites.route) {
            // TODO: Implement FavoritesScreen
            PlaceholderScreen(title = "علاقه‌مندی‌ها")
        }
        
        // Recently viewed screen
        composable(route = Screen.RecentlyViewed.route) {
            RecentlyViewedScreen(
                onNavigateBack = { navController.popBackStack() },
                onProductClick = { productId, colorHex ->
                    navController.navigate(Screen.ProductDetail.createRoute(productId, colorHex))
                }
            )
        }
        
        // Promotions screen
        composable(route = Screen.Promotions.route) {
            PromotionsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Settings screen
        composable(route = Screen.Settings.route) {
            // TODO: Implement SettingsScreen
            PlaceholderScreen(title = "تنظیمات")
        }
        
        // Support screen
        composable(route = Screen.Support.route) {
            SupportScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Tickets screen
        composable(route = Screen.Tickets.route) {
            TicketsScreen(
                onNavigateBack = { navController.popBackStack() },
                onTicketClick = { ticketId ->
                    navController.navigate(Screen.TicketDetail.createRoute(ticketId))
                }
            )
        }
        
        // Ticket detail screen
        composable(
            route = Screen.TicketDetail.route,
            arguments = listOf(navArgument("ticketId") { type = NavType.StringType })
        ) {
            // TODO: Implement TicketDetailScreen
            PlaceholderScreen(title = "جزئیات تیکت")
        }
        
        // Wallet screen
        composable(route = Screen.Wallet.route) {
            // TODO: Implement WalletScreen
            PlaceholderScreen(title = "کیف پول")
        }
        
        // Loyalty screen
        composable(route = Screen.Loyalty.route) {
            // TODO: Implement LoyaltyScreen
            PlaceholderScreen(title = "امتیاز باشگاه")
        }
        
        // Coupons screen
        composable(route = Screen.Coupons.route) {
            // TODO: Implement CouponsScreen
            PlaceholderScreen(title = "کوپن‌های من")
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
