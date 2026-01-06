package com.voxcina.shop.domain.repository

import com.voxcina.shop.domain.model.ShippingMethod
import com.voxcina.shop.util.Result

/**
 * Repository interface for shipping operations.
 * Abstracts the data layer from the domain layer.
 */
interface ShippingRepository {

    /**
     * Get available shipping methods and quotes for a destination city.
     *
     * @param cityCode The destination city code from Postex locality API
     * @param itemCount Number of items in the order
     * @param totalValue Total value of the order in Rials
     * @return Result containing list of available shipping methods with prices
     */
    suspend fun getShippingQuotes(
        cityCode: Int,
        itemCount: Int,
        totalValue: Long
    ): Result<List<ShippingMethod>>
}
