package com.nabilnazar.animsdotsamples


import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput

@Composable
fun LiquidSwipeExample() {


        var swipeProgress by remember { mutableStateOf(0f) }
        val animatedProgress by animateFloatAsState(targetValue = swipeProgress)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Gray)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { _, dragAmount ->
                        swipeProgress = (swipeProgress + dragAmount / 1000f).coerceIn(0f, 1f)
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height

                val swipeX = width * animatedProgress
                val centerY = height / 2

                val controlPointOffset = 800f // Size of the bulge

                val path = Path().apply {
                    moveTo(1f, 0f)
                    lineTo( controlPointOffset - swipeX , 0f)

                    // Top Curve
                    cubicTo(
                        swipeX, centerY - 300f, // Control point above the center
                        swipeX, centerY + 300f, // Control point below the center
                        swipeX - controlPointOffset, height
                    )

                    lineTo(1f, height)
                    close()
                }

                drawPath(path = path, color = Color.Cyan)
            }
        }
    }