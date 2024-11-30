package com.nabilnazar.animsdotsamples

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nabilnazar.animsdotsamples.ui.theme.AnimsDotSamplesTheme
import kotlin.math.pow
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import kotlin.math.sin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AnimsDotSamplesTheme {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    LiquidSwipeDemo()
                }
            }
        }
    }
}

@Composable
fun MySurface() {
    Surface(
        modifier = Modifier
            .size(200.dp)
            .padding(16.dp),
        color = MaterialTheme.colorScheme.primaryContainer, // Use a visible color
        shape = RectangleShape
    ) {
        Text(
            text = "Welcome, Vasu!",
            color = MaterialTheme.colorScheme.onPrimaryContainer, // Ensure text color is visible
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun WaveEffectDemo() {
    var percent by remember { mutableFloatStateOf(0f) }
    var showMessage by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF101221))
    ) {
        val timeText = (2000 - (percent * 20).toInt()).let {
            val hours = it / 100
            val minutes = it % 100
            String.format("%02d:%02d", hours, minutes)
        }


        WaveView(
            modifier = Modifier
                .fillMaxWidth().align(Alignment.BottomCenter),
            percent = percent,
            onTurtleClick = {
                showMessage = !showMessage
            }
        )

        Column(
            modifier = Modifier
                .align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = timeText,
                style = TextStyle(
                    color = Color.White,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Thin
                ),
            )

            Spacer(modifier = Modifier.height(24.dp))
            Slider(
                value = percent,
                onValueChange = { percent = it },
                valueRange = 0f..100f,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            )
        }

        if (showMessage) {
            Text(
                text = "🐢 You've found the turtle! 🐢",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
fun WaveView(modifier: Modifier = Modifier, percent: Float, onTurtleClick: () -> Unit) {
    var waveOffset by remember { mutableStateOf(0f) }
    val infiniteTransition = rememberInfiniteTransition(label = "")

    val animatedWaveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = ""
    )

    waveOffset = animatedWaveOffset

    if (percent > 0f) {
        var canvasSize by remember { mutableStateOf(Size.Zero) }
        var waveHeight by remember { mutableStateOf(0f) }
        var yoffset by remember { mutableStateOf(0f) }

        Box(modifier = modifier.height(200.dp)) {

            Canvas(modifier = Modifier.fillMaxSize()) {
                canvasSize = size
                waveHeight = 0.05f * size.height
                val lowFudge = 0.02f
                val highFudge = 0.98f
                val adjustedPercent = lowFudge + (highFudge - lowFudge) * percent.pow(0.34f)
                yoffset = (1f - adjustedPercent) * size.height

                val wavePath = Path().apply {
                    moveTo(0f, yoffset + waveHeight * sin(waveOffset.toRadians()))
                    for (x in 0 until size.width.toInt()) {
                        val angle = (x / size.width * 360f + waveOffset) % 360f
                        val y = yoffset + waveHeight * sin(angle.toRadians())
                        lineTo(x.toFloat(), y)
                    }
                    lineTo(size.width, size.height)
                    lineTo(0f, size.height)
                    close()
                }

                drawPath(
                    path = wavePath,
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xff014174), Color(0xff0196a5)),
                        startY = yoffset,
                        endY = size.height
                    ),
                    style = Fill
                )
            }

            val turtleYOffset = yoffset + waveHeight * sin(waveOffset.toRadians())

            val scale by animateFloatAsState(
                targetValue = if (turtleYOffset != 0f) 1.2f else 1f,
                animationSpec = tween(durationMillis = 500)
            )

            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(
                        y = turtleYOffset.dp + 600.dp
                    )
                    .size((64 * scale).dp)
                    .clickable {
                        onTurtleClick()
                    }
            )
        }
    }
}

fun Float.toRadians() = (this * Math.PI / 180f).toFloat()
