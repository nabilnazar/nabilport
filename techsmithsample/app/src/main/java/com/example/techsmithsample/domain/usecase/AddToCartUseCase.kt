package com.example.techsmithsample.domain.usecase

import com.example.techsmithsample.data.network.response.Product


interface AddToCartUseCase {
    suspend operator fun invoke(product: Product):Long
}