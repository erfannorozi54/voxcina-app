package com.voxcina.shop.domain.repository

interface ActivityRepository {
    suspend fun trackProductView(productId: String, productName: String, colorHex: String)
}
