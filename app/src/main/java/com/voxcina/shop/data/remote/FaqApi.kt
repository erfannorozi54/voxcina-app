package com.voxcina.shop.data.remote

import com.voxcina.shop.data.remote.dto.FaqDto
import retrofit2.http.GET

interface FaqApi {
    @GET("faqs")
    suspend fun getFaqs(): List<FaqDto>
}
