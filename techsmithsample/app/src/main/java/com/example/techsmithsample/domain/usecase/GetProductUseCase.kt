package com.example.techsmithsample.domain.usecase

import com.example.techsmithsample.core.data.network.Resource
import com.example.techsmithsample.presentation.screen.productdetail.state.GetProductResponseData
import kotlinx.coroutines.flow.Flow

interface GetProductUseCase {
     suspend operator fun invoke(productId: Int): Flow<Resource<GetProductResponseData>>
}