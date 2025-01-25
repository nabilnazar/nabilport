package com.nabilnazar.deck69coroutineexplorer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nabilnazar.deck69coroutineexplorer.ui.theme.Deck69coroutineExplorerTheme
import com.nabilnazar.deck69coroutineexplorer.usecases.CoroutineUseCase
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Deck69coroutineExplorerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CoroutineDemoScreen(Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun CoroutineDemoScreen(modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()

    // State variables for results
    var backgroundTaskResult by remember { mutableStateOf("") }
    var parallelTaskResult by remember { mutableStateOf("") }
    var errorHandlingResult by remember { mutableStateOf("") }
    var cancellationState by remember { mutableStateOf("Start Cancellation Demo") }
    var stateFlowValue by remember { mutableStateOf("Loading StateFlow...") }
    var sharedFlowValue by remember { mutableStateOf("Waiting for SharedFlow...") }

    // Cancellation demo
    val (job, stateFlow) = remember { CoroutineUseCase.cancellationDemo(scope) }

    // Launch StateFlow collector
    LaunchedEffect(stateFlow) {
        stateFlow.collect { cancellationState = it }
    }

    // Launch SharedFlow collector
    val sharedFlow = remember { CoroutineUseCase.sharedFlowDemo(scope) }
    LaunchedEffect(sharedFlow) {
        sharedFlow.collect { sharedFlowValue = it }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Button(onClick = {
                backgroundTaskResult = CoroutineUseCase.performBackgroundTask()
            }) {
                BasicText("Run Background Task")
            }
            BasicText("Result: $backgroundTaskResult")
        }
        item {
            Button(onClick = {
                scope.launch {
                    parallelTaskResult = CoroutineUseCase.parallelTasks().joinToString(", ")
                }
            }) {
                BasicText("Run Parallel Tasks")
            }
            BasicText("Result: $parallelTaskResult")
        }
        item {
            Button(onClick = {
                scope.launch {
                    errorHandlingResult = CoroutineUseCase.errorHandlingDemo()
                }
            }) {
                BasicText("Run Error Handling Demo")
            }
            BasicText("Result: $errorHandlingResult")
        }
        item {
            Button(onClick = { job.cancel() }) {
                BasicText(cancellationState)
            }
        }
        item {
            Button(onClick = {
                scope.launch {
                    CoroutineUseCase.stateFlowDemo().collect { stateFlowValue = it }
                }
            }) {
                BasicText("StateFlow Demo")
            }
            BasicText("StateFlow Result: $stateFlowValue")
        }
        item {
            BasicText("SharedFlow Result: $sharedFlowValue")
        }
    }
}




