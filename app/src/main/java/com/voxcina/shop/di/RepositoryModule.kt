package com.voxcina.shop.di

import com.voxcina.shop.data.repository.AuthRepository
import com.voxcina.shop.data.repository.AuthRepositoryImpl
import com.voxcina.shop.data.repository.CartRepositoryImpl
import com.voxcina.shop.data.repository.HomeRepository
import com.voxcina.shop.data.repository.HomeRepositoryImpl
import com.voxcina.shop.domain.repository.CartRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module providing repository dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository
    
    @Binds
    @Singleton
    abstract fun bindHomeRepository(
        homeRepositoryImpl: HomeRepositoryImpl
    ): HomeRepository

    @Binds
    @Singleton
    abstract fun bindCartRepository(
        cartRepositoryImpl: CartRepositoryImpl
    ): CartRepository
}
