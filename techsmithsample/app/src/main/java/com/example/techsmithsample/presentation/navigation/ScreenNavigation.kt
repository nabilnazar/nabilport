package com.example.techsmithsample.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.techsmithsample.presentation.screen.home.presentation.HomeUI
import com.example.techsmithsample.presentation.screen.home.viewmodel.HomeViewModel


@Composable
fun ScreenNavigation(
    navHostController: NavHostController
) {
    NavHost(navController = navHostController, startDestination = Screen.HomeUI.route) {
        composable(route = Screen.HomeUI.route) {
            NavigateToHomeUI(navController = navHostController)
        }

    }
}

@Composable
fun NavigateToHomeUI(navController: NavController) {
    val viewModel = hiltViewModel<HomeViewModel>()
    HomeUI(navController = navController, homeViewModel = viewModel)
}
