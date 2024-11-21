package com.example.techsmithsample.domain.usecase

import com.example.techsmithsample.data.network.response.Product
import kotlinx.coroutines.flow.Flow

interface GetCartProductUseCase {
    suspend operator fun invoke(id: Int): Flow<Product?>
}