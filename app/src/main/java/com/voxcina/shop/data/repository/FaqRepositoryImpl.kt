package com.voxcina.shop.data.repository

import com.voxcina.shop.data.remote.FaqApi
import com.voxcina.shop.data.remote.dto.toDomain
import com.voxcina.shop.domain.model.Faq
import com.voxcina.shop.domain.repository.FaqRepository
import com.voxcina.shop.util.FaqError
import com.voxcina.shop.util.Result
import javax.inject.Inject

class FaqRepositoryImpl @Inject constructor(
    private val faqApi: FaqApi
) : FaqRepository {
    
    override suspend fun getFaqs(): Result<List<Faq>> {
        return try {
            val faqs = faqApi.getFaqs().map { it.toDomain() }.sortedBy { it.order }
            Result.Success(faqs)
        } catch (e: Exception) {
            Result.Error(FaqError.FaqLoadFailed)
        }
    }
}
