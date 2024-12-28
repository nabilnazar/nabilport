package com.nabilnazar.deck37healthconnect

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.nabilnazar.deck37healthconnect.ui.theme.Deck37healthconnectTheme
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import kotlinx.coroutines.launch
import java.time.Instant

import androidx.activity.result.ActivityResultLauncher
import androidx.compose.ui.Alignment
import androidx.health.connect.client.records.HeartRateRecord


//todo :
// 1. blood pressure
// 2. add other 100+ or all metrics in health data
// 3. make theme fitness oriented
// 4. range based alert feature, like blood pressure




class MainActivity : ComponentActivity() {

    private lateinit var healthConnectClient: HealthConnectClient
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

    private val healthPermissions = arrayOf(
        "android.permission.health.READ_STEPS",
        "android.permission.health.WRITE_STEPS"
    )

    private var permissionsDenied by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the permission launcher
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (permissions.all { it.value }) {
                permissionsDenied = false
                initializeHealthConnectClient()
                setupContent()
            } else {
                permissionsDenied = true
            }
        }

        // Check permissions and proceed
        if (!hasPermissions()) {
            permissionLauncher.launch(healthPermissions)
        } else {
            initializeHealthConnectClient()
            setupContent()
        }

        // Handle content setup based on permissions state
        setContent {
            Deck37healthconnectTheme {
                if (permissionsDenied) {
                    ShowPermissionDeniedUI()
                } else {
                    Scaffold { innerPadding ->
                        HealthDashboard(
                            healthConnectClient = healthConnectClient,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }

    private fun hasPermissions(): Boolean {
        return healthPermissions.all { permission ->
            ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun initializeHealthConnectClient() {
        try {
            healthConnectClient = HealthConnectClient.getOrCreate(this)
        } catch (e: Exception) {
            e.printStackTrace()
            showInitializationError(e.message.orEmpty())
        }
    }

    @Composable
    private fun ShowPermissionDeniedUI() {
        Scaffold {it ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Permissions denied. Please grant Health Connect permissions in app settings.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Button(onClick = {
                    permissionLauncher.launch(healthPermissions)
                }) {
                    Text("Grant Permissions")
                }
            }
        }
    }

    private fun setupContent() {
        setContent {
            Deck37healthconnectTheme {
                Scaffold { innerPadding ->
                    HealthDashboard(
                        healthConnectClient = healthConnectClient,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    private fun showInitializationError(errorMessage: String) {
        setContent {
            Deck37healthconnectTheme {
                Scaffold {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Error initializing Health Connect: $errorMessage",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HealthDashboard(healthConnectClient: HealthConnectClient, modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    var stepsData by remember { mutableStateOf<List<StepsRecord>>(emptyList()) }
    var heartRateData by remember { mutableStateOf<List<HeartRateRecord>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Fetch data on launch
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val timeRange = TimeRangeFilter.between(
                    startTime = Instant.now().minusSeconds(7 * 24 * 60 * 60), // Last 7 days
                    endTime = Instant.now()
                )

                // Fetch steps data
                val stepsRequest = ReadRecordsRequest(
                    recordType = StepsRecord::class,
                    timeRangeFilter = timeRange
                )
                stepsData = healthConnectClient.readRecords(stepsRequest).records

                // Fetch heart rate data
                val heartRateRequest = ReadRecordsRequest(
                    recordType = HeartRateRecord::class,
                    timeRangeFilter = timeRange
                )
                heartRateData = healthConnectClient.readRecords(heartRateRequest).records
            } catch (e: Exception) {
                errorMessage = e.message
            }
        }
    }

    // UI
    if (errorMessage != null) {
        Text(
            text = "Error: ${errorMessage}",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyLarge
        )
    } else {
        LazyColumn(modifier = modifier.padding(16.dp)) {
            item {
                Text(
                    text = "Health Dashboard",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            if (stepsData.isEmpty() && heartRateData.isEmpty()) {
                item {
                    Text(
                        text = "No data available.",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
            } else {
                item {
                    StatisticsSection(stepsData, heartRateData)
                }
                item {
                    StepsSection(stepsData)
                }
                item {
                    HeartRateSection(heartRateData)
                }
            }
        }
    }
}


@Composable
fun StatisticsSection(stepsData: List<StepsRecord>, heartRateData: List<HeartRateRecord>) {
    val totalSteps = stepsData.sumOf { it.count }
    val averageSteps = if (stepsData.isNotEmpty()) totalSteps / stepsData.size else 0
    val averageHeartRate = if (heartRateData.isNotEmpty()) {
        heartRateData.flatMap { it.samples.map { sample -> sample.beatsPerMinute } }.average()
    } else {
        null
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Summary",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Total Steps: $totalSteps",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Average Steps per Day: $averageSteps",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Average Heart Rate: ${averageHeartRate?.let { "%.2f bpm".format(it) } ?: "No data"}",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}


@Composable
fun StepsSection(stepsData: List<StepsRecord>) {
    if (stepsData.isEmpty()) {
        Text(
            text = "No steps recorded.",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
    } else {
        Text(
            text = "Steps Data",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        stepsData.forEach { record ->
            HealthRecordCard(
                title = "Steps",
                value = "${record.count} steps",
                timestamp = record.startTime.toString()
            )
        }
    }
}

@Composable
fun HeartRateSection(heartRateData: List<HeartRateRecord>) {
    if (heartRateData.isEmpty()) {
        Text(
            text = "No heart rate data recorded.",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
    } else {
        Text(
            text = "Heart Rate Data",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        heartRateData.forEach { record ->
            val averageHeartRate = if (record.samples.isNotEmpty()) {
                record.samples.sumOf { it.beatsPerMinute.toInt() } / record.samples.size
            } else {
                null
            }
            HealthRecordCard(
                title = "Heart Rate",
                value = averageHeartRate?.let { "$it bpm" } ?: "No valid heart rate samples",
                timestamp = record.startTime.toString()
            )
        }
    }
}


@Composable
fun HealthRecordCard(title: String, value: String, timestamp: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Timestamp: $timestamp",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
