package com.voxcina.shop.data.repository

import com.google.gson.Gson
import com.voxcina.shop.data.remote.HomeApi
import com.voxcina.shop.data.remote.dto.ApiErrorResponse
import com.voxcina.shop.domain.model.*
import com.voxcina.shop.domain.repository.ProductsListRepository
import com.voxcina.shop.util.AppError
import com.voxcina.shop.util.HomeError
import com.voxcina.shop.util.Result
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductsListRepositoryImpl @Inject constructor(
    private val homeApi: HomeApi,
    private val gson: Gson
) : ProductsListRepository {

    override suspend fun getProducts(
        page: Int,
        limit: Int,
        sort: String?,
        categoryId: String?,
        brandId: String?,
        search: String?,
        isFlashSale: Boolean?,
        inStock: Boolean?
    ): Result<ProductListResult> {
        return safeApiCall(HomeError.ProductsLoadFailed) {
            val response = homeApi.getProducts(
                page = page,
                limit = limit,
                sort = sort,
                category = categoryId,
                brandId = brandId,
                search = search,
                isFlashSale = isFlashSale,
                inStock = inStock
            )
            if (response.isSuccessful) {
                val result = response.body()?.toDomain()
                    ?: ProductListResult(emptyList(), 0, page, null, 0)
                Result.Success(result)
            } else {
                Result.Error(mapHttpError(response, HomeError.ProductsLoadFailed))
            }
        }
    }

    override suspend fun getCategories(): Result<List<Category>> {
        return safeApiCall(HomeError.CategoriesLoadFailed) {
            val response = homeApi.getCategories()
            if (response.isSuccessful) {
                val categories = response.body()?.filter { it.isActive }?.toCategories() ?: emptyList()
                Result.Success(categories)
            } else {
                Result.Error(mapHttpError(response, HomeError.CategoriesLoadFailed))
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

    private fun <T> mapHttpError(response: Response<T>, fallbackError: AppError): AppError {
        val errorBody = response.errorBody()?.string()
        val apiError = parseErrorBody(errorBody)
        return when (response.code()) {
            in 400..499 -> fallbackError
            in 500..599 -> AppError.ServerError(response.code(), apiError?.message ?: "خطای سرور")
            else -> AppError.UnknownError(apiError?.message ?: "خطای ناشناخته")
        }
    }

    private fun parseErrorBody(errorBody: String?): ApiErrorResponse? {
        if (errorBody.isNullOrBlank()) return null
        return try {
            gson.fromJson(errorBody, ApiErrorResponse::class.java)
        } catch (e: Exception) {
            null
        }
    }
}
