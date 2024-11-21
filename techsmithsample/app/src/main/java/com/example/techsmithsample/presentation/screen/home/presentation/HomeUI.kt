package com.example.techsmithsample.presentation.screen.home.presentation

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.techsmithsample.presentation.navigation.Screen
import com.example.techsmithsample.presentation.screen.home.viewmodel.HomeViewModel

/**
 * Home Screen
 * @param navController
 * @param homeViewModel
 */
@Composable
fun HomeUI(
    navController: NavController = rememberNavController(),
    homeViewModel: HomeViewModel
) {
    DrawRootView(
        navController = navController, homeViewModel = homeViewModel
    )
}

/**
 * Draws root of the Home Screen
 * @param navController
 * @param homeViewModel
 */
@Composable
private fun DrawRootView(
    navController: NavController, homeViewModel: HomeViewModel
) {
    GetHomeData(viewModel = homeViewModel)
    Scaffold(containerColor = MaterialTheme.colorScheme.surface) {
        Column(modifier = Modifier.padding(it)) {
            DrawScreenBody(
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


/***
 * Draws body of the screen.
 * @param homeViewModel view model
 * @param navController Nav controller for the navigation.
 */
@Composable
private fun DrawScreenBody(
    navController: NavController,
    homeViewModel: HomeViewModel
) {
    DrawHomeHeader(
        cartCount = homeViewModel.cartCountState,
        onClickCart = { navController.navigate(Screen.CartUI.route) }
    )
    DrawContent(
        navController = navController,
        homeViewModel = homeViewModel
    )
}

/***
 * Draws content of the screen.
 * @param homeViewModel view model
 * @param navController Nav controller for the navigation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DrawContent(
    homeViewModel: HomeViewModel,
    navController: NavController
) {

    val productPagingItem = homeViewModel.productsState.collectAsLazyPagingItems()
    val state = rememberPullToRefreshState()

    if (state.isAnimating) {
        LaunchedEffect(true) {
            productPagingItem.refresh()
            homeViewModel.fetchProducts()
            state.animateToHidden()
        }
    }

    val scaleFraction = if (state.isAnimating) 1f
    else LinearOutSlowInEasing.transform(state.progress).coerceIn(0f, 1f)

    Box(Modifier.nestedScroll(state.nestedScrollConnection)) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(
                start = 8.dp, top = 16.dp, end = 8.dp
            ),
            content = {
                if (!state.isRefreshing) {
                    items(productPagingItem.itemCount) { index ->
                        productPagingItem[index]?.let {
                            DrawProductCard(product = it) {
                                navController.navigate(Screen.ProductDetailUI.route.plus("?${Keys.KEY_PRODUCT_ID}=${it.id ?: Constants.INVALID_ID}"))
                            }
                        }
                    }
                    productPagingItem.apply {
                        when {
                            loadState.refresh is LoadState.Loading -> {
                                item(span = { GridItemSpan(2) }
                                ) {
                                    DrawPageLoader(modifier = Modifier)
                                }
                            }

                            loadState.refresh is LoadState.Error -> {
                                val error = productPagingItem.loadState.refresh as LoadState.Error
                                item(span = { GridItemSpan(2) }) {
                                    DrawErrorMessage(modifier = Modifier,
                                        message = error.error.localizedMessage!!,
                                        onClickRetry = { retry() }
                                    )
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
                                val error = productPagingItem.loadState.append as LoadState.Error
                                item(span = { GridItemSpan(2) }) {
                                    DrawErrorMessage(modifier = Modifier,
                                        message = error.error.localizedMessage!!,
                                        onClickRetry = { retry() }
                                    )
                                }
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.padding(4.dp)) }
                }
            }
        )
        PullToRefreshContainer(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .graphicsLayer(scaleX = scaleFraction, scaleY = scaleFraction),
            state = state,
        )
    }
}
