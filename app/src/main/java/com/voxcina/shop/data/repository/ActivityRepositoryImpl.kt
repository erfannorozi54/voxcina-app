package com.voxcina.shop.data.repository

import com.voxcina.shop.data.remote.ActivityApi
import com.voxcina.shop.data.remote.dto.TrackActivityRequestDto
import com.voxcina.shop.domain.repository.ActivityRepository
import javax.inject.Inject

class ActivityRepositoryImpl @Inject constructor(
    private val activityApi: ActivityApi
) : ActivityRepository {
    
    override suspend fun trackProductView(productId: String, productName: String, colorHex: String) {
        try {
            activityApi.trackActivity(
                TrackActivityRequestDto(
                    activityType = "product_view",
                    productId = productId,
                    productName = productName,
                    metadata = mapOf("colorHex" to colorHex)
                )
            )
        } catch (_: Exception) {
            // Silently fail - activity tracking should not block user experience
        }
    }
}
