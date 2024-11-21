package com.example.techsmithsample.domain.usecase

import com.example.techsmithsample.data.network.response.Product
import kotlinx.coroutines.flow.Flow

interface GetCartUseCase {
    suspend operator fun invoke():Flow<List<Product>>
}