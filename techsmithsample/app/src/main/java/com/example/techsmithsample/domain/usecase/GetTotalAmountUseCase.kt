package com.example.techsmithsample.domain.usecase

import kotlinx.coroutines.flow.Flow

interface GetTotalAmountUseCase {
    suspend operator fun invoke(): Flow<Int>
}