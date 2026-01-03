package com.voxcina.shop.domain.model

data class Faq(
    val id: String,
    val question: String,
    val answer: String,
    val category: String? = null,
    val order: Int = 0
)
