package com.example.techsmithsample.data.network.pagination

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.techsmithsample.data.network.response.Product
import com.example.techsmithsample.domain.repository.ProductRepository

const val PRODUCT_PAGE_SIZE = 10

class ProductPagingSource(private val repository: ProductRepository) : PagingSource<Int, Product>() {


    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Product> {
        return try {
            val currentPage = params.key ?: 0
            val products = repository.getProducts(PRODUCT_PAGE_SIZE,currentPage)
            LoadResult.Page(
                data = products.body()?.products ?: emptyList(),
                prevKey = if(currentPage == 0) null else currentPage - PRODUCT_PAGE_SIZE,
                nextKey = if(products.body()?.products?.isEmpty() == true) null else currentPage + PRODUCT_PAGE_SIZE
            )
        }catch (e : Exception){
            return LoadResult.Error(e)
        }

    }


    override fun getRefreshKey(state: PagingState<Int, Product>): Int? {
         return state.anchorPosition
    }
}