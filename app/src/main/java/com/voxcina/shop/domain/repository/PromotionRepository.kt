package com.voxcina.shop.domain.repository

import com.voxcina.shop.domain.model.Promotion
import com.voxcina.shop.util.Result

interface PromotionRepository {
    suspend fun getUserPromotions(): Result<List<Promotion>>
}
