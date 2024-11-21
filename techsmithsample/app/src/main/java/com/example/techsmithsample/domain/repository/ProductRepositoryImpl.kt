package com.example.techsmithsample.domain.repository

import com.example.techsmithsample.core.data.network.Resource
import com.example.techsmithsample.core.data.network.SafeApiRequest
import com.example.techsmithsample.core.data.state.State
import com.example.techsmithsample.core.data.state.handle
import com.example.techsmithsample.data.network.api.ProductApi
import com.example.techsmithsample.data.network.response.Product
import com.example.techsmithsample.data.network.response.Products
import com.example.techsmithsample.data.roomdb.AppDatabase
import com.example.techsmithsample.data.roomdb.ProductDao
import com.example.techsmithsample.presentation.screen.productdetail.state.GetProductResponseData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val productApi: ProductApi,
    appDatabase: AppDatabase
): SafeApiRequest(),ProductRepository {


    private var productDao: ProductDao = appDatabase.productDao()



    override suspend fun getProducts(
        limit: Int,
        page: Int): Response<Products> = productApi.getProducts(limit=limit,page=page)


    override suspend fun getProduct(productId: Int): Flow<Resource<GetProductResponseData>> =
        flow {
            val responseData = GetProductResponseData()
            responseData.dataState = State.LOADING  
            emit(Resource.Loading(responseData))
            try {
                val apiResponse = safeApiRequest { productApi.getProduct(id = productId) }
                emit(with(apiResponse) {
                    handle(
                        success = {
                            val productResponseData = GetProductResponseData()
                            if (this.data != null) {
                                productResponseData.dataState = State.SUCCESS
                                Resource.Success(data = productResponseData.also {
                                    it.data = this.data
                                })
                            } else {
                                productResponseData.dataState = State.ERROR
                                productResponseData.error = "Product not found!"
                                Resource.Error(error = this.error)
                            }
                        },
                        error = {
                            val productResponseData = GetProductResponseData()
                            productResponseData.dataState = State.ERROR
                            productResponseData.error = "Product not found!"
                            this.error?.let {
                                if (it.errorCode == 404) {
                                    try {
                                        productResponseData.error =
                                            JSONObject(it.error ?: "error").get("message")
                                                .toString()
                                    } catch (e: Exception) {
                                        productResponseData.error = e.localizedMessage
                                    }
                                }
                            }
                            Resource.Error(data = productResponseData)
                        },
                        loading = {
                            val productResponseData =
                                this.data?.let { GetProductResponseData(data = it) }
                            productResponseData?.dataState = State.LOADING
                            Resource.Loading(productResponseData)
                        }
                    )
                })
            } catch (exception: Exception) {
                val productResponseData = GetProductResponseData()
                productResponseData.dataState = State.ERROR
                productResponseData.error = exception.localizedMessage
                emit(Resource.Error(data = productResponseData))
            }
        }.catch {
            val productResponseData = GetProductResponseData()
            productResponseData.dataState = State.ERROR
            productResponseData.error = it.localizedMessage
            emit(Resource.Error(data = productResponseData))
        }


    override suspend fun addToCart(product: Product) =  productDao.addToCart(product)

    override suspend fun getCart(): Flow<List<Product>> = productDao.getCartProducts()

    override suspend fun getCartCount(): Int  = productDao.getCartCount()

    override suspend fun getTotalAmount(): Flow<Int> = productDao.getTotalAmount()

    override suspend fun getCartProduct(id: Int): Flow<Product> = productDao.getCartProduct(id)

    override suspend fun deleteCartProduct(product: Product) {
        productDao.deleteCartProduct(product = product)
    }
}