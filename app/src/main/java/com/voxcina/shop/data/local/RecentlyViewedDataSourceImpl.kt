package com.voxcina.shop.data.local

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.voxcina.shop.domain.model.RecentlyViewedProduct
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [RecentlyViewedDataSource] using SharedPreferences with Gson serialization.
 * Stores recently viewed products with timestamp-based ordering, limited to [MAX_ITEMS] items.
 */
@Singleton
class RecentlyViewedDataSourceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson
) : RecentlyViewedDataSource {
    
    companion object {
        private const val PREFS_FILE_NAME = "voxcina_recently_viewed"
        private const val KEY_RECENTLY_VIEWED = "recently_viewed_products"
        private const val MAX_ITEMS = 5
    }
    
    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE)
    }
    
    override suspend fun addProduct(product: RecentlyViewedProduct) = withContext(Dispatchers.IO) {
        val current = getRecentProductsInternal().toMutableList()
        
        // Remove if already exists (will be re-added at front with new timestamp)
        current.removeAll { it.productId == product.productId }
        
        // Add to front with current timestamp
        val updatedProduct = product.copy(viewedAt = System.currentTimeMillis())
        current.add(0, updatedProduct)
        
        // Keep only MAX_ITEMS
        val trimmed = current.take(MAX_ITEMS)
        
        // Save to SharedPreferences
        saveProducts(trimmed)
    }
    
    override suspend fun getRecentProducts(limit: Int): List<RecentlyViewedProduct> = 
        withContext(Dispatchers.IO) {
            getRecentProductsInternal().take(limit)
        }
    
    override suspend fun clearAll() = withContext(Dispatchers.IO) {
        sharedPreferences.edit()
            .remove(KEY_RECENTLY_VIEWED)
            .apply()
    }
    
    /**
     * Internal method to retrieve products from SharedPreferences.
     * Returns products sorted by viewedAt timestamp (most recent first).
     */
    private fun getRecentProductsInternal(): List<RecentlyViewedProduct> {
        val json = sharedPreferences.getString(KEY_RECENTLY_VIEWED, null) 
            ?: return emptyList()
        
        return try {
            val type = object : TypeToken<List<RecentlyViewedProduct>>() {}.type
            val products: List<RecentlyViewedProduct> = gson.fromJson(json, type)
            // Ensure sorted by viewedAt descending (most recent first)
            products.sortedByDescending { it.viewedAt }
        } catch (e: Exception) {
            // If parsing fails, return empty list and clear corrupted data
            sharedPreferences.edit().remove(KEY_RECENTLY_VIEWED).apply()
            emptyList()
        }
    }
    
    /**
     * Saves the list of products to SharedPreferences as JSON.
     */
    private fun saveProducts(products: List<RecentlyViewedProduct>) {
        val json = gson.toJson(products)
        sharedPreferences.edit()
            .putString(KEY_RECENTLY_VIEWED, json)
            .apply()
    }
}
