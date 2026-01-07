package com.voxcina.shop.di

import com.voxcina.shop.data.repository.ActivityRepositoryImpl
import com.voxcina.shop.data.repository.AddressRepositoryImpl
import com.voxcina.shop.data.repository.AuthRepository
import com.voxcina.shop.data.repository.AuthRepositoryImpl
import com.voxcina.shop.data.repository.CartRepositoryImpl
import com.voxcina.shop.data.repository.CheckoutRepositoryImpl
import com.voxcina.shop.data.repository.FaqRepositoryImpl
import com.voxcina.shop.data.repository.HomeRepository
import com.voxcina.shop.data.repository.HomeRepositoryImpl
import com.voxcina.shop.data.repository.OrderRepositoryImpl
import com.voxcina.shop.data.repository.ProductRepositoryImpl
import com.voxcina.shop.data.repository.ProductsListRepositoryImpl
import com.voxcina.shop.data.repository.ProfileRepositoryImpl
import com.voxcina.shop.data.repository.PromotionRepositoryImpl
import com.voxcina.shop.data.repository.ShippingRepositoryImpl
import com.voxcina.shop.data.repository.TicketRepositoryImpl
import com.voxcina.shop.domain.repository.ActivityRepository
import com.voxcina.shop.domain.repository.AddressRepository
import com.voxcina.shop.domain.repository.CartRepository
import com.voxcina.shop.domain.repository.CheckoutRepository
import com.voxcina.shop.domain.repository.FaqRepository
import com.voxcina.shop.domain.repository.OrderRepository
import com.voxcina.shop.domain.repository.ProductRepository
import com.voxcina.shop.domain.repository.ProductsListRepository
import com.voxcina.shop.domain.repository.ProfileRepository
import com.voxcina.shop.domain.repository.PromotionRepository
import com.voxcina.shop.domain.repository.ShippingRepository
import com.voxcina.shop.domain.repository.TicketRepository
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
    abstract fun bindProductsListRepository(impl: ProductsListRepositoryImpl): ProductsListRepository

    @Binds
    @Singleton
    abstract fun bindProfileRepository(impl: ProfileRepositoryImpl): ProfileRepository

    @Binds
    @Singleton
    abstract fun bindAddressRepository(impl: AddressRepositoryImpl): AddressRepository

    @Binds
    @Singleton
    abstract fun bindPromotionRepository(impl: PromotionRepositoryImpl): PromotionRepository

    @Binds
    @Singleton
    abstract fun bindFaqRepository(impl: FaqRepositoryImpl): FaqRepository

    @Binds
    @Singleton
    abstract fun bindTicketRepository(impl: TicketRepositoryImpl): TicketRepository

    @Binds
    @Singleton
    abstract fun bindActivityRepository(impl: ActivityRepositoryImpl): ActivityRepository

    @Binds
    @Singleton
    abstract fun bindShippingRepository(impl: ShippingRepositoryImpl): ShippingRepository

    @Binds
    @Singleton
    abstract fun bindCheckoutRepository(impl: CheckoutRepositoryImpl): CheckoutRepository

    @Binds
    @Singleton
    abstract fun bindOrderRepository(impl: OrderRepositoryImpl): OrderRepository
}
