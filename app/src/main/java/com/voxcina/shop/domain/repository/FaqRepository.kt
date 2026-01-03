package com.voxcina.shop.domain.repository

import com.voxcina.shop.domain.model.Faq
import com.voxcina.shop.util.Result

interface FaqRepository {
    suspend fun getFaqs(): Result<List<Faq>>
}
