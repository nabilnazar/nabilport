package com.example.techsmithsample.domain.usecase.usecaseImpl

import com.example.techsmithsample.core.data.network.Resource
import com.example.techsmithsample.domain.repository.ProductRepository
import com.example.techsmithsample.domain.usecase.GetProductUseCase
import com.example.techsmithsample.presentation.screen.productdetail.state.GetProductResponseData
import kotlinx.coroutines.flow.Flow

class GetProductUseCaseImpl(
    private val repository: ProductRepository
) : GetProductUseCase {
    override suspend fun invoke(productId: Int): Flow<Resource<GetProductResponseData>> =
        repository.getProduct(productId = productId)
}