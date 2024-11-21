package com.example.techsmithsample.domain.usecase.usecaseImpl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.techsmithsample.data.network.pagination.PRODUCT_PAGE_SIZE
import com.example.techsmithsample.data.network.pagination.ProductPagingSource
import com.example.techsmithsample.data.network.response.Product
import com.example.techsmithsample.domain.repository.ProductRepository
import com.example.techsmithsample.domain.usecase.GetProductsUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProductsUseCaseImpl @Inject constructor(
    private val repository: ProductRepository
) : GetProductsUseCase {
    override suspend fun invoke(): Flow<PagingData<Product>> =
        Pager(
            config = PagingConfig(pageSize = PRODUCT_PAGE_SIZE, prefetchDistance = 2),
            pagingSourceFactory = {
                ProductPagingSource(repository)
            }
        ).flow
}