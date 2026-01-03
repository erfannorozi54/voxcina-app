package com.voxcina.shop.data.repository

import com.voxcina.shop.data.remote.ProfileApi
import com.voxcina.shop.data.remote.dto.AddressRequestDto
import com.voxcina.shop.domain.model.UserAddress
import com.voxcina.shop.domain.model.toDomain
import com.voxcina.shop.domain.repository.AddressRepository
import com.voxcina.shop.util.AddressError
import com.voxcina.shop.util.AppError
import com.voxcina.shop.util.Result
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddressRepositoryImpl @Inject constructor(
    private val profileApi: ProfileApi
) : AddressRepository {

    override suspend fun getAddresses(): Result<List<UserAddress>> = safeApiCall {
        val response = profileApi.getAddresses()
        if (response.isSuccessful) {
            Result.Success(response.body()?.map { it.toDomain() } ?: emptyList())
        } else {
            Result.Error(mapError(response.code()))
        }
    }

    override suspend fun addAddress(address: UserAddress): Result<List<UserAddress>> = safeApiCall {
        val response = profileApi.addAddress(address.toRequest())
        if (response.isSuccessful) {
            Result.Success(response.body()?.map { it.toDomain() } ?: emptyList())
        } else {
            Result.Error(mapError(response.code()))
        }
    }

    override suspend fun updateAddress(index: Int, address: UserAddress): Result<List<UserAddress>> = safeApiCall {
        val response = profileApi.updateAddress(index, address.toRequest())
        if (response.isSuccessful) {
            Result.Success(response.body()?.map { it.toDomain() } ?: emptyList())
        } else {
            Result.Error(mapError(response.code()))
        }
    }

    override suspend fun deleteAddress(index: Int): Result<List<UserAddress>> = safeApiCall {
        val response = profileApi.deleteAddress(index)
        if (response.isSuccessful) {
            Result.Success(response.body()?.map { it.toDomain() } ?: emptyList())
        } else {
            Result.Error(mapError(response.code()))
        }
    }

    private fun UserAddress.toRequest() = AddressRequestDto(
        title = title,
        firstName = firstName,
        lastName = lastName,
        phoneNumber = phoneNumber,
        province = province,
        provinceCode = if (provinceCode > 0) provinceCode else null,
        city = city,
        cityCode = if (cityCode > 0) cityCode else null,
        street = street,
        address = address,
        postalCode = postalCode,
        latitude = latitude,
        longitude = longitude,
        isDefault = isDefault
    )

    private fun mapError(code: Int): AppError = when (code) {
        401 -> AddressError.NotAuthenticated
        404 -> AddressError.AddressNotFound
        400 -> AddressError.InvalidAddress
        else -> AddressError.AddressSaveFailed
    }

    private inline fun <T> safeApiCall(call: () -> Result<T>): Result<T> = try {
        call()
    } catch (e: IOException) {
        Result.Error(AppError.NetworkError())
    } catch (e: Exception) {
        Result.Error(AppError.UnknownError(e.message ?: "خطای ناشناخته"))
    }
}
