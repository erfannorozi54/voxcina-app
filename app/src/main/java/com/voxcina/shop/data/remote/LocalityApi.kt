package com.voxcina.shop.data.remote

import com.voxcina.shop.data.remote.dto.CityDto
import com.voxcina.shop.data.remote.dto.ProvinceDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface LocalityApi {

    @GET("postex/locality/provinces")
    suspend fun getProvinces(): Response<List<ProvinceDto>>

    @GET("postex/locality/cities/{provinceCode}")
    suspend fun getCities(@Path("provinceCode") provinceCode: Int): Response<List<CityDto>>
}
