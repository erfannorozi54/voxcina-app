package com.voxcina.shop.domain.repository

import com.voxcina.shop.domain.model.Cart
import com.voxcina.shop.domain.model.CartVariant
import com.voxcina.shop.domain.model.Discount
import com.voxcina.shop.util.Result

/**
 * Repository interface for cart operations.
 * Abstracts the data layer from the domain layer.
 */
interface CartRepository {

    /**
     * Get user's active cart with items and summary.
     * @return Result<Cart> containing cart data
     */
    suspend fun getCart(): Result<Cart>

    /**
     * Add item to cart or increment quantity if exists.
     * @param productId Product ID to add
     * @param quantity Quantity to add
     * @param variant Variant selection (size and color)
     * @return Result<Cart> containing updated cart
     */
    suspend fun addItem(
        productId: String,
        quantity: Int,
        variant: CartVariant
    ): Result<Cart>

    /**
     * Update quantity of existing cart item.
     * @param productId Product ID to update
     * @param quantity New quantity (set to 0 to remove)
     * @param variant Variant selection (size and color)
     * @return Result<Cart> containing updated cart
     */
    suspend fun updateItemQuantity(
        productId: String,
        quantity: Int,
        variant: CartVariant
    ): Result<Cart>

    /**
     * Remove specific item from cart.
     * @param productId Product ID to remove
     * @param variant Variant selection (size and color)
     * @return Result<Cart> containing updated cart
     */
    suspend fun removeItem(
        productId: String,
        variant: CartVariant
    ): Result<Cart>

    /**
     * Remove all items from user's cart.
     * @return Result<Cart> containing empty cart
     */
    suspend fun clearCart(): Result<Cart>

    /**
     * Validate and get discount details by code.
     * @param code Discount code to validate
     * @return Result<Discount> containing discount details if valid
     */
    suspend fun validateDiscountCode(code: String): Result<Discount>
}
