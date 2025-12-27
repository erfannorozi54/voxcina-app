package com.voxcina.shop.di

import com.voxcina.shop.data.local.TokenManager
import com.voxcina.shop.data.local.TokenManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module providing local storage dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class LocalModule {
    
    @Binds
    @Singleton
    abstract fun bindTokenManager(
        tokenManagerImpl: TokenManagerImpl
    ): TokenManager
}
