package com.nabilnazar.deck77material3allcomponents

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nabilnazar.deck77material3allcomponents.ui.theme.Deck77Material3AllComponentsTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Deck77Material3AllComponentsTheme {
                MainScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                Text("Navigation Drawer", modifier = Modifier.padding(16.dp))
                NavigationDrawerItem(label = { Text("Components") }, selected = false, onClick = {})
            }
        },
        drawerState = drawerState
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Material3 Components") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = true,
                        onClick = {},
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home") }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = {},
                        icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                        label = { Text("Settings") }
                    )
                }
            }
        ) { innerPadding ->
            Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                SampleButtons()
                SampleTextFields()
                SampleCheckbox()
                SampleSwitch()
                SampleSlider()
                SampleTopAppBar()
                SampleBottomAppBar()
                SampleNavigationRail()
                SampleBadge()
                SampleChips()
                SampleProgressIndicator()
                SampleRadioButton()
                SampleSearch()
                //SampleDialogs()
                SampleSnackBar()
                SampleTabs()
               // SampleTooltips()
            }
        }
    }
}

@Composable
fun SampleButtons() {
    Button(onClick = {}) { Text("Button") }
    Spacer(modifier = Modifier.height(8.dp))
    ElevatedButton(onClick = {}) { Text("Elevated Button") }
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedButton(onClick = {}) { Text("Outlined Button") }
}

@Composable
fun SampleTextFields() {
    TextField(value = "", onValueChange = {}, label = { Text("Text Field") })
}

@Composable
fun SampleCheckbox() {
    Checkbox(checked = false, onCheckedChange = {})
}

@Composable
fun SampleSwitch() {
    Switch(checked = false, onCheckedChange = {})
}

@Composable
fun SampleSlider() {
    Slider(value = 0.5f, onValueChange = {})
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SampleTopAppBar() {
    CenterAlignedTopAppBar(title = { Text("Top App Bar") })
}

@Composable
fun SampleBottomAppBar() {
    BottomAppBar { Text("Bottom App Bar") }
}

@Composable
fun SampleNavigationRail() {
    NavigationRail {
        NavigationRailItem(selected = true, onClick = {}, icon = { Icon(Icons.Default.Home, contentDescription = "Home") })
    }
}

@Composable
fun SampleBadge() {
    BadgedBox(badge = { Badge { Text("1") } }) {
        Icon(Icons.Default.Notifications, contentDescription = "Notifications")
    }
}

@Composable
fun SampleChips() {
    AssistChip(onClick = {}, label = { Text("Assist Chip") })
}

@Composable
fun SampleProgressIndicator() {
    CircularProgressIndicator()
}

@Composable
fun SampleRadioButton() {
    RadioButton(selected = true, onClick = {})
}

@Composable
fun SampleSearch() {
    TextField(value = "Search", onValueChange = {}, label = { Text("Search") })
}

@Composable
fun SampleDialogs() {
    AlertDialog(onDismissRequest = {}, confirmButton = { Button(onClick = {}) { Text("OK") } })
}

@Composable
fun SampleSnackBar() {
    Snackbar { Text("Snackbar Message") }
}

@Composable
fun SampleTabs() {
    TabRow(selectedTabIndex = 0) { Tab(selected = true, onClick = {}, text = { Text("Tab 1") }) }
}

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun SampleTooltips() {
//    TooltipBox(
//        tooltip = { Text("Tooltip") },
//        positionProvider = TODO(),
//        state = TODO(),
//        modifier = TODO(),
//        focusable = TODO(),
//        enableUserInput = TODO()
//    ) { Text("Hover me") }
//}

