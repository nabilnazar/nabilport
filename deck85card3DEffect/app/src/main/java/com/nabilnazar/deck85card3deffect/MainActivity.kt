package com.nabilnazar.deck85card3deffect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.nabilnazar.deck85card3deffect.ui.theme.Deck85card3DEffectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Deck85card3DEffectTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Interactive3DCard()
                }
            }
        }
    }
}



@Composable
fun Interactive3DCard() {
    var rotationX by remember { mutableStateOf(0f) }
    var rotationY by remember { mutableStateOf(0f) }
    var isTapped by remember { mutableStateOf(false) }
    var scale by remember { mutableStateOf(1f) }

    val background = Brush.linearGradient(
        colors = listOf(Color(0xFFF30404), Color(0xFFA20303), Color(0xFF4D0101))
    )

    val infiniteTransition = rememberInfiniteTransition()
    val borderAnimation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(9000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Trigger smooth bounce when tapped
    LaunchedEffect(isTapped) {
        if (isTapped) {
            animate(
                initialValue = 1f,
                targetValue = 1.15f,
                animationSpec = tween(250, easing = LinearOutSlowInEasing)
            ) { value, _ -> scale = value }
            animate(
                initialValue = 1.15f,
                targetValue = 0.95f,
                animationSpec = tween(300, easing = LinearOutSlowInEasing)
            ) { value, _ -> scale = value }
            animate(
                initialValue = 0.95f,
                targetValue = 1.1f,
                animationSpec = tween(250, easing = LinearOutSlowInEasing)
            ) { value, _ -> scale = value }
            animate(
                initialValue = 1.1f,
                targetValue = 1f,
                animationSpec = tween(400, easing = LinearOutSlowInEasing)
            ) { value, _ -> scale = value }

            isTapped = false // Reset after animation
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, _, _ ->
                    rotationX += pan.y * 0.1f
                    rotationY -= pan.x * 0.1f
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .size(300.dp)
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    rotationX = rotationX,
                    rotationY = rotationY,
                    shadowElevation = 16f
                )
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, _, _ ->
                        rotationX += pan.y * 0.1f
                        rotationY -= pan.x * 0.1f
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { isTapped = true }
                    )
                },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = R.drawable.image),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 2.dp.toPx()
                    val cardWidth = size.width
                    val stripLength = cardWidth / 2f
                    val animatedOffset = stripLength * borderAnimation

                    val pathEffect = PathEffect.dashPathEffect(
                        floatArrayOf(stripLength, stripLength),
                        animatedOffset
                    )

                    val gradientBrush = Brush.linearGradient(
                        colors = listOf(Color(0xFFF8DA3C), Color(0xFFEFE753), Color(0xFFF1AF07)),
                        start = Offset(0f, 0f),
                        end = Offset(size.width, size.height)
                    )

                    drawRoundRect(
                        brush = gradientBrush,
                        style = Stroke(width = strokeWidth, pathEffect = pathEffect),
                        cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx())
                    )
                }
            }
        }
    }
}