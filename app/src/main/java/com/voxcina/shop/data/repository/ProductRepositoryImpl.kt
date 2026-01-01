package com.voxcina.shop.data.repository

import com.google.gson.Gson
import com.voxcina.shop.data.remote.ProductApi
import com.voxcina.shop.data.remote.dto.AddReviewRequest
import com.voxcina.shop.data.remote.dto.ApiErrorResponse
import com.voxcina.shop.data.remote.dto.toDomain
import com.voxcina.shop.domain.model.ProductDetail
import com.voxcina.shop.domain.model.ProductReview
import com.voxcina.shop.domain.model.toDomain
import com.voxcina.shop.domain.repository.ProductRepository
import com.voxcina.shop.util.AppError
import com.voxcina.shop.util.ProductError
import com.voxcina.shop.util.ReviewError
import com.voxcina.shop.util.Result
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of ProductRepository.
 * Handles API calls and maps responses/errors to domain types.
 */
@Singleton
class ProductRepositoryImpl @Inject constructor(
    private val productApi: ProductApi,
    private val gson: Gson
) : ProductRepository {

    override suspend fun getProductDetail(productId: String): Result<ProductDetail> {
        return safeApiCall(ProductError.ProductLoadFailed) {
            val response = productApi.getProductDetail(productId)
            if (response.isSuccessful) {
                val productDetail = response.body()?.toDomain()
                if (productDetail != null) {
                    Result.Success(productDetail)
                } else {
                    Result.Error(ProductError.ProductLoadFailed)
                }
            } else {
                Result.Error(mapHttpError(response))
            }
        }
    }

    override suspend fun getProductReviews(productId: String): Result<List<ProductReview>> {
        return safeApiCall(ReviewError.ReviewLoadFailed) {
            val response = productApi.getProductReviews(productId)
            if (response.isSuccessful) {
                val reviews = response.body()?.map { it.toDomain() } ?: emptyList()
                Result.Success(reviews)
            } else {
                Result.Error(mapHttpError(response))
            }
        }
    }

    override suspend fun addReview(
        productId: String,
        rating: Int,
        comment: String,
        isRecommended: Boolean
    ): Result<ProductReview> {
        return safeApiCall(ReviewError.ReviewSubmitFailed) {
            val request = AddReviewRequest(rating, comment, isRecommended)
            val response = productApi.addReview(productId, request)
            if (response.isSuccessful) {
                val review = response.body()?.toDomain()
                if (review != null) {
                    Result.Success(review)
                } else {
                    Result.Error(ReviewError.ReviewSubmitFailed)
                }
            } else {
                Result.Error(mapReviewHttpError(response))
            }
        }
    }

    /**
     * Wraps API calls with error handling for network exceptions.
     */
    private inline fun <T> safeApiCall(
        fallbackError: AppError,
        apiCall: () -> Result<T>
    ): Result<T> {
        return try {
            apiCall()
        } catch (e: IOException) {
            Result.Error(AppError.NetworkError())
        } catch (e: Exception) {
            Result.Error(fallbackError)
        }
    }

    /**
     * Maps HTTP error codes to domain-specific error types.
     */
    private fun <T> mapHttpError(response: Response<T>): AppError {
        val errorBody = response.errorBody()?.string()
        val apiError = parseErrorBody(errorBody)

        return when (response.code()) {
            400 -> {
                val message = apiError?.message?.lowercase() ?: apiError?.error?.lowercase() ?: ""
                if (message.contains("invalid") && message.contains("id")) {
                    ProductError.InvalidProductId
                } else {
                    ProductError.ProductLoadFailed
                }
            }
            404 -> ProductError.ProductNotFound
            in 500..599 -> AppError.ServerError(
                code = response.code(),
                message = apiError?.message ?: apiError?.error ?: "خطای سرور"
            )
            else -> AppError.UnknownError(
                apiError?.message ?: apiError?.error ?: "خطای ناشناخته"
            )
        }
    }

    /**
     * Maps HTTP error codes for review operations.
     */
    private fun <T> mapReviewHttpError(response: Response<T>): AppError {
        val errorBody = response.errorBody()?.string()
        val apiError = parseErrorBody(errorBody)

        return when (response.code()) {
            400 -> {
                val message = apiError?.message?.lowercase() ?: apiError?.error?.lowercase() ?: ""
                when {
                    message.contains("rating") -> ReviewError.InvalidRating
                    else -> ReviewError.ReviewSubmitFailed
                }
            }
            401 -> ReviewError.NotAuthenticated
            404 -> ProductError.ProductNotFound
            in 500..599 -> AppError.ServerError(
                code = response.code(),
                message = apiError?.message ?: apiError?.error ?: "خطای سرور"
            )
            else -> AppError.UnknownError(
                apiError?.message ?: apiError?.error ?: "خطای ناشناخته"
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
