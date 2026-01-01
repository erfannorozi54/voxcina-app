package com.voxcina.shop.domain.repository

import com.voxcina.shop.domain.model.UserProfile
import com.voxcina.shop.util.Result

/**
 * Repository interface for profile operations.
 * Combines local cached data with server data.
 */
interface ProfileRepository {

    /**
     * Get user profile from server.
     * Falls back to cached data if available.
     * 
     * @return Result<UserProfile> containing profile data
     */
    suspend fun getProfile(): Result<UserProfile>

    /**
     * Get cached user profile from local storage.
     * Returns null if no cached data available.
     * 
     * @return UserProfile or null
     */
    fun getCachedProfile(): UserProfile?

    /**
     * Logout user by clearing all local data.
     * This is a client-side operation.
     */
    fun logout()
}
