package com.voxcina.shop.data.remote

import com.voxcina.shop.data.local.TokenManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * OkHttp interceptor that adds authentication headers to requests.
 * Automatically injects the Bearer token from TokenManager.
 */
@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {
    
    companion object {
        private const val HEADER_AUTHORIZATION = "Authorization"
        private const val BEARER_PREFIX = "Bearer "
    }
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // Skip auth header for public endpoints
        if (isPublicEndpoint(originalRequest.url.encodedPath)) {
            return chain.proceed(originalRequest)
        }
        
        val accessToken = tokenManager.getAccessToken()
        
        // If no token available, proceed without auth header
        if (accessToken.isNullOrBlank()) {
            return chain.proceed(originalRequest)
        }
        
        // Add authorization header
        val authenticatedRequest = originalRequest.newBuilder()
            .header(HEADER_AUTHORIZATION, "$BEARER_PREFIX$accessToken")
            .build()
        
        return chain.proceed(authenticatedRequest)
    }
    
    /**
     * Determines if the endpoint is public and doesn't require authentication.
     */
    private fun isPublicEndpoint(path: String): Boolean {
        val publicPaths = listOf(
            "/auth/login",
            "/auth/signup",
            "/auth/check-phone",
            "/auth/send-otp",
            "/auth/verify-otp",
            "/auth/forgot-password",
            "/auth/refresh-token"
        )
        return publicPaths.any { path.contains(it, ignoreCase = true) }
    }
}
