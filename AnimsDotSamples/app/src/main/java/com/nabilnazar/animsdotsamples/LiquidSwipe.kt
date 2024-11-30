package com.nabilnazar.animsdotsamples

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
 import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.Canvas

@Composable
fun LiquidSwipe(
    pages: List<Color>,
    modifier: Modifier = Modifier
) {
    var currentPage by remember { mutableStateOf(0) }
    var swipeOffsetX by remember { mutableStateOf(0f) }

    val width = remember { mutableStateOf(0f) }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onHorizontalDrag = { change, dragAmount ->
                        swipeOffsetX = (swipeOffsetX + dragAmount).coerceIn(-width.value, width.value)
                        if (swipeOffsetX > width.value /5) {
                            currentPage = (currentPage - 1).coerceAtLeast(0)
                            swipeOffsetX = 0f
                        } else if (swipeOffsetX < -width.value/5 ) {
                            currentPage = (currentPage + 1).coerceAtMost(pages.size - 1)
                            swipeOffsetX = 0f
                        }
                    }
                )
            }
    ) {
        width.value = size.width

        // Draw current page
        drawRect(color = pages[currentPage], size = size)

        if (currentPage < pages.size - 1) {
            // Draw next page with liquid effect
            val path = Path().apply{
                moveTo(width.value + swipeOffsetX, 0f)
                quadraticTo(
                    width.value + swipeOffsetX, size.height,
                    width.value + swipeOffsetX, size.height,

                )
                lineTo(width.value, size.height)
                lineTo(width.value, 0f)
                close()
            }
            drawPath(path, color = pages[currentPage + 1])
        }
    }
}

@Composable
fun LiquidSwipeDemo() {
    val pages = listOf(
        Color(0xFF6C63FF),
        Color(0xFFFF6584),
        Color(0xFF56C596),
        Color(0xFFFFD460)
    )

    LiquidSwipe(pages = pages)
}