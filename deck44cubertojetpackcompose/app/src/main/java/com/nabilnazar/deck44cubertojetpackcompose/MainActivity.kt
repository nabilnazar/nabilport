package com.nabilnazar.deck44cubertojetpackcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.nabilnazar.deck44cubertojetpackcompose.ui.theme.Deck44cubertojetpackcomposeTheme




class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Deck44cubertojetpackcomposeTheme {
                LiquidSwipeScreen()
            }
        }
    }
}

@Composable
fun LiquidSwipeScreen() {
    val pages = remember {
        listOf(
            Color(0xFFE91E63), // Page 1 (Pink)
            Color(0xFF3F51B5), // Page 2 (Blue)
            Color(0xFF4CAF50)  // Page 3 (Green)
        )
    }

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { pages.size }
    )

    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(pages[page])
            ) {
                Text(
                    text = "Page ${page + 1}",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = Color.White
                    ),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        // Liquid Transition Effect using Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val progress = pagerState.currentPageOffsetFraction.coerceIn(0f, 1f)
            val currentPage = pagerState.currentPage
            val nextPage = (currentPage + 1) % pages.size

            val currentColor = pages[currentPage]
            val nextColor = pages[nextPage]

            val width = size.width
            val height = size.height

            // Adjusted control point calculation for smoother transition
            val controlX = if (progress < 0.5f) {
                width * (1 - progress) / 2 + width * progress
            } else {
                width * (1 - progress) + width * progress / 2
            }
            val controlY = height / 2

            // Creating the path for the liquid effect
            val path = Path().apply {
                moveTo(width, 0f)
                quadraticBezierTo(controlX, controlY, 0f, height)
                lineTo(width, height)
                close()
            }

            // Draw the current page color first
            drawRect(color = currentColor)

            // Then draw the path for the transition on top
            drawPath(
                path = path,
                color = nextColor
            )

            // Optional: Add a stroke for better visualization
            drawPath(
                path = path,
                color = Color.Black,
                style = Stroke(width = 2.dp.toPx())
            )
        }

        // Page Indicators at the bottom
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            pages.forEachIndexed { index, _ ->
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .padding(horizontal = 4.dp)
                        .background(
                            if (index == pagerState.currentPage) Color.White else Color.Gray
                        )
                )
            }
        }
    }
}