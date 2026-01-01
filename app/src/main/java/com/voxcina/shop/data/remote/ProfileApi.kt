package com.voxcina.shop.data.remote

import com.voxcina.shop.data.remote.dto.AddressDto
import com.voxcina.shop.data.remote.dto.AddressRequestDto
import com.voxcina.shop.data.remote.dto.ProfileResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ProfileApi {

    @GET("users/profile")
    suspend fun getProfile(): Response<ProfileResponseDto>

    @GET("users/addresses")
    suspend fun getAddresses(): Response<List<AddressDto>>

    @POST("users/addresses")
    suspend fun addAddress(@Body address: AddressRequestDto): Response<List<AddressDto>>

    @PUT("users/addresses/{index}")
    suspend fun updateAddress(
        @Path("index") index: Int,
        @Body address: AddressRequestDto
    ): Response<List<AddressDto>>

    @DELETE("users/addresses/{index}")
    suspend fun deleteAddress(@Path("index") index: Int): Response<List<AddressDto>>
}
