package com.voxcina.shop.data.repository

import com.google.gson.Gson
import com.voxcina.shop.data.local.TokenManager
import com.voxcina.shop.data.remote.ProfileApi
import com.voxcina.shop.data.remote.dto.ApiErrorResponse
import com.voxcina.shop.domain.model.UserProfile
import com.voxcina.shop.domain.model.toDomain
import com.voxcina.shop.domain.repository.ProfileRepository
import com.voxcina.shop.util.AppError
import com.voxcina.shop.util.ProfileError
import com.voxcina.shop.util.Result
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of ProfileRepository.
 * Handles API calls and combines with local cached data.
 */
@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val profileApi: ProfileApi,
    private val tokenManager: TokenManager,
    private val gson: Gson
) : ProfileRepository {

    override suspend fun getProfile(): Result<UserProfile> {
        return safeApiCall {
            val response = profileApi.getProfile()
            handleResponse(response) { dto ->
                val profile = dto.toDomain()
                // Update cached user info
                tokenManager.saveUserName(profile.name)
                tokenManager.saveUserPhone(profile.phone)
                tokenManager.saveUserId(profile.id)
                profile
            }
        }
    }

    override fun getCachedProfile(): UserProfile? {
        val id = tokenManager.getUserId()
        val name = tokenManager.getUserName()
        val phone = tokenManager.getUserPhone()
        
        return if (name != null && phone != null) {
            UserProfile(
                id = id ?: "",
                name = name,
                phone = phone,
                email = null,
                avatarUrl = null,
                hasAddresses = false,
                addresses = emptyList()
            )
        } else null
    }

    override fun logout() {
        tokenManager.clearAll()
    }

    /**
     * Wraps API calls with error handling for network exceptions.
     */
    private inline fun <T> safeApiCall(apiCall: () -> Result<T>): Result<T> {
        return try {
            apiCall()
        } catch (e: IOException) {
            Result.Error(AppError.NetworkError())
        } catch (e: Exception) {
            Result.Error(AppError.UnknownError(e.message ?: "خطای ناشناخته"))
        }
    }

    /**
     * Handles successful responses or maps errors.
     */
    private fun <T, R> handleResponse(
        response: Response<T>,
        transform: (T) -> R
    ): Result<R> {
        return if (response.isSuccessful) {
            response.body()?.let {
                Result.Success(transform(it))
            } ?: Result.Error(AppError.UnknownError("پاسخ خالی از سرور"))
        } else {
            Result.Error(mapHttpError(response))
        }
    }

    /**
     * Maps HTTP error codes to domain-specific error types.
     */
    private fun <T> mapHttpError(response: Response<T>): AppError {
        val errorBody = response.errorBody()?.string()
        val apiError = parseErrorBody(errorBody)
        
        return when (response.code()) {
            401 -> ProfileError.NotAuthenticated
            404 -> ProfileError.ProfileNotFound
            in 500..599 -> AppError.ServerError(
                code = response.code(),
                message = apiError?.message ?: "خطای سرور"
            )
            else -> AppError.UnknownError(
                apiError?.message ?: "خطای ناشناخته"
            )
        }
    }

    /**
     * Parses error response body to ApiErrorResponse.
     */
    private fun parseErrorBody(errorBody: String?): ApiErrorResponse? {
        if (errorBody.isNullOrBlank()) return null
        return try {
            gson.fromJson(errorBody, ApiErrorResponse::class.java)
        } catch (e: Exception) {
            null
        }
    }
}
