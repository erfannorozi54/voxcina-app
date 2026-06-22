package com.voxcina.shop.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.voxcina.shop.presentation.home.components.BottomNavBar
import com.voxcina.shop.presentation.home.components.BottomNavDestination
import com.voxcina.shop.presentation.profile.components.EditAccountButton
import com.voxcina.shop.presentation.profile.components.LogoutButton
import com.voxcina.shop.presentation.profile.components.OrderStatusSection
import com.voxcina.shop.presentation.profile.components.ProfileHeader
import com.voxcina.shop.presentation.profile.components.ProfileMenuGroup
import com.voxcina.shop.presentation.profile.components.ProfileMenuItem
import com.voxcina.shop.ui.components.EmptyState
import com.voxcina.shop.ui.components.VoxcinaLoading
import com.voxcina.shop.ui.components.VoxcinaPrimaryButton
import com.voxcina.shop.ui.theme.Destructive
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.SecondaryLight
import com.voxcina.shop.ui.theme.VoxcinaTheme

/**
 * Main Profile Screen composable that displays user profile information,
 * order status, and navigation menu items.
 */
@Composable
fun ProfileScreen(
    onNavigateToEditProfile: () -> Unit,
    onNavigateToEditAccount: () -> Unit,
    onNavigateToAddresses: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToRecentlyViewed: () -> Unit,
    onNavigateToPromotions: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToSupport: () -> Unit,
    onNavigateToTickets: () -> Unit,
    onNavigateToOrders: (OrderStatus?) -> Unit,
    onLogout: () -> Unit,
    onBottomNavClick: (BottomNavDestination) -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val showLogoutDialog by viewModel.showLogoutDialog.collectAsState()

    // Check if logout completed and navigate
    LaunchedEffect(uiState) {
        if (uiState is ProfileUiState.Success) {
            val success = uiState as ProfileUiState.Success
            if (success.isLoggingOut && !viewModel.isLoggedIn()) {
                onLogout()
            }
        }
    }

    ProfileScreenContent(
        uiState = uiState,
        showLogoutDialog = showLogoutDialog,
        onNavigateToEditProfile = onNavigateToEditProfile,
        onNavigateToEditAccount = onNavigateToEditAccount,
        onNavigateToAddresses = onNavigateToAddresses,
        onNavigateToFavorites = onNavigateToFavorites,
        onNavigateToRecentlyViewed = onNavigateToRecentlyViewed,
        onNavigateToPromotions = onNavigateToPromotions,
        onNavigateToSettings = onNavigateToSettings,
        onNavigateToSupport = onNavigateToSupport,
        onNavigateToTickets = onNavigateToTickets,
        onNavigateToOrders = onNavigateToOrders,
        onLogoutClick = { viewModel.onEvent(ProfileEvent.LogoutClicked) },
        onLogoutConfirm = { viewModel.onEvent(ProfileEvent.LogoutConfirmed) },
        onLogoutDismiss = { viewModel.onEvent(ProfileEvent.LogoutDismissed) },
        onRetry = { viewModel.onEvent(ProfileEvent.Retry) },
        onBottomNavClick = onBottomNavClick
    )
}


/**
 * Stateless content composable for ProfileScreen.
 * Handles UI state rendering (Loading, Success, Error).
 */
