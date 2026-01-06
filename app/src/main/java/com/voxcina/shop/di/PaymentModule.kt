package com.voxcina.shop.di

import com.voxcina.shop.data.remote.PaymentApi
import com.voxcina.shop.data.repository.PaymentRepositoryImpl
import com.voxcina.shop.domain.repository.PaymentRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PaymentModule {

    @Provides
    @Singleton
    fun providePaymentApi(retrofit: Retrofit): PaymentApi {
        return retrofit.create(PaymentApi::class.java)
    }

    @Provides
    @Singleton
    fun providePaymentRepository(paymentApi: PaymentApi): PaymentRepository {
        return PaymentRepositoryImpl(paymentApi)
    }
}
