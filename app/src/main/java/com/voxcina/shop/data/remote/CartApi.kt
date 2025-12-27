package com.voxcina.shop.data.remote

import com.voxcina.shop.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit API interface for cart endpoints.
 * All endpoints require authentication (Bearer token).
 */
interface CartApi {

    /**
     * Get user's active cart with items and summary.
     * GET /api/cart
     *
     * @return Response containing cart data
     */
    @GET("cart")
    suspend fun getCart(): Response<CartResponse>

    /**
     * Create new cart or merge items with existing cart.
     * Used on login to sync local cart.
     * POST /api/cart
     *
     * @param request Cart items to sync
     * @return Response containing updated cart
     */
    @POST("cart")
    suspend fun syncCart(
        @Body request: SyncCartRequest
    ): Response<CartResponse>

    /**
     * Add item to existing cart or increment quantity if exists.
     * POST /api/cart/item
     *
     * @param request Item to add with product ID, quantity, and variant
     * @return Response containing updated cart
     */
    @POST("cart/item")
    suspend fun addItem(
        @Body request: AddCartItemRequest
    ): Response<CartResponse>

    /**
     * Update quantity of existing cart item.
     * Set quantity to 0 to remove item.
     * PUT /api/cart/item
     *
     * @param request Item to update with product ID, new quantity, and variant
     * @return Response containing updated cart
     */
    @PUT("cart/item")
    suspend fun updateItemQuantity(
        @Body request: UpdateCartItemRequest
    ): Response<CartResponse>

    /**
     * Remove specific item from cart.
     * DELETE /api/cart/item
     *
     * @param productId Product ID to remove
     * @param variantSize Size of the variant to remove
     * @param variantColor Color of the variant to remove
     * @return Response containing updated cart
     */
    @DELETE("cart/item")
    suspend fun removeItem(
        @Query("productId") productId: String,
        @Query("variantSize") variantSize: String,
        @Query("variantColor") variantColor: String
    ): Response<CartResponse>

    /**
     * Remove all items from user's cart.
     * DELETE /api/cart
     *
     * @return Response containing empty cart
     */
    @DELETE("cart")
    suspend fun clearCart(): Response<CartResponse>
}
