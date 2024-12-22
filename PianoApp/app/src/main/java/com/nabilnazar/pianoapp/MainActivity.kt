package com.nabilnazar.pianoapp

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.media.SoundPool
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nabilnazar.pianoapp.ui.theme.PianoAppTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    private val soundPool = SoundPool.Builder().setMaxStreams(10).build()
    private lateinit var noteMap: Map<String, Int> // Initialize later in onCreate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        // Initialize the noteMap here
        noteMap = mapOf(
            "C" to soundPool.load(this, R.raw.aa, 1),
            "C#" to soundPool.load(this, R.raw.csharp, 1),
            "D" to soundPool.load(this, R.raw.dd, 1),
            "D#" to soundPool.load(this, R.raw.dd, 1),
            "E" to soundPool.load(this, R.raw.ee, 1),
            "F" to soundPool.load(this, R.raw.ff, 1),
            "F#" to soundPool.load(this, R.raw.fsharp, 1),
            "G" to soundPool.load(this, R.raw.gg, 1),
            "G#" to soundPool.load(this, R.raw.gg, 1),
            "A" to soundPool.load(this, R.raw.aa, 1),
            "A#" to soundPool.load(this, R.raw.aa, 1),
            "B" to soundPool.load(this, R.raw.bb, 1)
        )

        enableEdgeToEdge()
        setContent {
            PianoAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
                    ResponsivePiano(::playNoteInternal) // Pass playNoteInternal to the composable
                }
            }
        }
    }

    private fun playNoteInternal(note: String) {
        noteMap[note]?.let { soundId ->
            soundPool.play(soundId, 1f, 1f, 0, 0, 1f)
        }
    }
}
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun ResponsivePiano(playNote: (String) -> Unit) {
    BoxWithConstraints {
        val whiteKeyWidth = maxWidth / 7
        val blackKeyWidth = whiteKeyWidth / 2
        val maxHeight = maxHeight

        Box(modifier = Modifier.fillMaxSize()) {
            // White keys
            Row(modifier = Modifier.fillMaxWidth()) {
                listOf("C", "D", "E", "F", "G", "A", "B").forEach { note ->
                    PianoKey(note, isBlack = false, width = whiteKeyWidth) {
                        playNote(note)
                    }
                }
            }

            // Black keys - Positioned above white keys, in the correct positions
            Box(modifier = Modifier.fillMaxSize()) {
                val blackKeyNotes = listOf("C#", "D#", null, "F#", "G#", "A#", null)
                blackKeyNotes.forEachIndexed { index, note ->
                    if (note != null) {
                        val offsetX = (index + 1) * whiteKeyWidth.value - blackKeyWidth.value / 2
                        Box(
                            modifier = Modifier
                                .offset(x = offsetX.dp, y = -maxHeight / 3)
                                .width(blackKeyWidth)
                                .height(maxHeight * 3/ 2)
                        ) {
                            PianoKey(note, isBlack = true, width = blackKeyWidth) {
                                playNote(note)
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun PianoKey(note: String, isBlack: Boolean, width: Dp, onClick: () -> Unit) {
    var isPressed by remember { mutableStateOf(false) }

    // Handle the press animation with LaunchedEffect
    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(100) // Simulate the key press duration
            isPressed = false
        }
    }

    Box(
        modifier = Modifier
            .size(width, 400.dp)
            .background(if (isPressed) Color.Gray else if (isBlack) Color.Black else Color.White)
            .border(1.dp, Color.Black)
            .clickable {
                isPressed = true
                onClick() // Play the note
            }
    )
}
