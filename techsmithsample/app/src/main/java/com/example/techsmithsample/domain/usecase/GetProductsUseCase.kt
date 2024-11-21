package com.example.techsmithsample.domain.usecase

import androidx.paging.PagingData
import com.example.techsmithsample.data.network.response.Product
import kotlinx.coroutines.flow.Flow

interface GetProductsUseCase {
    suspend operator fun invoke(): Flow<PagingData<Product>>
}