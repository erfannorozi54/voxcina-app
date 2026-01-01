package com.voxcina.shop.data.local

/**
 * Interface for managing authentication tokens and basic user info.
 * Implementations should use secure storage (EncryptedSharedPreferences).
 */
interface TokenManager {
    fun saveTokens(accessToken: String, refreshToken: String)
    fun getAccessToken(): String?
    fun getRefreshToken(): String?
    fun clearTokens()
    fun isLoggedIn(): Boolean
    
    // User info cached from login
    fun saveUserName(name: String)
    fun getUserName(): String?
    fun saveUserPhone(phone: String)
    fun getUserPhone(): String?
    fun saveUserId(id: String)
    fun getUserId(): String?
    
    /**
     * Clears all user data (tokens + cached user info).
     * Called on logout.
     */
    fun clearAll()
}
