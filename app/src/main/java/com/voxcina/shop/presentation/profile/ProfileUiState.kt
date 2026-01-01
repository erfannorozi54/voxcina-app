package com.voxcina.shop.presentation.profile

/**
 * Sealed class representing all possible UI states for the profile screen.
 *
 * Requirements: 9.1, 9.2, 9.4
 */
sealed class ProfileUiState {

    /**
     * Initial loading state when the profile screen first loads.
     * Displays skeleton/shimmer placeholders.
     *
     * Requirements: 9.1
     */
    data object Loading : ProfileUiState()

    /**
     * Success state containing all profile data.
     *
     * Requirements: 9.2
     */
    data class Success(
        val userName: String,
        val phoneNumber: String,
        val avatarUrl: String?,
        val walletBalance: Long,
        val loyaltyPoints: Int,
        val activeCoupons: Int,
        val pendingOrdersCount: Int,
        val processingOrdersCount: Int,
        val shippedOrdersCount: Int,
        val returnedOrdersCount: Int,
        val cartItemCount: Int = 0,
        val isLoggingOut: Boolean = false
    ) : ProfileUiState() {

        /**
         * Returns the total number of orders across all statuses.
         */
        val totalOrdersCount: Int
            get() = pendingOrdersCount + processingOrdersCount + shippedOrdersCount + returnedOrdersCount

        /**
         * Returns true if there are any pending orders.
         */
        val hasPendingOrders: Boolean
            get() = pendingOrdersCount > 0

        /**
         * Returns true if there are any processing orders.
         */
        val hasProcessingOrders: Boolean
            get() = processingOrdersCount > 0

        /**
         * Returns true if there are any shipped orders.
         */
        val hasShippedOrders: Boolean
            get() = shippedOrdersCount > 0

        /**
         * Returns true if there are any returned orders.
         */
        val hasReturnedOrders: Boolean
            get() = returnedOrdersCount > 0
    }

    /**
     * Error state when the profile fails to load.
     * Supports cached data display while showing error.
     *
     * Requirements: 9.2, 9.4
     */
    data class Error(
        val message: String,
        val cachedData: Success? = null,
        val canRetry: Boolean = true
    ) : ProfileUiState()
}

/**
 * Enum representing order status for navigation.
 */
enum class OrderStatus(val label: String, val apiValue: String) {
    PENDING("در انتظار", "pending"),
    PROCESSING("در حال پردازش", "processing"),
    SHIPPED("ارسال شده", "shipped"),
    RETURNED("مرجوعی", "returned")
}

/**
 * Events that can be triggered from the profile screen UI.
 */
sealed class ProfileEvent {

    /**
     * User triggered refresh/reload of profile.
     */
    data object Refresh : ProfileEvent()

    /**
     * User tapped retry on error state.
     */
    data object Retry : ProfileEvent()

    /**
     * User tapped edit profile button.
     * Requirements: 1.6
     */
    data object EditProfile : ProfileEvent()

    /**
     * User tapped edit account button.
     * Requirements: 2.3
     */
    data object EditAccount : ProfileEvent()

    /**
     * User tapped on wallet stat card.
     * Requirements: 3.7
     */
    data object WalletClicked : ProfileEvent()

    /**
     * User tapped on loyalty points stat card.
     * Requirements: 3.7
     */
    data object LoyaltyClicked : ProfileEvent()

    /**
     * User tapped on coupons stat card.
     * Requirements: 3.7
     */
    data object CouponsClicked : ProfileEvent()

    /**
     * User tapped on an order status.
     * Requirements: 4.8
     */
    data class OrderStatusClicked(val status: OrderStatus) : ProfileEvent()

    /**
     * User tapped "View All" orders.
     * Requirements: 4.9
     */
    data object ViewAllOrders : ProfileEvent()

    /**
     * User tapped on addresses menu item.
     * Requirements: 5.7
     */
    data object AddressesClicked : ProfileEvent()

    /**
     * User tapped on favorites menu item.
     * Requirements: 5.7
     */
    data object FavoritesClicked : ProfileEvent()

    /**
     * User tapped on recently viewed menu item.
     * Requirements: 5.7
     */
    data object RecentlyViewedClicked : ProfileEvent()

    /**
     * User tapped on settings menu item.
     * Requirements: 6.5
     */
    data object SettingsClicked : ProfileEvent()

    /**
     * User tapped on support menu item.
     * Requirements: 6.6
     */
    data object SupportClicked : ProfileEvent()

    /**
     * User tapped logout button.
     * Requirements: 7.3
     */
    data object LogoutClicked : ProfileEvent()

    /**
     * User confirmed logout in dialog.
     * Requirements: 7.4
     */
    data object LogoutConfirmed : ProfileEvent()

    /**
     * User dismissed logout dialog.
     */
    data object LogoutDismissed : ProfileEvent()

    /**
     * User tapped on bottom navigation item.
     * Requirements: 8.4
     */
    data class BottomNavClicked(val destination: BottomNavDestination) : ProfileEvent()
}

/**
 * Enum representing bottom navigation destinations.
 */
enum class BottomNavDestination {
    HOME,
    CATEGORIES,
    CART,
    PROFILE
}
