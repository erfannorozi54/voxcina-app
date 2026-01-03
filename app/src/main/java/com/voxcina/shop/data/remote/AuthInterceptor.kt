package com.voxcina.shop.data.remote

import com.voxcina.shop.data.local.SessionManager
import com.voxcina.shop.data.local.TokenManager
import com.voxcina.shop.data.remote.dto.RefreshTokenRequest
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * OkHttp interceptor that adds authentication headers and handles token refresh.
 */
@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager,
    private val sessionManager: SessionManager
) : Interceptor {
    
    private val refreshMutex = Mutex()
    
    @Volatile
    private var authApi: AuthApi? = null
    
    fun setAuthApi(api: AuthApi) {
        authApi = api
    }
    
    companion object {
        private const val HEADER_AUTHORIZATION = "Authorization"
        private const val BEARER_PREFIX = "Bearer "
    }
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        if (isPublicEndpoint(originalRequest.url.encodedPath)) {
            return chain.proceed(originalRequest)
        }
        
        val accessToken = tokenManager.getAccessToken()
        if (accessToken.isNullOrBlank()) {
            return chain.proceed(originalRequest)
        }
        
        val authenticatedRequest = originalRequest.newBuilder()
            .header(HEADER_AUTHORIZATION, "$BEARER_PREFIX$accessToken")
            .build()
        
        val response = chain.proceed(authenticatedRequest)
        
        // Handle 401 - try to refresh token
        if (response.code == 401 && !isRefreshEndpoint(originalRequest.url.encodedPath)) {
            response.close()
            
            val newToken = runBlocking { tryRefreshToken() }
            
            if (newToken != null) {
                // Retry with new token
                val retryRequest = originalRequest.newBuilder()
                    .header(HEADER_AUTHORIZATION, "$BEARER_PREFIX$newToken")
                    .build()
                return chain.proceed(retryRequest)
            } else {
                // Refresh failed - session expired
                sessionManager.onSessionExpired()
            }
        }
        
        return response
    }
    
    private suspend fun tryRefreshToken(): String? {
        return refreshMutex.withLock {
            val refreshToken = tokenManager.getRefreshToken() ?: return@withLock null
            val api = authApi ?: return@withLock null
            
            try {
                val response = api.refreshToken(RefreshTokenRequest(refreshToken))
                if (response.isSuccessful) {
                    val newToken = response.body()?.accessToken
                    if (newToken != null) {
                        tokenManager.saveTokens(newToken, refreshToken)
                        return@withLock newToken
                    }
                }
            } catch (_: Exception) {
                // Refresh failed
            }
            null
        }
    }
    
    private fun isPublicEndpoint(path: String): Boolean {
        val publicPaths = listOf(
            "/auth/login",
            "/auth/signup",
            "/auth/check-phone",
            "/auth/send-otp",
            "/auth/verify-otp",
            "/auth/forgot-password",
            "/users/login",
            "/users/check-phone",
            "/users/register"
        )
        return publicPaths.any { path.contains(it, ignoreCase = true) }
    }
    
    private fun isRefreshEndpoint(path: String): Boolean {
        return path.contains("/users/refresh", ignoreCase = true)
    }
}
