package com.nabilnazar.deck49jetpackmultiplayergame

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import com.nabilnazar.deck49jetpackmultiplayergame.ui.theme.Deck49jetpackmultiplayergameTheme

class MainActivity : ComponentActivity() {

    private val serviceId = "com.nabilnazar.deck49jetpackmultiplayergame.SERVICE_ID"
    private lateinit var connectionClient: ConnectionsClient

    private val requiredPermissions = mutableListOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ).apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            add(Manifest.permission.BLUETOOTH_ADVERTISE)
            add(Manifest.permission.BLUETOOTH_CONNECT)
            add(Manifest.permission.BLUETOOTH_SCAN)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.NEARBY_WIFI_DEVICES)
        }
    }.toTypedArray()

    private val permissionRequestLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val allGranted = permissions.entries.all { it.value }
            if (allGranted) {
                startAdvertising()
                startDiscovery()
            } else {
                showToast("Permissions are required for Nearby Connections to work")
            }
        }

    private var connectedEndpointId: String? = null
    private var receivedMessage by mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        connectionClient = Nearby.getConnectionsClient(this)

        setContent {
            Deck49jetpackmultiplayergameTheme {
                var messageToSend by remember { mutableStateOf(TextFieldValue("")) }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Nearby Multiplayer Game",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        BasicTextField(
                            value = messageToSend,
                            onValueChange = { messageToSend = it },
                            textStyle = TextStyle(
                            color = if (isSystemInDarkTheme()) Color.White else Color.Black ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            decorationBox = { innerTextField ->
                                Box(modifier = Modifier.padding(4.dp)) {
                                    if (messageToSend.text.isEmpty()) {
                                        Text(text = "Enter a message to send")
                                    }
                                    innerTextField()
                                }
                            }
                        )
                        Button(
                            onClick = {
                                connectedEndpointId?.let {
                                    val payload = Payload.fromBytes(messageToSend.text.toByteArray())
                                    connectionClient.sendPayload(it, payload)
                                    messageToSend = TextFieldValue("")
                                } ?: showToast("No connected endpoint")
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Send Message")
                        }
                        Text(text = "Received Message: $receivedMessage")
                    }
                }
            }
        }

        if (arePermissionsGranted()) {
            startAdvertising()
            startDiscovery()
        } else {
            permissionRequestLauncher.launch(requiredPermissions)
        }
    }

    private fun arePermissionsGranted(): Boolean {
        return requiredPermissions.all { permission ->
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun startAdvertising() {
        val advertisingOptions = AdvertisingOptions.Builder()
            .setStrategy(Strategy.P2P_STAR)
            .build()

        connectionClient.startAdvertising(
            "Player",
            serviceId,
            connectionLifecycleCallback,
            advertisingOptions
        ).addOnSuccessListener {
            showToast("Advertising started")
        }.addOnFailureListener {
            showToast("Advertising failed: ${it.message}")
            Log.d("Advertising", "Advertising failed: ${it.message}")
        }
    }

    private fun startDiscovery() {
        val discoveryOptions = DiscoveryOptions.Builder()
            .setStrategy(Strategy.P2P_STAR)
            .build()

        connectionClient.startDiscovery(
            serviceId,
            endpointDiscoveryCallback,
            discoveryOptions
        ).addOnSuccessListener {
            showToast("Discovery started")
        }.addOnFailureListener {
            showToast("Discovery failed: ${it.message}")
            Log.d("Discovery", "Discovery failed: ${it.message}")
        }
    }

    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
            connectionClient.acceptConnection(endpointId, payloadCallback)
            showToast("Connection initiated with ${connectionInfo.endpointName}")
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            when (result.status.statusCode) {
                ConnectionsStatusCodes.STATUS_OK -> {
                    connectedEndpointId = endpointId
                    showToast("Connected to $endpointId")
                }
                ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> showToast("Connection rejected")
                ConnectionsStatusCodes.STATUS_ERROR -> showToast("Connection error")
            }
        }

        override fun onDisconnected(endpointId: String) {
            connectedEndpointId = null
            showToast("Disconnected from $endpointId")
        }
    }

    private val endpointDiscoveryCallback = object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
            showToast("Endpoint found: ${info.endpointName}")
            connectionClient.requestConnection("Player", endpointId, connectionLifecycleCallback)
        }

        override fun onEndpointLost(endpointId: String) {
            showToast("Endpoint lost: $endpointId")
        }
    }

    private val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            if (payload.asBytes() != null) {
                val message = String(payload.asBytes()!!)
                runOnUiThread { receivedMessage = message }
                showToast("Message received: $message")
            }
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
            if (update.status == PayloadTransferUpdate.Status.SUCCESS) {
                showToast("Payload transfer complete")
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