@Composable
private fun ProfileScreenContent(
    uiState: ProfileUiState,
    showLogoutDialog: Boolean,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToEditAccount: () -> Unit,
    onNavigateToAddresses: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToRecentlyViewed: () -> Unit,
    onNavigateToPromotions: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToSupport: () -> Unit,
    onNavigateToTickets: () -> Unit,
    onNavigateToOrders: (OrderStatus?) -> Unit,
    onLogoutClick: () -> Unit,
    onLogoutConfirm: () -> Unit,
    onLogoutDismiss: () -> Unit,
    onRetry: () -> Unit,
    onBottomNavClick: (BottomNavDestination) -> Unit
) {
    // Get cart item count for bottom nav badge
    val cartItemCount = when (uiState) {
        is ProfileUiState.Success -> uiState.cartItemCount
        is ProfileUiState.Error -> uiState.cachedData?.cartItemCount ?: 0
        else -> 0
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            bottomBar = {
                BottomNavBar(
                    selectedDestination = BottomNavDestination.PROFILE,
                    cartItemCount = cartItemCount,
                    onDestinationSelected = onBottomNavClick
                )
            },
            containerColor = SecondaryLight
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (uiState) {
                    is ProfileUiState.Loading -> {
                        ProfileLoadingState()
                    }
                    is ProfileUiState.Success -> {
                        ProfileSuccessState(
                            state = uiState,
                            onNavigateToEditProfile = onNavigateToEditProfile,
                            onNavigateToEditAccount = onNavigateToEditAccount,
                            onNavigateToAddresses = onNavigateToAddresses,
                            onNavigateToFavorites = onNavigateToFavorites,
                            onNavigateToRecentlyViewed = onNavigateToRecentlyViewed,
                            onNavigateToPromotions = onNavigateToPromotions,
                            onNavigateToSettings = onNavigateToSettings,
                            onNavigateToSupport = onNavigateToSupport,
                            onNavigateToTickets = onNavigateToTickets,
                            onNavigateToOrders = onNavigateToOrders,
                            onLogoutClick = onLogoutClick
                        )
                    }
                    is ProfileUiState.Error -> {
                        if (uiState.cachedData != null) {
                            // Show cached data with error banner
                            ProfileSuccessState(
                                state = uiState.cachedData,
                                onNavigateToEditProfile = onNavigateToEditProfile,
                                onNavigateToEditAccount = onNavigateToEditAccount,
                                onNavigateToAddresses = onNavigateToAddresses,
                                onNavigateToFavorites = onNavigateToFavorites,
                                onNavigateToRecentlyViewed = onNavigateToRecentlyViewed,
                                onNavigateToPromotions = onNavigateToPromotions,
                                onNavigateToSettings = onNavigateToSettings,
                                onNavigateToSupport = onNavigateToSupport,
                                onNavigateToTickets = onNavigateToTickets,
                                onNavigateToOrders = onNavigateToOrders,
                                onLogoutClick = onLogoutClick,
                                errorMessage = uiState.message
                            )
                        } else {
                            ProfileErrorState(
                                message = uiState.message,
                                onRetry = onRetry
                            )
                        }
                    }
                }

                // Logout confirmation dialog
                if (showLogoutDialog) {
                    LogoutConfirmationDialog(
                        onConfirm = onLogoutConfirm,
                        onDismiss = onLogoutDismiss
                    )
                }
            }
        }
    }
}

/**
 * Loading state for profile screen.
 * Displays centered loading indicator.
 *
 * Requirements: 9.1
 */
@Composable
private fun ProfileLoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        VoxcinaLoading(
            size = 100.dp,
            loadingText = "در حال بارگذاری پروفایل..."
        )
    }
}

/**
 * Error state for profile screen.
 * Displays error message with retry button.
 *
 * Requirements: 9.2
 */
@Composable
private fun ProfileErrorState(
    message: String,
    onRetry: () -> Unit
) {
    EmptyState(
        icon = Icons.Default.Error,
        title = "خطا در بارگذاری",
        subtitle = message,
        actionButtonText = "تلاش مجدد",
        onActionClick = onRetry,
        iconTint = Destructive.copy(alpha = 0.7f)
    )
}


/**
 * Success state for profile screen.
 * Displays all profile sections in a scrollable column.
 */
