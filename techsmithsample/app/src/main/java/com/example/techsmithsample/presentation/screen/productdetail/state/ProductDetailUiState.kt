package com.example.techsmithsample.presentation.screen.productdetail.state

import com.example.techsmithsample.core.data.state.BaseData
import com.example.techsmithsample.core.presentation.state.BaseState
import com.example.techsmithsample.data.network.response.Product


class ProductDetailUIState(
    var data: Product = Product(),
    var isLoadCartCount: Boolean = true
) : BaseState()


class GetProductResponseData(
    var data: Product = Product()
) : BaseData()

