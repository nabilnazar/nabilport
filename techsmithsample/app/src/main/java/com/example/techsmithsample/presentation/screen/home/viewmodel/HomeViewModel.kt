package com.example.techsmithsample.presentation.screen.home.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.techsmithsample.data.network.response.Product
import com.example.techsmithsample.domain.usecase.GetCartCountUseCase
import com.example.techsmithsample.domain.usecase.GetProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userCase: GetProductsUseCase,
    private val cartCountUseCase: GetCartCountUseCase
) : ViewModel() {

    private val _productsState: MutableStateFlow<PagingData<Product>> =
        MutableStateFlow(value = PagingData.empty())
    val productsState: MutableStateFlow<PagingData<Product>> get() = _productsState

    private val _cartCountState: MutableState<Int> = mutableStateOf(value = 0)
    val cartCountState: MutableState<Int> get() = _cartCountState

    fun fetchProducts() {
        viewModelScope.launch {
            getProducts()
        }
    }

    fun getCartCount() {
        viewModelScope.launch(Dispatchers.IO) {
            val cartCount = async { cartCountUseCase() }.await()
            withContext(Dispatchers.Main) {
                _cartCountState.value = cartCount
            }
        }
    }

    private suspend fun getProducts() {
        userCase()
            .distinctUntilChanged()
            .cachedIn(viewModelScope)
            .collect {
                _productsState.value = it
            }
    }

}