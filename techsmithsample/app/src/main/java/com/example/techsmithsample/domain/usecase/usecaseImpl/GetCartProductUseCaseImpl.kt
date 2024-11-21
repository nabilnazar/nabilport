package com.example.techsmithsample.domain.usecase.usecaseImpl


import com.example.techsmithsample.data.network.response.Product
import com.example.techsmithsample.domain.repository.ProductRepository
import com.example.techsmithsample.domain.usecase.GetCartProductUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCartProductUseCaseImpl @Inject constructor(
    private val repository: ProductRepository
) : GetCartProductUseCase {
    override suspend fun invoke(id: Int): Flow<Product> = repository.getCartProduct(id = id)
}