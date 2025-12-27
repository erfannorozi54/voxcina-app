package com.voxcina.shop.data.local

interface TokenManager {
    fun saveTokens(accessToken: String, refreshToken: String)
    fun getAccessToken(): String?
    fun getRefreshToken(): String?
    fun clearTokens()
    fun isLoggedIn(): Boolean
    fun saveUserName(name: String)
    fun getUserName(): String?
}
