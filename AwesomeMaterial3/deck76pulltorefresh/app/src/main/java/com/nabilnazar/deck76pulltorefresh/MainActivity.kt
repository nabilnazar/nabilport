package com.nabilnazar.deck76pulltorefresh

import DogViewModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nabilnazar.deck76pulltorefresh.ui.theme.Deck76pulltorefreshTheme
import kotlinx.serialization.Serializable

@Serializable
data class DogResponse(val data: List<Dog>)

@Serializable
data class Dog(
    val id: String,
    val type: String,
    val attributes: DogAttributes
)

@Serializable
data class DogAttributes(
    val name: String,
    val description: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DogListScreen(viewModel: DogViewModel) {
    val dogs by remember { derivedStateOf { viewModel.dogs } }
    val isRefreshing by remember { derivedStateOf { viewModel.isRefreshing } }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Dog Breeds") }) }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.fetchDogs() },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                if (dogs.isEmpty()) {
                    item {
                        Text(
                            text = "No data available",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                } else {
                    items(dogs.size) { dog ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = dogs[dog].attributes.name,
                                    style = MaterialTheme.typography.headlineSmall
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = dogs[dog].attributes.description,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}




class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Deck76pulltorefreshTheme {
                val viewModel = remember { DogViewModel() }
                DogListScreen(viewModel)
            }
        }
    }
}
