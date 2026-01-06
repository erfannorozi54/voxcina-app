package com.voxcina.shop.data.repository

import com.voxcina.shop.data.remote.OrdersApi
import com.voxcina.shop.data.remote.dto.toDomain
import com.voxcina.shop.domain.model.Order
import com.voxcina.shop.domain.repository.OrderRepository
import com.voxcina.shop.domain.repository.OrdersResult
import com.voxcina.shop.util.AppError
import com.voxcina.shop.util.OrderError
import com.voxcina.shop.util.Result
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor(
    private val ordersApi: OrdersApi
) : OrderRepository {

    override suspend fun getOrders(page: Int, limit: Int): Result<OrdersResult> {
        return try {
            val response = ordersApi.getOrders(page, limit)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.Success(
                        OrdersResult(
                            hasOrders = body.hasOrders,
                            orders = body.ordersData.map { it.toDomain() }
                        )
                    )
                } else {
                    Result.Error(OrderError.OrdersLoadFailed)
                }
            } else {
                Result.Error(AppError.ServerError(response.code(), "خطا در دریافت سفارشات"))
            }
        } catch (e: Exception) {
            Result.Error(AppError.NetworkError(e.message ?: "خطا در اتصال به سرور"))
        }
    }

    override suspend fun getOrderById(orderId: String): Result<Order> {
        return try {
            val response = ordersApi.getOrderById(orderId)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.Success(body.toDomain())
                } else {
                    Result.Error(OrderError.OrderNotFound)
                }
            } else {
                Result.Error(AppError.ServerError(response.code(), "خطا در دریافت سفارش"))
            }
        } catch (e: Exception) {
            Result.Error(AppError.NetworkError(e.message ?: "خطا در اتصال به سرور"))
        }
    }
}
