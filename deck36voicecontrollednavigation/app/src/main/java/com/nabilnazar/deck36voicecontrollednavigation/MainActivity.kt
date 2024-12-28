package com.nabilnazar.deck36voicecontrollednavigation

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nabilnazar.deck36voicecontrollednavigation.ui.theme.Deck36voicecontrollednavigationTheme

class MainActivity : ComponentActivity() {

    private val speechRecognizerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            spokenText?.let { handleVoiceCommand(it) }
        } else {
            Toast.makeText(this, "Voice recognition canceled or failed.", Toast.LENGTH_SHORT).show()
        }
    }

    private var navigateTo: ((String) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Deck36voicecontrollednavigationTheme {
                val navController = rememberNavController()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    floatingActionButton = {
                        FloatingActionButton(onClick = { startVoiceRecognition() }) {
                            Text("🎤")
                        }
                    },
                    bottomBar = {
                        BottomNavigationBar(navController)
                    }
                ) { innerPadding ->
                    NavigationHost(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }

                // Store the navigation callback
                LaunchedEffect(navController) {
                    navigateTo = { destination ->
                        if (navController.currentDestination?.route != destination) {
                            navController.navigate(destination) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                }
            }
        }
    }

    private fun startVoiceRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak a command to navigate (e.g., 'Go to home')")
        }
        speechRecognizerLauncher.launch(intent)
    }

    private fun handleVoiceCommand(command: String) {
        val destination = when {
            command.contains("home", ignoreCase = true) -> "home"
            command.contains("profile", ignoreCase = true) -> "profile"
            command.contains("settings", ignoreCase = true) -> "settings"
            else -> {
                Toast.makeText(this, "Unknown command: $command", Toast.LENGTH_SHORT).show()
                return
            }
        }
        navigateTo?.invoke(destination)
    }
}

@Composable
fun NavigationHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController = navController, startDestination = "home", modifier = modifier) {
        composable("home") { ScreenContent("Home Screen") }
        composable("profile") { ScreenContent("Profile Screen") }
        composable("settings") { ScreenContent("Settings Screen") }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    NavigationBar {
        NavigationBarItem(
            selected = currentRoute == "home",
            onClick = {
                navController.navigate("home") {
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") }
        )
        NavigationBarItem(
            selected = currentRoute == "profile",
            onClick = {
                navController.navigate("profile") {
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile") }
        )
        NavigationBarItem(
            selected = currentRoute == "settings",
            onClick = {
                navController.navigate("settings") {
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
            label = { Text("Settings") }
        )
    }
}

@Composable
fun ScreenContent(title: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = title, style = MaterialTheme.typography.headlineMedium)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewApp() {
    Deck36voicecontrollednavigationTheme {
        val navController = rememberNavController()
        NavigationHost(navController)
    }
}
