package com.example.techsmithsample.data.network.response

import androidx.room.Entity



data class Products(
    val limit: Int?,
    val products: List<Product>?,
    val skip: Int?,
    val total: Int?
)