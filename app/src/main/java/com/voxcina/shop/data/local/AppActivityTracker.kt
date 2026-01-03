package com.voxcina.shop.data.local

import android.content.Context
import com.voxcina.shop.BuildConfig
import com.voxcina.shop.data.remote.ProfileApi
import com.voxcina.shop.data.remote.dto.AppActivityRequestDto
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Tracks app activity for analytics.
 * Handles debouncing (1 hour) and fails silently.
 */
@Singleton
class AppActivityTracker @Inject constructor(
    @ApplicationContext private val context: Context,
    private val profileApi: ProfileApi,
    private val tokenManager: TokenManager
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val prefs = context.getSharedPreferences("app_activity", Context.MODE_PRIVATE)
    
    companion object {
        private const val KEY_LAST_TRACKED = "last_tracked_time"
        private const val DEBOUNCE_MILLIS = 60 * 60 * 1000L // 1 hour
    }

    /**
     * Track app open. Respects debounce unless bypassDebounce is true.
     * Call with bypassDebounce=true after login/register.
     */
    fun trackAppOpen(bypassDebounce: Boolean = false) {
        if (!tokenManager.isLoggedIn()) return
        
        if (!bypassDebounce && !shouldTrack()) return
        
        scope.launch {
            try {
                profileApi.recordAppActivity(
                    AppActivityRequestDto(appVersion = BuildConfig.VERSION_NAME)
                )
                prefs.edit().putLong(KEY_LAST_TRACKED, System.currentTimeMillis()).apply()
            } catch (_: Exception) {
                // Fail silently - this is background tracking
            }
        }
    }

    private fun shouldTrack(): Boolean {
        val lastTracked = prefs.getLong(KEY_LAST_TRACKED, 0)
        return System.currentTimeMillis() - lastTracked >= DEBOUNCE_MILLIS
    }
}
