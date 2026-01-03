package com.voxcina.shop.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ProvinceDto(
    @SerializedName("province_code") val provinceCode: Int,
    @SerializedName("province_name") val provinceName: String
)

data class CityDto(
    @SerializedName("city_code") val cityCode: Int,
    @SerializedName("city_name") val cityName: String
)
