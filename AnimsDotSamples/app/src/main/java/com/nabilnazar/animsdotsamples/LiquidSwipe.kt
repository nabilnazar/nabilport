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

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectHorizontalDragGestures { _, dragAmount ->
                    swipeOffsetX += dragAmount
                    if (swipeOffsetX > size.width / 2) {
                        currentPage = (currentPage - 1).coerceAtLeast(0)
                        swipeOffsetX = 0f
                    } else if (swipeOffsetX < -size.width / 2) {
                        currentPage = (currentPage + 1).coerceAtMost(pages.size - 1)
                        swipeOffsetX = 0f
                    }
                }
            }
    ) {
        val width = size.width
        val height = size.height

        // Draw the current page
        drawRect(color = pages[currentPage], size = size)

        if (currentPage < pages.size - 1) {
            // Draw the next page with a liquid swipe effect
            val path = Path().apply {
                moveTo(width + swipeOffsetX, 0f)
                lineTo(width, 0f)

                // Top bulge curve
                cubicTo(
                    width + swipeOffsetX / 2, height / 3, // First control point
                    width + swipeOffsetX / 2, 2 * height / 3, // Second control point
                    width + swipeOffsetX, height
                )

                lineTo(width, height)
                close()
            }

            drawPath(path = path, color = pages[currentPage + 1])
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

    LiquidSwipe(pages = pages, modifier = Modifier.fillMaxSize())
}