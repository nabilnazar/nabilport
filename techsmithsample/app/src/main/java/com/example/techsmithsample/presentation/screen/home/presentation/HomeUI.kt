package com.example.techsmithsample.presentation.screen.home.presentation

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.techsmithsample.core.presentation.composable.DrawErrorMessage
import com.example.techsmithsample.data.constants.Constants
import com.example.techsmithsample.data.constants.Keys
import com.example.techsmithsample.presentation.navigation.Screen
import com.example.techsmithsample.presentation.screen.home.viewmodel.HomeViewModel
import kotlinx.coroutines.launch



@Composable
fun HomeUI(
    navController: NavController = rememberNavController(), homeViewModel: HomeViewModel
) {


    GetHomeData(viewModel = homeViewModel)
    Scaffold(containerColor = MaterialTheme.colorScheme.surface) {
        Column(modifier = Modifier.padding(it)) {
            DrawHomeHeader(cartCount = homeViewModel.cartCountState,
                onClickCart = { navController.navigate(Screen.CartUI.route) })
            DrawContent(
                navController = navController, homeViewModel = homeViewModel
            )
        }
    }
}


/**
 * Getting data for display in the home screen.
 */
@Composable
private fun GetHomeData(viewModel: HomeViewModel) {
    LaunchedEffect(Unit) {
        viewModel.fetchProducts()
        viewModel.getCartCount()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DrawContent(
    homeViewModel: HomeViewModel, navController: NavController
) {

    val productPagingItem = homeViewModel.productsState.collectAsLazyPagingItems()
    var isRefreshing by remember { mutableStateOf(false) }
    val state = rememberPullToRefreshState()
    val coroutineScope = rememberCoroutineScope()
    val onRefresh: () -> Unit = {
        isRefreshing = true
        coroutineScope.launch {
            productPagingItem.refresh()
            homeViewModel.fetchProducts()
            isRefreshing = false
        }

    }

    Scaffold(
        modifier = Modifier.pullToRefresh(
            state = state, isRefreshing = isRefreshing, onRefresh = onRefresh
        )
    ) { innerPadding ->

        val scaleFraction = {
            if (isRefreshing) 1f
            else LinearOutSlowInEasing.transform(state.distanceFraction).coerceIn(0f, 1f)
        }

            PullToRefreshBox(state = state,
                isRefreshing = isRefreshing,
                onRefresh = onRefresh,
                indicator = {
                    PullToRefreshDefaults.Indicator(
                        state = state,
                        isRefreshing = isRefreshing,
                        modifier = Modifier.align(Alignment.TopCenter)
                            .graphicsLayer(scaleX = scaleFraction(), scaleY = scaleFraction())
                    )
                },
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = innerPadding,
                ) {
                    if (!isRefreshing) {
                        items(productPagingItem.itemCount) { index ->
                            productPagingItem[index]?.let { product ->
                                DrawProductCard(product = product) {
                                    navController.navigate(Screen.ProductDetailUI.route.plus("?${Keys.KEY_PRODUCT_ID}=${product.id ?: Constants.INVALID_ID}"))
                                }
                            }
                        }

                        productPagingItem.apply {
                            when {
                                loadState.refresh is LoadState.Loading -> {
                                    item(span = { GridItemSpan(2) }) {
                                        DrawPageLoader(modifier = Modifier)
                                    }
                                }

                                loadState.refresh is LoadState.Error -> {
                                    val error =
                                        productPagingItem.loadState.refresh as LoadState.Error
                                    item(span = { GridItemSpan(2) }) {
                                        DrawErrorMessage(modifier = Modifier,
                                            message = error.error.localizedMessage!!,
                                            onClickRetry = { retry() })
                                    }
                                }

                                loadState.append is LoadState.Loading -> {
                                    item(span = { GridItemSpan(2) }) {
                                        DrawLoadingNextPageItem(
                                            modifier = Modifier
                                        )
                                    }
                                }

                                loadState.append is LoadState.Error -> {
                                    val error =
                                        productPagingItem.loadState.append as LoadState.Error
                                    item(span = { GridItemSpan(2) }) {
                                        DrawErrorMessage(modifier = Modifier,
                                            message = error.error.localizedMessage!!,
                                            onClickRetry = { retry() })
                                    }
                                }
                            }
                        }

                        item { Spacer(modifier = Modifier.padding(4.dp)) }
                    }
                }
            }

    }
}


