package com.nabilnazar.deck57benchmarkingcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.nabilnazar.deck57benchmarkingcompose.ui.theme.Deck57benchmarkingComposeTheme
import kotlin.system.measureNanoTime

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Deck57benchmarkingComposeTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        BenchmarkingPrototype()
                    }
                }
            }
        }
    }
}

@Composable
fun BenchmarkingPrototype() {
    var recompositionCount by remember { mutableStateOf(0) }
    var renderTime by remember { mutableStateOf(0L) }

    // Track recompositions
    LaunchedEffect(Unit) {
        recompositionCount++
    }

    // Sample content: dynamic list and input field
    val items = remember { (1..100).map { "Item $it" } }
    var searchText by remember { mutableStateOf("") }
    val filteredItems = items.filter { it.contains(searchText, ignoreCase = true) }

    // Persistent state for switches
    val switchStates = remember { mutableStateMapOf<String, Boolean>() }

    // Measure rendering time for the composable
    val renderTimeNano = measureNanoTime {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            // Input field to filter the list
            BasicTextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.LightGray, shape = MaterialTheme.shapes.small)
                    .padding(8.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            // LazyColumn with persistent switch states
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(filteredItems.size) { index ->
                    val item = filteredItems[index]
                    val isChecked = switchStates[item] == true

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .background(Color.White)
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = item, modifier = Modifier.weight(1f))
                        Switch(
                            checked = isChecked,
                            onCheckedChange = { checked ->
                                switchStates[item] = checked // Update state map
                                recompositionCount++ // Increment recomposition count
                            }
                        )
                    }
                }
            }
        }
    }
    renderTime = renderTimeNano / 1_000_000 // Render time in ms

    // Overlay to display benchmarking data
    PerformanceMetricsOverlay(recompositionCount, renderTime)
}

@Composable
fun PerformanceMetricsOverlay(recompositionCount: Int, renderTime: Long) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.7f))
            .padding(8.dp)
    ) {
        Text(
            text = "Recomposition Count: $recompositionCount",
            color = Color.White,
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = "Render Time: $renderTime ms",
            color = Color.White,
            style = MaterialTheme.typography.bodySmall
        )
    }
}
