package com.voxcina.shop.data.repository

import com.google.gson.Gson
import com.voxcina.shop.data.remote.CartApi
import com.voxcina.shop.data.remote.DiscountApi
import com.voxcina.shop.data.remote.dto.ApiErrorResponse
import com.voxcina.shop.data.remote.dto.AddCartItemRequest
import com.voxcina.shop.data.remote.dto.CartVariantRequest
import com.voxcina.shop.data.remote.dto.CouponCartItemRequest
import com.voxcina.shop.data.remote.dto.DiscountCodeRequest
import com.voxcina.shop.data.remote.dto.NegotiatedCouponRequest
import com.voxcina.shop.data.remote.dto.UpdateCartItemRequest
import com.voxcina.shop.domain.model.Cart
import com.voxcina.shop.domain.model.CartVariant
import com.voxcina.shop.domain.model.Discount
import com.voxcina.shop.domain.model.toDomain
import com.voxcina.shop.domain.repository.CartRepository
import com.voxcina.shop.util.AppError
import com.voxcina.shop.util.CartError
import com.voxcina.shop.util.Result
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of CartRepository.
 * Handles API calls and maps responses/errors to domain types.
 */
@Singleton
class CartRepositoryImpl @Inject constructor(
    private val cartApi: CartApi,
    private val discountApi: DiscountApi,
    private val gson: Gson
) : CartRepository {

    override suspend fun getCart(): Result<Cart> {
        return safeApiCall(CartError.CartLoadFailed) {
            val response = cartApi.getCart()
            if (response.isSuccessful) {
                val cart = response.body()?.toDomain()
                if (cart != null) {
                    Result.Success(cart)
                } else {
                    Result.Error(CartError.CartLoadFailed)
                }
            } else {
                Result.Error(mapCartHttpError(response, CartError.CartLoadFailed))
            }
        }
    }

    override suspend fun addItem(
        productId: String,
        quantity: Int,
        variant: CartVariant
    ): Result<Cart> {
        return safeApiCall(CartError.ItemUpdateFailed) {
            val request = AddCartItemRequest(
                productId = productId,
                quantity = quantity,
                variant = CartVariantRequest(
                    variantId = variant.variantId,
                    size = variant.size,
                    color = variant.color
                )
            )
            val response = cartApi.addItem(request)
            if (response.isSuccessful) {
                val cart = response.body()?.toDomain()
                if (cart != null) {
                    Result.Success(cart)
                } else {
                    Result.Error(CartError.ItemUpdateFailed)
                }
            } else {
                Result.Error(mapCartHttpError(response, CartError.ItemUpdateFailed))
            }
        }
    }


    override suspend fun updateItemQuantity(
        productId: String,
        quantity: Int,
        variant: CartVariant
    ): Result<Cart> {
        return safeApiCall(CartError.ItemUpdateFailed) {
            val request = UpdateCartItemRequest(
                productId = productId,
                quantity = quantity,
                variant = CartVariantRequest(
                    variantId = variant.variantId,
                    size = variant.size,
                    color = variant.color
                )
            )
            val response = cartApi.updateItemQuantity(request)
            if (response.isSuccessful) {
                val cart = response.body()?.toDomain()
                if (cart != null) {
                    Result.Success(cart)
                } else {
                    Result.Error(CartError.ItemUpdateFailed)
                }
            } else {
                Result.Error(mapCartHttpError(response, CartError.ItemUpdateFailed))
            }
        }
    }

    override suspend fun removeItem(
        productId: String,
        variant: CartVariant
    ): Result<Cart> {
        return safeApiCall(CartError.ItemRemoveFailed) {
            val response = cartApi.removeItem(
                productId = productId,
                variantSize = variant.size,
                variantColor = variant.color
            )
            if (response.isSuccessful) {
                val cart = response.body()?.toDomain()
                if (cart != null) {
                    Result.Success(cart)
                } else {
                    Result.Error(CartError.ItemRemoveFailed)
                }
            } else {
                Result.Error(mapCartHttpError(response, CartError.ItemRemoveFailed))
            }
        }
    }

    override suspend fun clearCart(): Result<Cart> {
        return safeApiCall(CartError.ClearCartFailed) {
            val response = cartApi.clearCart()
            if (response.isSuccessful) {
                val cart = response.body()?.toDomain()
                if (cart != null) {
                    Result.Success(cart)
                } else {
                    Result.Error(CartError.ClearCartFailed)
                }
            } else {
                Result.Error(mapCartHttpError(response, CartError.ClearCartFailed))
            }
        }
    }

    override suspend fun applyVoucher(code: String, cart: Cart): Result<Discount> {
        return safeApiCall(CartError.DiscountInvalid("Unknown error")) {
            val response = discountApi.validateDiscount(code)
            if (response.isSuccessful) {
                val discount = response.body()?.toDomain()
                if (discount != null) {
                    Result.Success(discount)
                } else {
                    Result.Error(CartError.DiscountInvalid("Invalid response"))
                }
            } else if (response.code() == 404) {
                // Not an admin discount code — it may be a negotiated or
                // cart-recovery coupon, validated against the actual cart
                // contents (mirrors the web front-end fallback).
                applyNegotiatedCoupon(code, cart)
            } else {
                Result.Error(mapDiscountHttpError(response, code))
            }
        }
    }

    /**
     * Validates a negotiated / cart-recovery coupon against the current cart.
     * POST /api/coupons/apply
     */
    private suspend fun applyNegotiatedCoupon(code: String, cart: Cart): Result<Discount> {
        val request = NegotiatedCouponRequest(
            code = code,
            cartItems = cart.items.map { item ->
                CouponCartItemRequest(
                    productId = item.product.id,
                    color = item.variant.color,
                    colorName = item.variant.colorName
                )
            }
        )
        return safeApiCall(CartError.DiscountInvalid("Unknown error")) {
            val response = discountApi.applyNegotiatedCoupon(request)
            if (response.isSuccessful) {
                val body = response.body()
                val discount = body?.discount?.toDomain()
                if (body?.valid == true && discount != null) {
                    Result.Success(discount)
                } else {
                    Result.Error(CartError.DiscountInvalid("Invalid response"))
                }
            } else {
                Result.Error(mapDiscountHttpError(response, code))
            }
        }
    }

    override suspend fun activateVoucher(code: String): Result<Unit> {
        return safeApiCall(CartError.DiscountInvalid("Unknown error")) {
            val response = discountApi.activateDiscount(DiscountCodeRequest(code))
            if (response.isSuccessful) {
                Result.Success(Unit)
            } else {
                Result.Error(mapDiscountHttpError(response, code))
            }
        }
    }

    override suspend fun deactivateVoucher(code: String): Result<Unit> {
        return safeApiCall(CartError.DiscountInvalid("Unknown error")) {
            val response = discountApi.deactivateDiscount(DiscountCodeRequest(code))
            if (response.isSuccessful) {
                Result.Success(Unit)
            } else {
                Result.Error(mapDiscountHttpError(response, code))
            }
        }
    }


    /**
     * Wraps API calls with error handling for network exceptions.
     */
    private inline fun <T> safeApiCall(
        fallbackError: AppError,
        apiCall: () -> Result<T>
    ): Result<T> {
        return try {
            apiCall()
        } catch (e: IOException) {
            Result.Error(AppError.NetworkError())
        } catch (e: Exception) {
            Result.Error(fallbackError)
        }
    }

    /**
     * Maps HTTP error codes to cart-specific error types.
     */
    private fun <T> mapCartHttpError(
        response: Response<T>,
        fallbackError: CartError
    ): AppError {
        val errorBody = response.errorBody()?.string()
        val apiError = parseErrorBody(errorBody)
        val message = apiError?.message?.lowercase() ?: apiError?.error?.lowercase() ?: ""

        return when (response.code()) {
            400 -> {
                when {
                    message.contains("stock") || message.contains("inventory") -> 
                        CartError.InsufficientStock
                    else -> fallbackError
                }
            }
            404 -> fallbackError
            in 500..599 -> AppError.ServerError(
                code = response.code(),
                message = apiError?.message ?: apiError?.error ?: "خطای سرور"
            )
            else -> AppError.UnknownError(
                apiError?.message ?: apiError?.error ?: "خطای ناشناخته"
            )
        }
    }

    /**
     * Maps HTTP error codes to discount-specific error types.
     */
    private fun <T> mapDiscountHttpError(
        response: Response<T>,
        code: String
    ): AppError {
        val errorBody = response.errorBody()?.string()
        val apiError = parseErrorBody(errorBody)
        val message = apiError?.message?.lowercase() ?: apiError?.error?.lowercase() ?: ""

        return when (response.code()) {
            400 -> {
                when {
                    message.contains("expired") -> CartError.DiscountExpired(code)
                    message.contains("minimum") || message.contains("min") -> {
                        // Try to extract minimum amount from message
                        val minAmount = extractMinAmount(message)
                        CartError.DiscountMinOrderNotMet(minAmount)
                    }
                    else -> CartError.DiscountInvalid(message)
                }
            }
            404 -> CartError.DiscountInvalid("کد تخفیف یافت نشد")
            in 500..599 -> AppError.ServerError(
                code = response.code(),
                message = apiError?.message ?: apiError?.error ?: "خطای سرور"
            )
            else -> CartError.DiscountInvalid(
                apiError?.message ?: apiError?.error ?: "کد تخفیف نامعتبر است"
            )
        }
    }

    /**
     * Extracts minimum order amount from error message.
     */
    private fun extractMinAmount(message: String): Long {
        val regex = Regex("\\d+")
        val match = regex.find(message)
        return match?.value?.toLongOrNull() ?: 0L
    }

    /**
     * Parses error response body to ApiErrorResponse.
     */
    private fun parseErrorBody(errorBody: String?): ApiErrorResponse? {
        if (errorBody.isNullOrBlank()) return null
        return try {
            gson.fromJson(errorBody, ApiErrorResponse::class.java)
        } catch (e: Exception) {
            null
        }
    }
}
