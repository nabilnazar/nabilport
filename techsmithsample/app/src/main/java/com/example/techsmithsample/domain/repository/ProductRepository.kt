package com.example.techsmithsample.domain.repository


import com.example.techsmithsample.core.data.network.Resource
import com.example.techsmithsample.data.network.response.Product
import com.example.techsmithsample.data.network.response.Products
import com.example.techsmithsample.presentation.screen.productdetail.state.GetProductResponseData
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface ProductRepository {
    suspend fun getProducts(limit: Int, page: Int): Response<Products>
    suspend fun getProduct(productId: Int): Flow<Resource<GetProductResponseData>>

    suspend fun addToCart(product: Product):Long
    suspend fun getCart(): Flow<List<Product>>
    suspend fun getCartCount(): Int
    suspend fun getTotalAmount(): Flow<Int>
    suspend fun getCartProduct(id: Int): Flow<Product>
    suspend fun deleteCartProduct(product: Product)
}