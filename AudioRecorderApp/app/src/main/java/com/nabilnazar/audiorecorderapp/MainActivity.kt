package com.nabilnazar.audiorecorderapp


import android.content.Intent

import android.os.Bundle
import android.os.PersistableBundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.nabilnazar.audiorecorderapp.ui.theme.AudioRecorderAppTheme

class MainActivity : ComponentActivity() {


        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            enableEdgeToEdge()
            setContent {
                AudioRecorderAppTheme {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        AudioRecorderScreen()
                    }
                }
            }
        }
    }


@Composable
fun AudioRecorderScreen() {
    val isRecording = remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (isRecording.value) "Recording..." else "Tap to Record",
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 20.dp)
        )
        Button(
            onClick = {
                if (isRecording.value) {
                    // Stop Recording
                    Intent(context, AudioRecordingService::class.java).also { intent ->
                        intent.action = "STOP_RECORDING"
                        ContextCompat.startForegroundService(context, intent)
                    }
                } else {
                    // Start Recording
                    Intent(context, AudioRecordingService::class.java).also { intent ->
                        intent.action = "START_RECORDING"
                        ContextCompat.startForegroundService(context, intent)
                    }
                }
                isRecording.value = !isRecording.value
            },
            shape = CircleShape,
            modifier = Modifier.size(100.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        ) {
            Icon(
                painter = painterResource(
                    if (isRecording.value) android.R.drawable.ic_media_pause
                    else android.R.drawable.ic_btn_speak_now
                ),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(50.dp)
            )
        }
    }
}
