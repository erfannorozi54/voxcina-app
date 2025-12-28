package com.voxcina.shop.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.voxcina.shop.BuildConfig
import com.voxcina.shop.data.remote.ApiClient
import com.voxcina.shop.data.remote.AuthApi
import com.voxcina.shop.data.remote.AuthInterceptor
import com.voxcina.shop.data.remote.CartApi
import com.voxcina.shop.data.remote.DiscountApi
import com.voxcina.shop.data.remote.HomeApi
import com.voxcina.shop.data.remote.ProductApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * Hilt module providing network-related dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder().create()
    }
    
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return ApiClient.createLoggingInterceptor(BuildConfig.DEBUG)
    }
    
    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return ApiClient.createOkHttpClient(authInterceptor, loggingInterceptor)
    }
    
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return ApiClient.createRetrofit(okHttpClient)
    }
    
    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }
    
    @Provides
    @Singleton
    fun provideHomeApi(retrofit: Retrofit): HomeApi {
        return retrofit.create(HomeApi::class.java)
    }
    
    @Provides
    @Singleton
    fun provideCartApi(retrofit: Retrofit): CartApi {
        return retrofit.create(CartApi::class.java)
    }
    
    @Provides
    @Singleton
    fun provideDiscountApi(retrofit: Retrofit): DiscountApi {
        return retrofit.create(DiscountApi::class.java)
    }
    
    @Provides
    @Singleton
    fun provideProductApi(retrofit: Retrofit): ProductApi {
        return retrofit.create(ProductApi::class.java)
    }
}
