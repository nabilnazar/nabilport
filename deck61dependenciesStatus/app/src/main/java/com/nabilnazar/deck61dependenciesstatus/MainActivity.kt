package com.nabilnazar.deck61dependenciesstatus

import android.annotation.SuppressLint
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nabilnazar.deck61dependenciesstatus.ui.theme.Deck61dependenciesStatusTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Deck61dependenciesStatusTheme {
                DependencyStatusApp { getInstalledDependencies() }
            }
        }
    }


    private fun getInstalledDependencies(): List<DependencyStatus> {
        val packageManager = packageManager
        val installedApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        return installedApps.map { appInfo ->
            DependencyStatus(
                name = appInfo.loadLabel(packageManager).toString(),
                isActive = (appInfo.flags and ApplicationInfo.FLAG_STOPPED == 0)
            )
        }
    }
}

@Composable
fun DependencyStatusApp(fetchDependencies: () -> List<DependencyStatus>) {
    val dependencies = remember { mutableStateOf<List<DependencyStatus>>(emptyList()) }

    LaunchedEffect(Unit) {
        dependencies.value = fetchDependencies()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { AppTopBar() }
    ) { innerPadding ->
        DependencyStatusScreen(
            dependencies = dependencies.value,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar() {
    TopAppBar(
        title = { Text(text = "Dependency Status") },
        colors = TopAppBarDefaults.mediumTopAppBarColors()
    )
}

@Composable
fun DependencyStatusScreen(dependencies: List<DependencyStatus>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(dependencies.size) { index ->
            DependencyStatusCard(dependency = dependencies[index])
        }
    }
}

@Composable
fun DependencyStatusCard(dependency: DependencyStatus) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (dependency.isActive) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = dependency.name, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = if (dependency.isActive) "Active" else "Inactive",
                color = if (dependency.isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

data class DependencyStatus(val name: String, val isActive: Boolean)

@Preview(showBackground = true)
@Composable
fun DependencyStatusScreenPreview() {
    Deck61dependenciesStatusTheme {
        DependencyStatusScreen(
            dependencies = listOf(
                DependencyStatus("Retrofit", true),
                DependencyStatus("Room", false),
                DependencyStatus("Coroutine", true)
            )
        )
    }
}
