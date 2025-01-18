package com.nabilnazar.deck58nestednavigationincompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.twotone.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nabilnazar.deck58nestednavigationincompose.ui.theme.Deck58nestedNavigationinComposeTheme

sealed class Screen(val route: String, val resourceId: Int, val icon: ImageVector) {
    object Home : Screen("home", R.string.home, Icons.Filled.Home)
    object Search : Screen("search", R.string.search, Icons.Outlined.Search)
    object Favorites : Screen("favorites", R.string.favorites, Icons.TwoTone.Favorite)
    object Profile : Screen("profile", R.string.profile, Icons.Default.Person)

    object HomeDetail : Screen("home/detail", R.string.home_detail, Icons.Rounded.Info)
    object HomeDetailInfo : Screen("home/detail/info", R.string.home_detail_info, Icons.Default.Info)

    object SearchDetail : Screen("search/detail", R.string.search_detail, Icons.Outlined.Search)
    object SearchResult : Screen("search/result", R.string.search_result, Icons.AutoMirrored.Default.Send)
}

@Composable
fun HomeScreen() {
    val homeNavController = rememberNavController()
    NavHost(
        navController = homeNavController,
        startDestination = Screen.HomeDetail.route
    ) {
        composable(Screen.HomeDetail.route) { HomeDetailScreen(homeNavController) }
        composable(Screen.HomeDetailInfo.route) { HomeDetailInfoScreen() }
    }
}

@Composable
fun HomeDetailScreen(navController: NavHostController) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Button(onClick = { navController.navigate(Screen.HomeDetailInfo.route) }) {
            Text("Go to Detail Info")
        }
    }
}

@Composable
fun HomeDetailInfoScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Home Detail Info Screen")
    }
}

@Composable
fun SearchScreen() {
    val searchNavController = rememberNavController()
    NavHost(
        navController = searchNavController,
        startDestination = Screen.SearchDetail.route
    ) {
        composable(Screen.SearchDetail.route) { SearchDetailScreen(searchNavController) }
        composable(Screen.SearchResult.route) { SearchResultScreen() }
    }
}

@Composable
fun SearchDetailScreen(navController: NavHostController) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Button(onClick = { navController.navigate(Screen.SearchResult.route) }) {
            Text("Go to Search Result")
        }
    }
}

@Composable
fun SearchResultScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Search Result Screen")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val items = listOf(Screen.Home, Screen.Search, Screen.Favorites, Screen.Profile)

    Scaffold(
        bottomBar = {
            BottomAppBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(stringResource(id = screen.resourceId)) },
                        selected = currentDestination?.route == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController,
            startDestination = Screen.Home.route,
            Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) { HomeScreen() }
            composable(Screen.Search.route) { SearchScreen() }
            composable(Screen.Favorites.route) { CenteredText("Favorites Screen") }
            composable(Screen.Profile.route) { CenteredText("Profile Screen") }
        }
    }
}

@Composable
fun CenteredText(text: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text)
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Deck58nestedNavigationinComposeTheme {
                MainScreen()
            }
        }
    }
}
