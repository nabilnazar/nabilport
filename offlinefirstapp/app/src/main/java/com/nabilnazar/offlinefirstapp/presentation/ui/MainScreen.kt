package com.nabilnazar.offlinefirstapp.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nabilnazar.offlinefirstapp.presentation.viewmodel.MainViewModel


@Composable
fun MainScreen(viewModel: MainViewModel = hiltViewModel()) {
    // Observing calculation history from the ViewModel
    val history by viewModel.calculationHistory.collectAsState()

    var input1 by remember { mutableStateOf("") }
    var input2 by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // First Box: Calculation UI
        TextField(
            value = input1,
            onValueChange = { input1 = it },
            label = { Text("Input 1") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = input2,
            onValueChange = { input2 = it },
            label = { Text("Input 2") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val num1 = input1.toDoubleOrNull() ?: 0.0
                val num2 = input2.toDoubleOrNull() ?: 0.0
                val res = num1 + num2
                result = res.toString()
                viewModel.addCalculation(num1, num2, res)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Calculate")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text("Result: $result", style = MaterialTheme.typography.headlineMedium)

        // Second Box: History UI
        Spacer(modifier = Modifier.height(16.dp))
        Text("Calculation History", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(history.size) { calculation ->
                Text(
                    text = "(${history[calculation].input1}. + ${history[calculation].input2}) = ${history[calculation].result}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}