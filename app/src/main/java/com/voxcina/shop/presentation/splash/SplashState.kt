package com.voxcina.shop.presentation.splash

/**
 * Represents the different states of the splash screen.
 * 
 * State transitions follow a strict order: Playing → Loading → Complete
 * - Playing: Video is currently playing
 * - Loading: Video ended, showing last frame with progress indicator
 * - Complete: Ready to navigate to main screen
 */
sealed class SplashState {
    /** Video is currently playing */
    data object Playing : SplashState()
    
    /** Video ended, showing last frame with progress indicator */
    data object Loading : SplashState()
    
    /** Ready to navigate to main screen */
    data object Complete : SplashState()
}
