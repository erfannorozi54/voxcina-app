package com.voxcina.shop.util

import android.content.Context
import android.content.SharedPreferences

/**
 * Interface for managing onboarding state.
 * Determines whether the user has completed the onboarding flow.
 */
interface OnboardingManager {
    /**
     * Checks if the user has completed the onboarding flow.
     * @return true if onboarding has been completed, false otherwise
     */
    fun isOnboardingCompleted(): Boolean

    /**
     * Marks the onboarding flow as completed.
     * This should be called when the user either skips or completes the onboarding.
     */
    fun setOnboardingCompleted()
}

/**
 * Implementation of [OnboardingManager] using SharedPreferences for persistence.
 * The onboarding completion flag is stored persistently and survives app restarts.
 *
 * @param context Application context used to access SharedPreferences
 */
class OnboardingManagerImpl(context: Context) : OnboardingManager {

    companion object {
        private const val PREFS_NAME = "voxcina_onboarding_prefs"
        private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
    }

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun isOnboardingCompleted(): Boolean {
        return sharedPreferences.getBoolean(KEY_ONBOARDING_COMPLETED, false)
    }

    override fun setOnboardingCompleted() {
        sharedPreferences.edit()
            .putBoolean(KEY_ONBOARDING_COMPLETED, true)
            .apply()
    }
}
