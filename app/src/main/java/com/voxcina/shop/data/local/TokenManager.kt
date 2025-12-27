package com.voxcina.shop.data.local

/**
 * Interface for managing authentication tokens.
 * Implementations should use secure storage (EncryptedSharedPreferences).
 */
interface TokenManager {
    
    /**
     * Saves access and refresh tokens securely.
     */
    fun saveTokens(accessToken: String, refreshToken: String)
    
    /**
     * Retrieves the current access token.
     * @return The access token or null if not stored.
     */
    fun getAccessToken(): String?
    
    /**
     * Retrieves the current refresh token.
     * @return The refresh token or null if not stored.
     */
    fun getRefreshToken(): String?
    
    /**
     * Clears all stored tokens (used for logout).
     */
    fun clearTokens()
    
    /**
     * Checks if the user is currently logged in (has valid tokens).
     * @return true if tokens are present, false otherwise.
     */
    fun isLoggedIn(): Boolean
}
