package com.nabilnazar.deck46permissionsapp

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PermissionApp()
        }
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionApp() {
    val permissions = listOf(
        Manifest.permission.CALL_PHONE,
        Manifest.permission.SEND_SMS,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.POST_NOTIFICATIONS
    )



    val permissionStates = permissions.map { rememberPermissionState(it) }

    LazyColumn(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Manage Permissions",
                style = MaterialTheme.typography.titleLarge
            )
        }

        items(permissionStates.size) { index ->
            val permissionState = permissionStates[index]
            PermissionRow(permissionState = permissionState)
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionRow(permissionState: PermissionState) {
    val context = LocalContext.current
    val isGranted = permissionState.status.isGranted
    var showRationale by remember { mutableStateOf(false) }

    if (showRationale) {
        AlertDialog(
            onDismissRequest = { showRationale = false },
            title = { Text("Permission Required") },
            text = { Text("This permission is essential for accessing app features.") },
            confirmButton = {
                Button(onClick = {
                    showRationale = false
                    permissionState.launchPermissionRequest()
                }) {
                    Text("Grant Permission")
                }
            },
            dismissButton = {
                Button(onClick = { showRationale = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = permissionState.permission)

        Switch(
            checked = isGranted,
            onCheckedChange = { isChecked ->
                if (isChecked && !isGranted) {
                    if (permissionState.status.shouldShowRationale) {
                        showRationale = true
                    } else {
                        permissionState.launchPermissionRequest()
                    }
                } else if (!isChecked && isGranted) {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                    Toast.makeText(
                        context,
                        "Revoke permissions manually in settings.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // Handle feature usage if already granted
                    when (permissionState.permission) {
                        Manifest.permission.CALL_PHONE -> {
                            val callIntent = Intent(Intent.ACTION_CALL).apply {
                                data = Uri.parse("tel:1234567890")
                            }
                            if (isGranted) context.startActivity(callIntent)
                        }
                        Manifest.permission.SEND_SMS -> {
                            val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("smsto:1234567890")
                                putExtra("sms_body", "Hello from PermissionApp!")
                            }
                            if (isGranted) context.startActivity(smsIntent)
                        }
                        else -> Unit
                    }
                }
            }
        )
    }
}

