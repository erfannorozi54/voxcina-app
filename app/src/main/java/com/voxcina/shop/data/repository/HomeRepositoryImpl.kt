package com.voxcina.shop.data.repository

import com.google.gson.Gson
import com.voxcina.shop.data.remote.HomeApi
import com.voxcina.shop.data.remote.dto.ApiErrorResponse
import com.voxcina.shop.domain.model.*
import com.voxcina.shop.util.AppError
import com.voxcina.shop.util.HomeError
import com.voxcina.shop.util.Result
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of HomeRepository.
 * Handles API calls and maps responses/errors to domain types.
 */
@Singleton
class HomeRepositoryImpl @Inject constructor(
    private val homeApi: HomeApi,
    private val gson: Gson
) : HomeRepository {

    override suspend fun getHeroImages(): Result<List<HeroImage>> {
        return safeApiCall(HomeError.HeroImagesLoadFailed) {
            // First try mobile device type
            var response = homeApi.getHeroImages(device = "mobile")
            if (response.isSuccessful) {
                val mobileImages = response.body()?.heroImages
                    ?.filter { it.isActive }
                    ?.sortedBy { it.displayOrder }
                    ?.toHeroImages()
                    ?: emptyList()
                
                // If mobile images are empty, fallback to desktop images
                if (mobileImages.isEmpty()) {
                    response = homeApi.getHeroImages(device = "desktop")
                    if (response.isSuccessful) {
                        val desktopImages = response.body()?.heroImages
                            ?.filter { it.isActive }
                            ?.sortedBy { it.displayOrder }
                            ?.toHeroImages()
                            ?: emptyList()
                        return@safeApiCall Result.Success(desktopImages)
                    }
                }
                
                Result.Success(mobileImages)
            } else {
                Result.Error(mapHttpError(response, HomeError.HeroImagesLoadFailed))
            }
        }
    }

    override suspend fun getCategories(): Result<List<Category>> {
        return safeApiCall(HomeError.CategoriesLoadFailed) {
            val response = homeApi.getCategories()
            if (response.isSuccessful) {
                val categories = response.body()
                    ?.filter { it.isActive }
                    ?.toCategories()
                    ?: emptyList()
                Result.Success(categories)
            } else {
                Result.Error(mapHttpError(response, HomeError.CategoriesLoadFailed))
            }
        }
    }

    override suspend fun getProducts(page: Int, limit: Int): Result<ProductListResult> {
        return safeApiCall(HomeError.ProductsLoadFailed) {
            val response = homeApi.getProducts(page = page, limit = limit)
            if (response.isSuccessful) {
                val result = response.body()?.toDomain()
                    ?: ProductListResult(
                        products = emptyList(),
                        totalPages = 0,
                        currentPage = page,
                        nextPage = null,
                        totalItems = 0
                    )
                Result.Success(result)
            } else {
                Result.Error(mapHttpError(response, HomeError.ProductsLoadFailed))
            }
        }
    }

    override suspend fun getFlashSaleProducts(): Result<List<Product>> {
        return safeApiCall(HomeError.FlashSaleLoadFailed) {
            val response = homeApi.getProducts(isFlashSale = true, limit = 10)
            if (response.isSuccessful) {
                val products = response.body()?.data?.map { it.toDomain() } ?: emptyList()
                Result.Success(products)
            } else {
                Result.Error(mapHttpError(response, HomeError.FlashSaleLoadFailed))
            }
        }
    }

    override suspend fun getBrands(): Result<List<Brand>> {
        return safeApiCall(HomeError.BrandsLoadFailed) {
            val response = homeApi.getBrands()
            if (response.isSuccessful) {
                val brands = response.body()?.toBrands() ?: emptyList()
                Result.Success(brands)
            } else {
                Result.Error(mapHttpError(response, HomeError.BrandsLoadFailed))
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
    private fun <T> mapHttpError(response: Response<T>, fallbackError: AppError): AppError {
        val errorBody = response.errorBody()?.string()
        val apiError = parseErrorBody(errorBody)

        return when (response.code()) {
            in 400..499 -> fallbackError
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
