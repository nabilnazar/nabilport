package com.nabilnazar.jetkoinapp

 import android.os.Bundle
 import androidx.activity.ComponentActivity
 import androidx.activity.compose.setContent
 import androidx.activity.enableEdgeToEdge
 import androidx.compose.foundation.layout.fillMaxSize
 import androidx.compose.foundation.layout.padding
 import androidx.compose.material.icons.Icons
 import androidx.compose.material.icons.filled.Add
 import androidx.compose.material3.FabPosition
 import androidx.compose.material3.FloatingActionButton
 import androidx.compose.material3.Icon
 import androidx.compose.material3.Scaffold
 import androidx.compose.material3.Surface
 import androidx.compose.runtime.Composable
 import androidx.compose.runtime.collectAsState
 import androidx.compose.runtime.getValue
 import androidx.compose.runtime.mutableStateOf
 import androidx.compose.runtime.remember
 import androidx.compose.ui.Modifier
 import androidx.navigation.compose.NavHost
 import androidx.navigation.compose.composable
 import androidx.navigation.compose.currentBackStackEntryAsState
 import androidx.navigation.compose.rememberNavController
 import com.nabilnazar.jetkoinapp.screens.HomeScreen
 import com.nabilnazar.jetkoinapp.screens.ProfileScreen
 import com.nabilnazar.jetkoinapp.screens.SettingsScreen
 import com.nabilnazar.jetkoinapp.ui.theme.JetKoinAppTheme
 import com.nabilnazar.jetkoinapp.utility.AddNoteDialog
 import com.nabilnazar.jetkoinapp.utility.BottomNavigationBar
 import org.koin.compose.viewmodel.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JetKoinAppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    NavApp()
                }
            }
        }
    }
}


@Composable
fun NavApp(viewModel: NoteViewModel = koinViewModel()) {
    val notes by viewModel.allNotes.collectAsState(initial = emptyList())
    val navController = rememberNavController()
    val showDialog = remember { mutableStateOf(false) }

    if (showDialog.value) {
        AddNoteDialog(
            onDismiss = { showDialog.value = false }
        ) { title, content ->
            viewModel.addNote(Note(title = title, content = content))
            showDialog.value = false
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { BottomNavigationBar(navController) },
        floatingActionButton = {
            val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
            if (currentRoute == "Home") {
                FloatingActionButton(onClick = { showDialog.value = true }) {
                    Icon(Icons.Filled.Add, contentDescription = "Add")
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "Home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("Home") { HomeScreen(notes, viewModel) }
            composable("Profile") { ProfileScreen() }
            composable("Settings") { SettingsScreen() }
        }
    }
}