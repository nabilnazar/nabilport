package com.nabilnazar.deck50jetpackanimation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nabilnazar.deck50jetpackanimation.ui.theme.Deck50jetpackanimationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Deck50jetpackanimationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreenWithFlippingCard()
                }
            }
        }
    }
}

@Composable
fun MainScreenWithFlippingCard() {
    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .width(300.dp)
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = Color.LightGray
                )
            ) {
                FlippingCardWithText()
            }
        }
    }
}

@Composable
fun FlippingCardWithText() {
    var rotation by remember { mutableStateOf(0f) }
    val animatedRotation by animateFloatAsState(
        targetValue = rotation,
        label = "Flip Card Animation"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .pointerInput(Unit) {
                detectHorizontalDragGestures { change, dragAmount ->
                    // Update rotation based on drag amount but clamp it to the 0 - 360 degree range
                    rotation = (rotation + dragAmount * 0.5f) % 360
                    // This will ensure the rotation stays within the bounds of 0 - 360 degrees
                    if (rotation < 0) rotation += 360 // Ensure no negative rotation value
                    change.consume()
                }
            },
        contentAlignment = Alignment.Center
    ) {
        // Flipping Card
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .fillMaxHeight(0.8f)
                .clip(RoundedCornerShape(8.dp))
                .graphicsLayer {
                    rotationY = animatedRotation
                    cameraDistance = 12 * density // Adds perspective effect
                }
                .background(
                    if (animatedRotation % 360 in 90f..270f) Color.Red // Back side is Red
                    else Color.Blue // Front side is Blue
                ),
            contentAlignment = Alignment.Center
        ) {
            if (animatedRotation % 360 in 90f..270f) {
                // Back side
                Text(
                    text = "Back Side",
                    color = Color.White,
                    fontSize = 16.sp
                )
            } else {
                // Front side
                Text(
                    text = "Front Side",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }

        // Trailing Text
        if (animatedRotation % 360 in 90f..270f) {
            Text(
                text = "Trailing Text →",
                color = Color.Black,
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.BottomStart).padding(8.dp)
            )
        } else {
            Text(
                text = "← Trailing Text",
                color = Color.Black,
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp)
            )
        }
    }
}
