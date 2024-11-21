package com.example.techsmithsample.domain.usecase

import com.example.techsmithsample.data.network.response.Product


interface DeleteCartProductUseCase {
    suspend operator fun invoke(product: Product)
}