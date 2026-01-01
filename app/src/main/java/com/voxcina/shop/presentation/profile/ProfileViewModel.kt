package com.voxcina.shop.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.voxcina.shop.data.local.TokenManager
import com.voxcina.shop.domain.model.OrderCounts
import com.voxcina.shop.domain.repository.CartRepository
import com.voxcina.shop.domain.repository.ProfileRepository
import com.voxcina.shop.util.AppError
import com.voxcina.shop.util.ProfileError
import com.voxcina.shop.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Profile Screen.
 * Manages profile data loading, logout, and UI state.
 *
 * Requirements: 7.4, 9.3
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val cartRepository: CartRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _showLogoutDialog = MutableStateFlow(false)
    val showLogoutDialog: StateFlow<Boolean> = _showLogoutDialog.asStateFlow()

    init {
        loadProfile()
    }

    /**
     * Handles UI events from the profile screen.
     */
    fun onEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.Refresh -> loadProfile()
            is ProfileEvent.Retry -> loadProfile()
            is ProfileEvent.LogoutClicked -> _showLogoutDialog.value = true
            is ProfileEvent.LogoutConfirmed -> logout()
            is ProfileEvent.LogoutDismissed -> _showLogoutDialog.value = false
            else -> { /* Navigation events handled by UI */ }
        }
    }

    /**
     * Loads user profile data from repository.
     * Falls back to cached data if available on error.
     *
     * Requirements: 9.1, 9.2, 9.3, 9.4
     */
    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading

            // Try to get cached profile first for faster display
            val cachedProfile = profileRepository.getCachedProfile()

            // Fetch fresh profile from server
            val profileResult = profileRepository.getProfile()
            val cartResult = cartRepository.getCart()

            val cartItemCount = (cartResult as? Result.Success)?.data?.items?.sumOf { it.quantity } ?: 0

            when (profileResult) {
                is Result.Success -> {
                    val profile = profileResult.data
                    _uiState.value = ProfileUiState.Success(
                        userName = profile.name,
                        phoneNumber = profile.phone,
                        avatarUrl = profile.avatarUrl,
                        walletBalance = 0L, // TODO: Fetch from wallet API when available
                        loyaltyPoints = 0, // TODO: Fetch from loyalty API when available
                        activeCoupons = 0, // TODO: Fetch from coupons API when available
                        pendingOrdersCount = 0, // TODO: Fetch from orders API when available
                        processingOrdersCount = 0,
                        shippedOrdersCount = 0,
                        returnedOrdersCount = 0,
                        cartItemCount = cartItemCount
                    )
                }
                is Result.Error -> {
                    val errorMessage = mapErrorToMessage(profileResult.error)
                    
                    // If we have cached data, show it with error banner
                    val cachedSuccess = cachedProfile?.let {
                        ProfileUiState.Success(
                            userName = it.name,
                            phoneNumber = it.phone,
                            avatarUrl = it.avatarUrl,
                            walletBalance = 0L,
                            loyaltyPoints = 0,
                            activeCoupons = 0,
                            pendingOrdersCount = 0,
                            processingOrdersCount = 0,
                            shippedOrdersCount = 0,
                            returnedOrdersCount = 0,
                            cartItemCount = cartItemCount
                        )
                    }
                    
                    _uiState.value = ProfileUiState.Error(
                        message = errorMessage,
                        cachedData = cachedSuccess,
                        canRetry = true
                    )
                }
            }
        }
    }

    /**
     * Logs out the user by clearing session and local data.
     *
     * Requirements: 7.4
     */
    fun logout() {
        viewModelScope.launch {
            // Update UI to show logging out state
            _uiState.update { state ->
                if (state is ProfileUiState.Success) {
                    state.copy(isLoggingOut = true)
                } else state
            }

            // Clear all local data (tokens, cached user info)
            profileRepository.logout()

            // Hide dialog
            _showLogoutDialog.value = false

            // Note: Navigation to auth screen is handled by the UI layer
            // by observing the logout completion
        }
    }

    /**
     * Checks if user is logged in.
     * Used by UI to determine if logout completed.
     */
    fun isLoggedIn(): Boolean = tokenManager.isLoggedIn()

    /**
     * Maps AppError to user-friendly Persian error messages.
     */
    private fun mapErrorToMessage(error: AppError): String {
        return when (error) {
            is AppError.NetworkError -> "خطا در اتصال به سرور"
            is ProfileError.ProfileNotFound -> "پروفایل یافت نشد"
            is ProfileError.ProfileLoadFailed -> "خطا در بارگذاری پروفایل"
            is ProfileError.NotAuthenticated -> "برای مشاهده پروفایل باید وارد شوید"
            is ProfileError.LogoutFailed -> "خطا در خروج از حساب کاربری"
            is AppError.ServerError -> error.message
            is AppError.UnknownError -> "خطای ناشناخته"
            else -> error.message
        }
    }
}
