package com.example.techsmithsample.domain.usecase.usecaseImpl


import com.example.techsmithsample.data.network.response.Product
import com.example.techsmithsample.domain.repository.ProductRepository
import com.example.techsmithsample.domain.usecase.AddToCartUseCase
import javax.inject.Inject

class AddToCartUseCaseImpl @Inject constructor(private val repository: ProductRepository) : AddToCartUseCase {
    override suspend fun invoke(product: Product): Long = repository.addToCart(product = product)
}