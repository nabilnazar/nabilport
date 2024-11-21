package com.example.techsmithsample.domain.usecase.usecaseImpl


import com.example.techsmithsample.data.network.response.Product
import com.example.techsmithsample.domain.repository.ProductRepository
import com.example.techsmithsample.domain.usecase.DeleteCartProductUseCase
import javax.inject.Inject

class DeleteCartProductUseCaseImpl @Inject constructor(
    private val repository: ProductRepository
) : DeleteCartProductUseCase {
    override suspend fun invoke(product: Product) {
        repository.deleteCartProduct(product = product)
    }
}