package com.voxcina.shop.data.repository

import com.google.gson.Gson
import com.voxcina.shop.data.remote.CheckoutApi
import com.voxcina.shop.data.remote.dto.ApiErrorResponse
import com.voxcina.shop.data.remote.dto.CreateOrderRequestDto
import com.voxcina.shop.data.remote.dto.OrderItemRequestDto
import com.voxcina.shop.data.remote.dto.OrderVariantRequestDto
import com.voxcina.shop.data.remote.dto.ShippingAddressRequestDto
import com.voxcina.shop.data.remote.dto.toDomain
import com.voxcina.shop.domain.model.CartItem
import com.voxcina.shop.domain.model.Order
import com.voxcina.shop.domain.model.UserAddress
import com.voxcina.shop.domain.repository.CheckoutRepository
import com.voxcina.shop.util.AppError
import com.voxcina.shop.util.CheckoutError
import com.voxcina.shop.util.Result
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of CheckoutRepository.
 * Handles API calls for order creation and maps responses to domain models.
 */
@Singleton
class CheckoutRepositoryImpl @Inject constructor(
    private val checkoutApi: CheckoutApi,
    private val gson: Gson
) : CheckoutRepository {

    override suspend fun createOrder(
        items: List<CartItem>,
        totalAmount: Long,
        shippingAddress: UserAddress,
        shippingCost: Long,
        discountAmount: Long,
        promoCode: String?
    ): Result<Order> = safeApiCall {
        val request = CreateOrderRequestDto(
            items = items.map { it.toRequestDto() },
            totalAmount = totalAmount,
            shippingCost = shippingCost,
            taxAmount = 0,
            discountAmount = discountAmount,
            shippingAddress = shippingAddress.toRequestDto(),
            promoCode = promoCode
        )
        
        val response = checkoutApi.createOrder(request)
        
        if (response.isSuccessful) {
            val order = response.body()?.toDomain()
            if (order != null) {
                Result.Success(order)
            } else {
                Result.Error(CheckoutError.OrderCreationFailed)
            }
        } else {
            Result.Error(mapError(response))
        }
    }

    /**
     * Maps CartItem to OrderItemRequestDto.
     */
    private fun CartItem.toRequestDto(): OrderItemRequestDto = OrderItemRequestDto(
        productId = product.id,
        variant = OrderVariantRequestDto(
            size = variant.size,
            color = variant.color,
            colorName = variant.colorName
        ),
        quantity = quantity,
        priceAtPurchase = product.price
    )

    /**
     * Maps UserAddress to ShippingAddressRequestDto.
     */
    private fun UserAddress.toRequestDto(): ShippingAddressRequestDto = ShippingAddressRequestDto(
        title = title,
        firstName = firstName,
        lastName = lastName,
        phoneNumber = phoneNumber,
        province = province,
        provinceCode = if (provinceCode > 0) provinceCode else null,
        city = city,
        cityCode = if (cityCode > 0) cityCode else null,
        address = address,
        postalCode = postalCode,
        latitude = latitude,
        longitude = longitude
    )

    /**
     * Maps HTTP error codes to checkout-specific error types.
     */
    private fun <T> mapError(response: Response<T>): AppError {
        val errorBody = response.errorBody()?.string()
        val apiError = parseErrorBody(errorBody)
        val message = apiError?.message?.lowercase() ?: apiError?.error?.lowercase() ?: ""

        return when (response.code()) {
            400 -> {
                when {
                    message.contains("cart") || message.contains("empty") -> CheckoutError.EmptyCart
                    message.contains("address") -> CheckoutError.MissingAddress
                    message.contains("product") -> CheckoutError.OrderCreationFailed
                    else -> CheckoutError.OrderCreationFailed
                }
            }
            401 -> CheckoutError.NotAuthenticated
            404 -> CheckoutError.OrderCreationFailed
            in 500..599 -> AppError.ServerError(
                code = response.code(),
                message = apiError?.message ?: apiError?.error ?: "خطای سرور"
            )
            else -> CheckoutError.OrderCreationFailed
        }
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

    /**
     * Wraps API calls with error handling for network exceptions.
     */
    private inline fun <T> safeApiCall(call: () -> Result<T>): Result<T> = try {
        call()
    } catch (e: IOException) {
        Result.Error(AppError.NetworkError())
    } catch (e: Exception) {
        Result.Error(AppError.UnknownError(e.message ?: "خطای ناشناخته"))
    }
}
