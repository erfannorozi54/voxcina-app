package com.voxcina.shop.data.repository

import com.voxcina.shop.data.remote.ProfileApi
import com.voxcina.shop.domain.model.Promotion
import com.voxcina.shop.domain.repository.PromotionRepository
import com.voxcina.shop.util.AppError
import com.voxcina.shop.util.Result
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PromotionRepositoryImpl @Inject constructor(
    private val profileApi: ProfileApi
) : PromotionRepository {

    override suspend fun getUserPromotions(): Result<List<Promotion>> {
        return try {
            val response = profileApi.getUserPromotions()
            if (response.isSuccessful) {
                val dtos = response.body() ?: emptyList()
                val promotions = dtos.map { dto ->
                    Promotion(
                        id = dto.id,
                        code = dto.code,
                        type = dto.type,
                        value = dto.value,
                        minOrderAmount = dto.minOrderAmount,
                        validFrom = dto.validFrom,
                        validTo = dto.validTo,
                        maxUses = dto.maxUses,
                        usedCount = dto.usedCount,
                        isPublic = dto.isPublic
                    )
                }
                Result.Success(promotions)
            } else {
                Result.Error(AppError.ServerError(response.code(), "خطا در دریافت تخفیف‌ها"))
            }
        } catch (e: IOException) {
            Result.Error(AppError.NetworkError())
        } catch (e: Exception) {
            Result.Error(AppError.UnknownError(e.message ?: "خطای ناشناخته"))
        }
    }
}
