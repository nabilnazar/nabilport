package com.example.techsmithsample.domain.usecase.usecaseImpl

import com.example.techsmithsample.domain.repository.ProductRepository
import com.example.techsmithsample.domain.usecase.GetTotalAmountUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTotalAmountUseCaseImpl @Inject constructor(
    private val repository: ProductRepository
) : GetTotalAmountUseCase {
    override suspend fun invoke(): Flow<Int> = repository.getTotalAmount()
}