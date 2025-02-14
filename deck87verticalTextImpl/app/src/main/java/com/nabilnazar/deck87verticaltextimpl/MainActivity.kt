package com.nabilnazar.deck87verticaltextimpl

import android.annotation.SuppressLint
import android.graphics.Paint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.EaseOutBounce
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.nabilnazar.deck87verticaltextimpl.ui.theme.Deck87verticalTextImplTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Deck87verticalTextImplTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth().fillMaxHeight(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FallingVerticalText(
                                modifier = Modifier
                                    .wrapContentSize(Alignment.Center) // Ensure text is centered
                            )
                            VerticalText(
                                modifier = Modifier
                                    .wrapContentSize(Alignment.Center) // Ensure text is centered
                            )
                        }
                    }
                }
            }
        }
    }
}


@SuppressLint("WrongConstant")
@Composable
fun FallingVerticalText(modifier: Modifier = Modifier) {
    val text = "熟練の技"
    val density = LocalDensity.current
    val textSizePx = with(density) { 64.sp.toPx() } // Convert sp to px

    val infiniteTransition = rememberInfiniteTransition()

    val offsets = text.mapIndexed { index, _ ->
        infiniteTransition.animateFloat(
            initialValue = -200f,
            targetValue = 0f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000 + (index * 300), easing = EaseOutBounce),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    Box(
        modifier
            .background(Color.White)
            .wrapContentSize()
            .drawWithContent {
                drawIntoCanvas { canvas ->
                    val paint = Paint().apply {
                        textSize = textSizePx
                        textAlign = Paint.Align.CENTER
                        color = android.graphics.Color.BLACK
                    }

                    val xPos = size.width / 2
                    val yStart = (size.height - (textSizePx * text.length)) / 2

                    text.forEachIndexed { index, char ->
                        val yOffset = yStart + (index * textSizePx) + offsets[index].value
                        canvas.nativeCanvas.drawText(
                            char.toString(),
                            xPos,
                            yOffset,
                            paint
                        )
                    }
                }
            }
    )
}

@Composable
@SuppressLint("WrongConstant")
fun VerticalText(modifier: Modifier = Modifier) {
    val text = "熟練の技"
    val density = LocalDensity.current
    val textSizePx = with(density) { 64.sp.toPx() } // Convert sp to px

    Box(
        modifier
            .background(Color.White)
            .wrapContentSize()
            .drawWithContent {
                drawIntoCanvas { canvas ->
                    val paint = Paint().apply {
                        textSize = textSizePx
                        textAlign = Paint.Align.CENTER
                        color = android.graphics.Color.BLACK
                    }

                    val xPos = size.width / 2
                    val yStart = (size.height - (textSizePx * text.length)) / 2

                    text.forEachIndexed { index, char ->
                        val yOffset = yStart + (index * textSizePx)
                        canvas.nativeCanvas.drawText(
                            char.toString(),
                            xPos,
                            yOffset,
                            paint
                        )
                    }
                }
            }
    )
}
