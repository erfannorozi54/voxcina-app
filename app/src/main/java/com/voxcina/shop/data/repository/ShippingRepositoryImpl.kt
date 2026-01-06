package com.voxcina.shop.data.repository

import com.voxcina.shop.data.remote.ShippingApi
import com.voxcina.shop.data.remote.dto.ShippingQuotesRequestDto
import com.voxcina.shop.data.remote.dto.toDomain
import com.voxcina.shop.domain.model.ShippingMethod
import com.voxcina.shop.domain.repository.ShippingRepository
import com.voxcina.shop.util.AppError
import com.voxcina.shop.util.Result
import com.voxcina.shop.util.ShippingError
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of ShippingRepository.
 * Handles API calls to Postex shipping endpoints and maps responses to domain models.
 */
@Singleton
class ShippingRepositoryImpl @Inject constructor(
    private val shippingApi: ShippingApi
) : ShippingRepository {

    override suspend fun getShippingQuotes(
        cityCode: Int,
        itemCount: Int,
        totalValue: Long
    ): Result<List<ShippingMethod>> = safeApiCall {
        val request = ShippingQuotesRequestDto(
            toCityCode = cityCode,
            itemCount = itemCount,
            totalValue = totalValue
        )
        
        val response = shippingApi.getShippingQuotes(request)
        
        if (response.isSuccessful) {
            val methods = response.body()?.methods?.map { it.toDomain() } ?: emptyList()
            if (methods.isEmpty()) {
                Result.Error(ShippingError.NoShippingMethodsAvailable)
            } else {
                Result.Success(methods)
            }
        } else {
            Result.Error(mapError(response.code()))
        }
    }

    /**
     * Maps HTTP error codes to shipping-specific error types.
     */
    private fun mapError(code: Int): AppError = when (code) {
        400 -> ShippingError.InvalidCityCode
        502 -> ShippingError.ShippingQuotesLoadFailed
        503 -> AppError.NetworkError("سرویس ارسال در دسترس نیست")
        in 500..599 -> AppError.ServerError(code, "خطای سرور")
        else -> ShippingError.ShippingQuotesLoadFailed
    }

    /**
     * Wraps API calls with error handling for network exceptions.
     */
    private inline fun <T> safeApiCall(call: () -> Result<T>): Result<T> = try {
        call()
    } catch (e: IOException) {
        Result.Error(AppError.NetworkError())
    } catch (e: Exception) {
        Result.Error(AppError.UnknownError(e.message ?: "خطای ناشناخته"))
    }
}
