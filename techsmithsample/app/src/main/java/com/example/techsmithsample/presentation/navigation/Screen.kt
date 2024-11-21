package com.example.techsmithsample.presentation.navigation

import okhttp3.Route

sealed class Screen(val route: String){
    object HomeUI : Screen("home_ui")
    object ProductDetailUI : Screen("product_detail_ui")
    object CartUI : Screen("cart_ui")

}