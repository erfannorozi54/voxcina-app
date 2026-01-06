package com.voxcina.shop.data.local

import com.voxcina.shop.domain.model.RecentlyViewedProduct

/**
 * Interface for managing recently viewed products in local storage.
 * Stores up to 40 product color variants that user has viewed.
 */
interface RecentlyViewedDataSource {
    
    /**
     * Adds a product color variant to the recently viewed list.
     * If the same product+color already exists, updates timestamp and moves to front.
     */
    suspend fun addProduct(product: RecentlyViewedProduct)
    
    /**
     * Retrieves recently viewed products.
     * @param limit Maximum number of products to return (default: MAX_ITEMS)
     */
    suspend fun getRecentProducts(limit: Int = MAX_ITEMS): List<RecentlyViewedProduct>
    
    /**
     * Clears all recently viewed products.
     */
    suspend fun clearAll()
    
    companion object {
        const val MAX_ITEMS = 40
    }
}
