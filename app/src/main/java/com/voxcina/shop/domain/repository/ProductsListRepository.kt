package com.voxcina.shop.domain.repository

import com.voxcina.shop.domain.model.Brand
import com.voxcina.shop.domain.model.Category
import com.voxcina.shop.domain.model.ProductListResult
import com.voxcina.shop.util.Result

interface ProductsListRepository {
    suspend fun getProducts(
        page: Int = 1,
        limit: Int = 20,
        sort: String? = null,
        categoryId: String? = null,
        brandId: String? = null,
        search: String? = null,
        isFlashSale: Boolean? = null,
        inStock: Boolean? = null
    ): Result<ProductListResult>

    suspend fun getCategories(): Result<List<Category>>
    suspend fun getBrands(): Result<List<Brand>>
}
