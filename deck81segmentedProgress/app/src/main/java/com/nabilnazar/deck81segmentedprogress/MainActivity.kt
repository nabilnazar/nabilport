package com.nabilnazar.deck81segmentedprogress


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.min

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                SegmentedProgressBarDemo(innerPadding)
            }
        }
    }
}

@Composable
fun SegmentedProgressBarDemo(innerPadding: PaddingValues) {
    var progress by remember { mutableStateOf(0.5f) }
    var strokeWidth by remember { mutableStateOf(30f) }
    var gapAngle by remember { mutableStateOf(15f) }
    var maxAngle by remember { mutableStateOf(180f) }

    val animatedProgress by animateFloatAsState(targetValue = progress, label = "Progress Animation")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SegmentedProgressBar(progress = animatedProgress, strokeWidth, gapAngle, maxAngle)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Progress: ${(progress * 100).toInt()}%", fontSize = 18.sp, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Adjust Progress")
        Slider(value = progress, onValueChange = { progress = it })
        Text(text = "Stroke Width")
        Text(text = "${strokeWidth.toInt()}°")
        Slider(value = strokeWidth, onValueChange = { strokeWidth = it }, valueRange = 10f..50f)
        Text(text = "Gap Angle")
        Text(text = gapAngle.toInt().toString())
        Slider(value = gapAngle, onValueChange = { gapAngle = it }, valueRange = 5f..30f)
        Text(text = "Max Angle")
        Text(text = "${maxAngle.toInt()}°")
        Slider(value = maxAngle, onValueChange = { maxAngle = it }, valueRange = 100f..360f)
    }
}
/*
@Composable
fun SegmentedProgressBar(progress: Float, strokeWidth: Float, gapAngle: Float, maxAngle: Float) {
    val segmentCount = 5
    val segmentAngle = (maxAngle - (gapAngle * (segmentCount - 1))) / segmentCount

    Canvas(modifier = Modifier.size(200.dp)) {
        val radius = (min(size.width, size.height) / 2) - strokeWidth / 2
        val startAngle = -maxAngle / 2 - 90f

        for (i in 0 until segmentCount) {
            val angle = startAngle + i * (segmentAngle + gapAngle)
            val color = if (i < (progress * segmentCount).toInt()) Color.Green else Color.Gray

            drawArc(
                color = color,
                startAngle = angle,
                sweepAngle = segmentAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth)
            )
        }
    }
}
*/

@Composable
fun SegmentedProgressBar(progress: Float, strokeWidth: Float, gapAngle: Float, maxAngle: Float) {
    val segmentCount = 5
    val segmentAngle = (maxAngle - (gapAngle * (segmentCount - 1))) / segmentCount
    val filledSegments = (progress * segmentCount).toInt()
    val partialFill = (progress * segmentCount) - filledSegments

    Canvas(modifier = Modifier.size(200.dp)) {
        val radius = (min(size.width, size.height) / 2) - strokeWidth / 2
        val startAngle = -maxAngle / 2 - 90f

        for (i in 0 until segmentCount) {
            val angle = startAngle + i * (segmentAngle + gapAngle)
            val isFilled = i < filledSegments
            val isPartial = i == filledSegments
            val sweep = if (isPartial) segmentAngle * partialFill else segmentAngle

            if (isPartial) {
                // Draw the filled portion in Light Green
                drawArc(
                    color = Color.Green.copy(alpha = 0.8f),
                    startAngle = angle,
                    sweepAngle = sweep,
                    useCenter = false,
                    style = Stroke(width = strokeWidth)
                )

                // Draw the remaining part in Red
                drawArc(
                    color = Color.Red.copy(alpha = 0.5f),
                    startAngle = angle + sweep, // Start where the filled part ended
                    sweepAngle = segmentAngle - sweep, // Remaining segment space
                    useCenter = false,
                    style = Stroke(width = strokeWidth)
                )
            } else {
                // Normal filled or empty segments
                val color = if (isFilled) Color.Green else Color.Gray
                drawArc(
                    color = color,
                    startAngle = angle,
                    sweepAngle = segmentAngle,
                    useCenter = false,
                    style = Stroke(width = strokeWidth)
                )
            }
        }

    }
}


//@Composable
//fun SegmentedProgressBar(progress: Float, strokeWidth: Float, gapAngle: Float, maxAngle: Float) {
//    val segmentCount = 5
//    val segmentAngle = (maxAngle - (gapAngle * (segmentCount - 1))) / segmentCount
//    val filledSegments = (progress * segmentCount).toInt()
//    val partialFill = (progress * segmentCount) - filledSegments
//
//    Canvas(modifier = Modifier.size(200.dp)) {
//        val radius = (min(size.width, size.height) / 2) - strokeWidth / 2
//        val startAngle = -maxAngle / 2 - 90f
//
//        for (i in 0 until segmentCount) {
//            val angle = startAngle + i * (segmentAngle + gapAngle)
//            val isFilled = i < filledSegments
//            val isPartial = i == filledSegments
//            val sweep = if (isPartial) segmentAngle * partialFill else segmentAngle
//            val color = when {
//                isFilled -> Color.Green
//                isPartial ->  Color.Green.copy(alpha = 0.5f)
//                else -> Color.Gray
//            }
//
//            drawArc(
//                color = color,
//                startAngle = angle,
//                sweepAngle = sweep,
//                useCenter = false,
//                style = Stroke(width = strokeWidth)
//            )
//        }
//    }
//}