@Composable
private fun ProfileSuccessState(
    state: ProfileUiState.Success,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToEditAccount: () -> Unit,
    onNavigateToAddresses: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToRecentlyViewed: () -> Unit,
    onNavigateToPromotions: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToSupport: () -> Unit,
    onNavigateToTickets: () -> Unit,
    onNavigateToOrders: (OrderStatus?) -> Unit,
    onLogoutClick: () -> Unit,
    errorMessage: String? = null
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp)
    ) {
        // Error banner if showing cached data with error
        if (errorMessage != null) {
            ErrorBanner(message = errorMessage)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Profile Header - Requirements: 1.1-1.6
        ProfileHeader(
            avatarUrl = state.avatarUrl,
            userName = state.userName,
            phoneNumber = state.phoneNumber,
            onEditClick = onNavigateToEditProfile,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Edit Account Button - Requirements: 2.1-2.4
        EditAccountButton(
            onClick = onNavigateToEditAccount,
            enabled = !state.isLoggingOut
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Order Status Section - Requirements: 4.1-4.10
        OrderStatusSection(
            pendingCount = state.pendingOrdersCount,
            processingCount = state.processingOrdersCount,
            shippedCount = state.shippedOrdersCount,
            returnedCount = state.returnedOrdersCount,
            onStatusClick = { status ->
                onNavigateToOrders(
                    when (status) {
                        com.voxcina.shop.presentation.profile.components.OrderStatus.PENDING -> OrderStatus.PENDING
                        com.voxcina.shop.presentation.profile.components.OrderStatus.PROCESSING -> OrderStatus.PROCESSING
                        com.voxcina.shop.presentation.profile.components.OrderStatus.SHIPPED -> OrderStatus.SHIPPED
                        com.voxcina.shop.presentation.profile.components.OrderStatus.RETURNED -> OrderStatus.RETURNED
                    }
                )
            },
            onViewAllClick = { onNavigateToOrders(null) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Account Menu Group
        AccountMenuGroup(
            onAddressesClick = onNavigateToAddresses,
            onFavoritesClick = onNavigateToFavorites,
            onRecentlyViewedClick = onNavigateToRecentlyViewed,
            onPromotionsClick = onNavigateToPromotions
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Settings Menu Group
        SettingsMenuGroup(
            onSettingsClick = onNavigateToSettings,
            onSupportClick = onNavigateToSupport,
            onTicketsClick = onNavigateToTickets
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Logout Button - Requirements: 7.1-7.3
        LogoutButton(
            onClick = onLogoutClick,
            enabled = !state.isLoggingOut
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

/**
 * Error banner displayed at top when showing cached data with error.
 */
@Composable
private fun ErrorBanner(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Destructive.copy(alpha = 0.1f),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
            )
            .padding(12.dp)
    ) {
        Text(
            text = message,
            color = Destructive,
            style = androidx.compose.material3.MaterialTheme.typography.bodySmall
        )
    }
}


/**
 * Account menu group containing addresses, favorites, recently viewed, and promotions.
 */
@Composable
private fun AccountMenuGroup(
    onAddressesClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onRecentlyViewedClick: () -> Unit,
    onPromotionsClick: () -> Unit
) {
    ProfileMenuGroup {
        ProfileMenuItem(
            icon = Icons.Default.LocationOn,
            label = "آدرسهای من",
            onClick = onAddressesClick,
            showDivider = true
        )

        ProfileMenuItem(
            icon = Icons.Default.Favorite,
            label = "علاقهمندیها",
            onClick = onFavoritesClick,
            showDivider = true
        )

        ProfileMenuItem(
            icon = Icons.Default.History,
            label = "بازدیدهای اخیر",
            onClick = onRecentlyViewedClick,
            showDivider = true
        )

        ProfileMenuItem(
            icon = Icons.Outlined.LocalOffer,
            label = "کدهای تخفیف من",
            onClick = onPromotionsClick,
            showDivider = false
        )
    }
}

/**
 * Settings menu group containing settings and support items.
 */
@Composable
private fun SettingsMenuGroup(
    onSettingsClick: () -> Unit,
    onSupportClick: () -> Unit,
    onTicketsClick: () -> Unit
) {
    ProfileMenuGroup {
        // Settings - Requirements: 6.2
        ProfileMenuItem(
            icon = Icons.Default.Settings,
            label = "تنظیمات",
            onClick = onSettingsClick,
            showDivider = true
        )

        // Tickets
        ProfileMenuItem(
            icon = Icons.Default.ConfirmationNumber,
            label = "تیکتهای پشتیبانی",
            onClick = onTicketsClick,
            showDivider = true
        )

        // Support & FAQ - Requirements: 6.3
        ProfileMenuItem(
            icon = Icons.Default.HeadsetMic,
            label = "سوالات متداول",
            onClick = onSupportClick,
            showDivider = false
        )
    }
}

/**
 * Logout confirmation dialog.
 *
 * Requirements: 7.3, 7.4, 7.5
 */
@Composable
private fun LogoutConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "خروج از حساب کاربری",
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        },
        text = {
            Text(
                text = "آیا مطمئن هستید که می‌خواهید از حساب کاربری خود خارج شوید؟",
                color = Color.Gray
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = "خروج",
                    color = Destructive,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "انصراف",
                    color = Primary
                )
            }
        },
        containerColor = Color.White
    )
}


