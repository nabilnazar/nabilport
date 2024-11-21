package com.example.techsmithsample.data.network.api


import com.example.techsmithsample.data.network.response.Product
import com.example.techsmithsample.data.network.response.Products
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductApi {

    companion object {
        const val BASE_URL = "https://dummyjson.com/"
    }

    @GET("products")
    suspend fun getProducts(
        @Query("limit") limit: Int,
        @Query("skip") page: Int
    ): Response<Products>

    @GET("products/{id}")
    suspend fun getProduct(
        @Path("id") id: Int,
    ): Response<Product>


}