package com.voxcina.shop.data.local

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages session state and broadcasts logout events.
 */
@Singleton
class SessionManager @Inject constructor(
    private val tokenManager: TokenManager
) {
    private val _logoutEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val logoutEvent: SharedFlow<Unit> = _logoutEvent.asSharedFlow()

    /**
     * Called when token refresh fails. Clears all user data and emits logout event.
     */
    fun onSessionExpired() {
        tokenManager.clearAll()
        _logoutEvent.tryEmit(Unit)
    }
}
