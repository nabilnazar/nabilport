package com.example.techsmithsample.domain.usecase.usecaseImpl

import com.example.techsmithsample.domain.repository.ProductRepository
import com.example.techsmithsample.domain.usecase.GetCartCountUseCase
import javax.inject.Inject

class GetCartCountUseCaseImpl @Inject constructor(
    private val repository: ProductRepository
) : GetCartCountUseCase {
    override suspend fun invoke(): Int = repository.getCartCount()
}