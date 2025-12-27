package com.voxcina.shop.data.local

import com.voxcina.shop.domain.model.RecentlyViewedProduct

/**
 * Interface for managing recently viewed products in local storage.
 * Provides methods to add, retrieve, and clear recently viewed products.
 */
interface RecentlyViewedDataSource {
    
    /**
     * Adds a product to the recently viewed list.
     * If the product already exists, it updates the timestamp and moves it to the front.
     * The list is limited to [MAX_ITEMS] items.
     * 
     * @param product The product to add to recently viewed
     */
    suspend fun addProduct(product: RecentlyViewedProduct)
    
    /**
     * Retrieves the most recently viewed products.
     * 
     * @param limit Maximum number of products to return (default: 5)
     * @return List of recently viewed products ordered by viewedAt timestamp (most recent first)
     */
    suspend fun getRecentProducts(limit: Int = 5): List<RecentlyViewedProduct>
    
    /**
     * Clears all recently viewed products from storage.
     */
    suspend fun clearAll()
    
    companion object {
        const val MAX_ITEMS = 5
    }
}
