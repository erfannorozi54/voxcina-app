package com.voxcina.shop.data.remote

import com.voxcina.shop.data.remote.dto.DiscountCodeRequest
import com.voxcina.shop.data.remote.dto.DiscountResponse
import com.voxcina.shop.data.remote.dto.NegotiatedCouponRequest
import com.voxcina.shop.data.remote.dto.NegotiatedCouponResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Retrofit API interface for discount/voucher endpoints.
 *
 * Mirrors the web front-end voucher flow:
 * 1. GET /discounts/code/{code} validates admin discount codes.
 * 2. If not found (404), POST /coupons/apply validates negotiated /
 *    cart-recovery coupons against the actual cart contents.
 * 3. POST /discounts/activate and /discounts/deactivate track whether a
 *    voucher is applied to the cart (usage counters).
 */
interface DiscountApi {

    /**
     * Validate and get discount details by code.
     * GET /api/discounts/code/{code}
     *
     * @param code Discount code to validate
     * @return Response containing discount details if valid
     */
    @GET("discounts/code/{code}")
    suspend fun validateDiscount(
        @Path("code") code: String
    ): Response<DiscountResponse>

    /**
     * Validate a negotiated / cart-recovery coupon against the current cart.
     * POST /api/coupons/apply
     *
     * @param body Code + cart items (product_id, color, color_name)
     * @return Response containing the discount if valid
     */
    @POST("coupons/apply")
    suspend fun applyNegotiatedCoupon(
        @Body body: NegotiatedCouponRequest
    ): Response<NegotiatedCouponResponse>

    /**
     * Mark a voucher as applied to the cart.
     * POST /api/discounts/activate
     *
     * @param body Discount code
     */
    @POST("discounts/activate")
    suspend fun activateDiscount(
        @Body body: DiscountCodeRequest
    ): Response<Unit>

    /**
     * Mark a voucher as no longer applied to the cart.
     * POST /api/discounts/deactivate
     *
     * @param body Discount code
     */
    @POST("discounts/deactivate")
    suspend fun deactivateDiscount(
        @Body body: DiscountCodeRequest
    ): Response<Unit>
}
