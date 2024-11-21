package com.example.techsmithsample.domain.usecase


interface GetCartCountUseCase {
    suspend operator fun invoke(): Int
}