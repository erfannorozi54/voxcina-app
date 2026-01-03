package com.voxcina.shop.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.voxcina.shop.domain.model.Faq

data class FaqDto(
    @SerializedName("id") val id: String?,
    @SerializedName("question") val question: String?,
    @SerializedName("answer") val answer: String?,
    @SerializedName("category") val category: String?,
    @SerializedName("order") val order: Int?
)

fun FaqDto.toDomain(): Faq = Faq(
    id = id.orEmpty(),
    question = question.orEmpty(),
    answer = answer.orEmpty(),
    category = category,
    order = order ?: 0
)
