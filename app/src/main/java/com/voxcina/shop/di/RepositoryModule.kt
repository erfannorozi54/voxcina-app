package com.voxcina.shop.di

import com.voxcina.shop.data.repository.AddressRepositoryImpl
import com.voxcina.shop.data.repository.AuthRepository
import com.voxcina.shop.data.repository.AuthRepositoryImpl
import com.voxcina.shop.data.repository.CartRepositoryImpl
import com.voxcina.shop.data.repository.HomeRepository
import com.voxcina.shop.data.repository.HomeRepositoryImpl
import com.voxcina.shop.data.repository.ProductRepositoryImpl
import com.voxcina.shop.data.repository.ProfileRepositoryImpl
import com.voxcina.shop.domain.repository.AddressRepository
import com.voxcina.shop.domain.repository.CartRepository
import com.voxcina.shop.domain.repository.ProductRepository
import com.voxcina.shop.domain.repository.ProfileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository
    
    @Binds
    @Singleton
    abstract fun bindHomeRepository(impl: HomeRepositoryImpl): HomeRepository

    @Binds
    @Singleton
    abstract fun bindCartRepository(impl: CartRepositoryImpl): CartRepository

    @Binds
    @Singleton
    abstract fun bindProductRepository(impl: ProductRepositoryImpl): ProductRepository

    @Binds
    @Singleton
    abstract fun bindProfileRepository(impl: ProfileRepositoryImpl): ProfileRepository

    @Binds
    @Singleton
    abstract fun bindAddressRepository(impl: AddressRepositoryImpl): AddressRepository
}
