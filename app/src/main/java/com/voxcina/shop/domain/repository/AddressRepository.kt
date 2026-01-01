package com.voxcina.shop.domain.repository

import com.voxcina.shop.domain.model.UserAddress
import com.voxcina.shop.util.Result

interface AddressRepository {
    suspend fun getAddresses(): Result<List<UserAddress>>
    suspend fun addAddress(address: UserAddress): Result<List<UserAddress>>
    suspend fun updateAddress(index: Int, address: UserAddress): Result<List<UserAddress>>
    suspend fun deleteAddress(index: Int): Result<List<UserAddress>>
}
