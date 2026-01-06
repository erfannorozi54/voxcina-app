package com.voxcina.shop.data.remote

import com.voxcina.shop.data.remote.dto.RecentlyViewedResponseDto
import com.voxcina.shop.data.remote.dto.TrackActivityRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ActivityApi {
    
    @POST("activity/track")
    suspend fun trackActivity(@Body request: TrackActivityRequestDto): Response<Unit>
    
    @GET("activity/recently-viewed")
    suspend fun getRecentlyViewed(): Response<RecentlyViewedResponseDto>
}
