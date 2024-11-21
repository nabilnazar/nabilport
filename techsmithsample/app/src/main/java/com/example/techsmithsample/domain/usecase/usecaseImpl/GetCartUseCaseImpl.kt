package com.example.techsmithsample.domain.usecase.usecaseImpl

import com.example.techsmithsample.data.network.response.Product
import com.example.techsmithsample.domain.repository.ProductRepository
import com.example.techsmithsample.domain.usecase.GetCartUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCartUseCaseImpl @Inject constructor(
    private val repository: ProductRepository
) : GetCartUseCase {
    override suspend fun invoke(): Flow<List<Product>> = repository.getCart()
}