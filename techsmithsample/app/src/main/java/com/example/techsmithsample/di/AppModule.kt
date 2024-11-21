package com.example.techsmithsample.di

import android.content.Context
import com.example.techsmithsample.core.data.network.BaseApi
import com.example.techsmithsample.data.network.api.ProductApi
import com.example.techsmithsample.data.roomdb.AppDatabase
import com.example.techsmithsample.domain.repository.ProductRepository
import com.example.techsmithsample.domain.repository.ProductRepositoryImpl
import com.example.techsmithsample.domain.usecase.AddToCartUseCase
import com.example.techsmithsample.domain.usecase.DeleteCartProductUseCase
import com.example.techsmithsample.domain.usecase.GetCartCountUseCase
import com.example.techsmithsample.domain.usecase.GetCartProductUseCase
import com.example.techsmithsample.domain.usecase.GetCartUseCase
import com.example.techsmithsample.domain.usecase.GetProductUseCase
import com.example.techsmithsample.domain.usecase.GetProductsUseCase
import com.example.techsmithsample.domain.usecase.GetTotalAmountUseCase
import com.example.techsmithsample.domain.usecase.usecaseImpl.AddToCartUseCaseImpl
import com.example.techsmithsample.domain.usecase.usecaseImpl.DeleteCartProductUseCaseImpl
import com.example.techsmithsample.domain.usecase.usecaseImpl.GetCartCountUseCaseImpl
import com.example.techsmithsample.domain.usecase.usecaseImpl.GetCartProductUseCaseImpl
import com.example.techsmithsample.domain.usecase.usecaseImpl.GetCartUseCaseImpl
import com.example.techsmithsample.domain.usecase.usecaseImpl.GetProductUseCaseImpl
import com.example.techsmithsample.domain.usecase.usecaseImpl.GetProductsUseCaseImpl
import com.example.techsmithsample.domain.usecase.usecaseImpl.GetTotalAmountUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun providesProductApi(): ProductApi = BaseApi(ProductApi::class.java, ProductApi.BASE_URL)


    @Provides
    @Singleton
    fun providesAppDatabase(@ApplicationContext context: Context): AppDatabase =
        AppDatabase.getDatabase(context = context)


    @Singleton
    @Provides
    fun providesProductRepository(
        productApi: ProductApi,
        appDatabase: AppDatabase
    ): ProductRepository {
        return ProductRepositoryImpl(
            productApi = productApi,
            appDatabase = appDatabase
        )
    }


    @Provides
    fun providesGetProductsUseCase(
        productRepository: ProductRepository
    ): GetProductsUseCase {
        return GetProductsUseCaseImpl(productRepository)
    }

    @Provides
    fun providesGetProductUseCase(
        productRepository: ProductRepository
    ): GetProductUseCase {
        return GetProductUseCaseImpl(productRepository)
    }

    @Provides
    fun providesAddToCartUseCase(
        productRepository: ProductRepository
    ): AddToCartUseCase {
        return AddToCartUseCaseImpl(productRepository)
    }

    @Provides
    fun providesGetCartUseCase(
        productRepository: ProductRepository
    ): GetCartUseCase {
        return GetCartUseCaseImpl(productRepository)
    }

    @Provides
    fun providesDeleteCartProductUseCase(
        productRepository: ProductRepository
    ): DeleteCartProductUseCase {
        return DeleteCartProductUseCaseImpl(productRepository)
    }

    @Provides
    fun providesGetTotalAmountUseCase(
        productRepository: ProductRepository
    ): GetTotalAmountUseCase {
        return GetTotalAmountUseCaseImpl(productRepository)
    }

    @Provides
    fun providesGetCartProductUseCase(
        productRepository: ProductRepository
    ): GetCartProductUseCase {
        return GetCartProductUseCaseImpl(productRepository)
    }

    @Provides
    fun providesGetCartCountUseCaseUseCase(
        productRepository: ProductRepository
    ): GetCartCountUseCase {
        return GetCartCountUseCaseImpl(productRepository)
    }

}