package org.nabilnazar.kmmproject

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            CalculatorScreen(calculator = Calculator())
        }
    }
}


@Composable
fun CalculatorScreen(calculator: Calculator) {
    var input1 by remember { mutableStateOf("") }
    var input2 by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TextField(
            value = input1,
            onValueChange = { input1 = it },
            label = { Text("Input 1") }
        )
        TextField(
            value = input2,
            onValueChange = { input2 = it },
            label = { Text("Input 2") }
        )
        Button(onClick = {
            val a = input1.toDoubleOrNull() ?: 0.0
            val b = input2.toDoubleOrNull() ?: 0.0
            result = "Result: ${calculator.add(a, b)}"
        }) {
            Text("Add")
        }
        Text(result)
    }
}