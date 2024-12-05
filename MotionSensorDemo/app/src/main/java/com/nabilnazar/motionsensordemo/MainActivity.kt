package com.nabilnazar.motionsensordemo

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.nabilnazar.motionsensordemo.ui.theme.MotionSensorDemoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        setContent {
            MotionSensorDemoTheme {
                StepCounterScreen(sensorManager, sensor)
            }
        }
    }
}

@Composable
fun StepCounterScreen(sensorManager: SensorManager, sensor: Sensor?) {
    var totalSteps by remember { mutableStateOf(0f) }
    var baselineSteps by remember { mutableStateOf(0f) }
    var displaySteps by remember { mutableStateOf(0) }

    DisposableEffect(Unit) {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.values?.get(0)?.let { currentSteps ->
                    if (baselineSteps == 0f) {
                        baselineSteps = currentSteps // Set baseline on first sensor change
                    }
                    totalSteps = currentSteps
                    displaySteps = (totalSteps - baselineSteps).toInt() // Calculate steps since reset
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // No-op
            }
        }

        sensor?.let {
            sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_UI)
        }

        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    // UI Layout
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Step Count: $displaySteps")
                Button(onClick = {
                    // Reset baseline steps
                    baselineSteps = totalSteps
                    displaySteps = 0 // Immediately reflect reset in the UI
                }) {
                    Text("Reset Step Counter")
                }
            }
        }
    }
}
