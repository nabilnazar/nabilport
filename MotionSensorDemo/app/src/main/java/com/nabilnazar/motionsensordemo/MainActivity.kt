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
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class MainActivity : ComponentActivity() {

    private val sensorManager by lazy {
        getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    private val sensor: Sensor? by lazy {
        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MotionSensorDemoTheme {
                if (sensor == null) {
                    Text(text = "Step counter sensor is not present on this device")
                } else {
                    StepCounterScreen(sensorManager, sensor!!)
                }
            }
        }
    }
}

// Helper function to create a Flow from the step counter sensor
fun SensorManager.stepCounterFlow(sensor: Sensor): Flow<Float> = callbackFlow {
    val listener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            event?.values?.get(0)?.let { trySend(it).isSuccess }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL)
    awaitClose { unregisterListener(listener) }
}

@Composable
fun StepCounterScreen(sensorManager: SensorManager, sensor: Sensor) {
    var totalSteps by remember { mutableStateOf(0f) }
    var baselineSteps by remember { mutableStateOf(0f) }
    var displaySteps by remember { mutableStateOf(0) }

    // Collect step data using Flow
    LaunchedEffect(sensor) {
        sensorManager.stepCounterFlow(sensor).collect { currentSteps ->
            if (baselineSteps == 0f) baselineSteps = currentSteps
            totalSteps = currentSteps
            displaySteps = (totalSteps - baselineSteps).toInt()
        }
    }

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
                    baselineSteps = totalSteps
                    displaySteps = 0 // Reset display steps
                }) {
                    Text("Reset Step Counter")
                }
            }
        }
    }
}
