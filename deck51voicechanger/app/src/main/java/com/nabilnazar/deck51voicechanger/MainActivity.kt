package com.nabilnazar.deck51voicechanger

import android.Manifest
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.arthenica.mobileffmpeg.FFmpeg
import com.nabilnazar.deck51voicechanger.ui.theme.Deck51voicechangerTheme
import java.io.IOException

private const val LOG_TAG = "VoiceChanger"

class MainActivity : ComponentActivity() {

    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null
    private var recordedFilePath: String? = null
    private val permissions = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions.values.all { it }) {
                Log.i(LOG_TAG, "Permissions granted")
            } else {
                Log.e(LOG_TAG, "Permissions denied")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissionLauncher.launch(permissions)

        setContent {
            Deck51voicechangerTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    content = { padding ->
                        MainScreen(
                            startRecording = { startRecording() },
                            stopRecording = { stopRecording() },
                            playAudio = { playAudio() },
                            applyEffect = { effect -> applyAudioEffect(effect) },
                            playProcessedAudio = { playProcessedAudio() },
                            modifier = Modifier.padding(padding)
                        )
                    }
                )
            }
        }
    }

    private fun startRecording() {
        recordedFilePath = "${externalCacheDir?.absolutePath}/audioRecord.mp3"
        recorder = MediaRecorder(this).apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(recordedFilePath)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            try {
                prepare()
                start()
                Log.i(LOG_TAG, "Recording started")
            } catch (e: IOException) {
                Log.e(LOG_TAG, "prepare() failed", e)
            }
        }
    }

    private fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
        Log.i(LOG_TAG, "Recording stopped, file saved at $recordedFilePath")
    }

    private fun playAudio() {
        recordedFilePath?.let {
            player = MediaPlayer().apply {
                try {
                    setDataSource(it)
                    prepare()
                    start()
                    Log.i(LOG_TAG, "Playback started")
                } catch (e: IOException) {
                    Log.e(LOG_TAG, "prepare() failed", e)
                }
            }
        }
    }

    private fun applyAudioEffect(effect: String) {
        recordedFilePath?.let { inputFilePath ->
            val outputFilePath = "${externalCacheDir?.absolutePath}/audioEffect.mp3"
            val cmd = when (effect) {
                "Chipmunk" -> arrayOf("-y", "-i", inputFilePath, "-af", "asetrate=22100,atempo=1/2", outputFilePath)
                "Robot" -> arrayOf("-y", "-i", inputFilePath, "-af", "asetrate=11100,atempo=4/3,atempo=1/2,atempo=3/4", outputFilePath)
                "Cave" -> arrayOf("-y", "-i", inputFilePath, "-af", "aecho=0.8:0.9:1000:0.3", outputFilePath)
                else -> null
            }
            cmd?.let {
                FFmpeg.execute(it)
                Log.i(LOG_TAG, "Effect $effect applied, output at $outputFilePath")
            }
        }
    }

    private fun playProcessedAudio() {
        val audioEffectFilePath = "${externalCacheDir?.absolutePath}/audioEffect.mp3"
        MediaPlayer().apply {
            try {
                setDataSource(audioEffectFilePath)
                prepare()
                start()
                Log.i(LOG_TAG, "Processed audio playback started")
            } catch (e: IOException) {
                Log.e(LOG_TAG, "Failed to play processed audio", e)
            }
        }
    }
}

@Composable
fun MainScreen(
    startRecording: () -> Unit,
    stopRecording: () -> Unit,
    playAudio: () -> Unit,
    applyEffect: (String) -> Unit,
    playProcessedAudio: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isRecording by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = {
                if (isRecording) {
                    stopRecording()
                } else {
                    startRecording()
                }
                isRecording = !isRecording
            },
            modifier = Modifier.padding(8.dp)
        ) {
            BasicText(text = if (isRecording) "Stop Recording" else "Start Recording")
        }
        Button(
            onClick = playAudio,
            modifier = Modifier.padding(8.dp)
        ) {
            BasicText("Play Audio")
        }
        ClickableText(
            style = TextStyle(color = MaterialTheme.colorScheme.onBackground),
            text = AnnotatedString("Apply Chipmunk Effect"),
            modifier = Modifier.padding(8.dp),
            onClick = { applyEffect("Chipmunk") }
        )
        ClickableText(
            style = TextStyle(color = MaterialTheme.colorScheme.onBackground),
            text = AnnotatedString("Apply Robot Effect"),
            modifier = Modifier.padding(8.dp),
            onClick = { applyEffect("Robot") }
        )
        ClickableText(
            style = TextStyle(color = MaterialTheme.colorScheme.onBackground),
            text = AnnotatedString("Apply Cave Effect"),
            modifier = Modifier.padding(8.dp),
            onClick = { applyEffect("Cave") }
        )
        Button(
            onClick = playProcessedAudio,
            modifier = Modifier.padding(8.dp)
        ) {
            BasicText("Play Processed Audio")
        }
    }
}
