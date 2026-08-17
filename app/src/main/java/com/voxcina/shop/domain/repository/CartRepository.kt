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
     * Validate and apply a discount code / voucher to the cart.
     * Tries the admin discount endpoint first (GET /discounts/code/{code});
     * if the code is not found there, falls back to negotiated/cart-recovery
     * coupon validation against the actual cart contents
     * (POST /coupons/apply), mirroring the web front-end flow.
     *
     * @param code Voucher code to validate
     * @param cart Current cart used for negotiated coupon validation
     * @return Result<Discount> containing discount details if valid
     */
    suspend fun applyVoucher(code: String, cart: Cart): Result<Discount>

    /**
     * Mark a voucher as applied to the cart on the backend.
     * POST /api/discounts/activate
     *
     * @param code Voucher code to activate
     */
    suspend fun activateVoucher(code: String): Result<Unit>

    /**
     * Mark a voucher as removed from the cart on the backend.
     * POST /api/discounts/deactivate
     *
     * @param code Voucher code to deactivate
     */
    suspend fun deactivateVoucher(code: String): Result<Unit>
}
