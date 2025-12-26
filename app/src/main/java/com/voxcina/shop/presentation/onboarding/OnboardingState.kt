package com.voxcina.shop.presentation.onboarding

/**
 * UI state for the onboarding screen.
 *
 * Holds the list of onboarding pages, current page position, and app version
 * for display at the bottom of the screen.
 *
 * @property pages List of onboarding pages to display
 * @property currentPageIndex Index of the currently visible page (0-based)
 * @property appVersion App version string to display (e.g., "1.0.0")
 */
data class OnboardingState(
    val pages: List<OnboardingPage>,
    val currentPageIndex: Int = 0,
    val appVersion: String
)
