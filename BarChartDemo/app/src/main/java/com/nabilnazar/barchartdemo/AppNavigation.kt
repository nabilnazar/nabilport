package com.nabilnazar.barchartdemo

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nabilnazar.barchartdemo.ui.theme.screens.BarGraphScreen
import com.nabilnazar.barchartdemo.ui.theme.screens.DonutChartScreen
import com.nabilnazar.barchartdemo.ui.theme.screens.LinedGraphScreen
import com.nabilnazar.barchartdemo.ui.theme.screens.PieChartScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
     NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(navController) }
        composable("line_graph") { LinedGraphScreen() }
        composable("bar_graph") { BarGraphScreen() }
        composable("pie_chart") { PieChartScreen() }
        composable("donut_chart") { DonutChartScreen() }
    }
}