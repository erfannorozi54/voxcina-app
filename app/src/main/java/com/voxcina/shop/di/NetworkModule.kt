package com.voxcina.shop.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.voxcina.shop.BuildConfig
import com.voxcina.shop.data.remote.ApiClient
import com.voxcina.shop.data.remote.AuthApi
import com.voxcina.shop.data.remote.AuthInterceptor
import com.voxcina.shop.data.remote.CartApi
import com.voxcina.shop.data.remote.DiscountApi
import com.voxcina.shop.data.remote.FaqApi
import com.voxcina.shop.data.remote.HomeApi
import com.voxcina.shop.data.remote.LocalityApi
import com.voxcina.shop.data.remote.ProductApi
import com.voxcina.shop.data.remote.ProfileApi
import com.voxcina.shop.data.remote.TicketApi
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
    fun provideAuthApi(retrofit: Retrofit, authInterceptor: AuthInterceptor): AuthApi {
        val api = retrofit.create(AuthApi::class.java)
        authInterceptor.setAuthApi(api)
        return api
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
    
    @Provides
    @Singleton
    fun provideProfileApi(retrofit: Retrofit): ProfileApi {
        return retrofit.create(ProfileApi::class.java)
    }
    
    @Provides
    @Singleton
    fun provideLocalityApi(retrofit: Retrofit): LocalityApi {
        return retrofit.create(LocalityApi::class.java)
    }
    
    @Provides
    @Singleton
    fun provideFaqApi(retrofit: Retrofit): FaqApi {
        return retrofit.create(FaqApi::class.java)
    }
    
    @Provides
    @Singleton
    fun provideTicketApi(retrofit: Retrofit): TicketApi {
        return retrofit.create(TicketApi::class.java)
    }
}
