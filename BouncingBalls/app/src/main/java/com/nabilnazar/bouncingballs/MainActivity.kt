package com.nabilnazar.bouncingballs

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import com.nabilnazar.bouncingballs.ui.theme.BouncingBallsTheme
import kotlin.math.pow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BouncingBallsTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    BouncingBallsGame()
                }
            }
        }
    }
}

@Composable
fun BouncingBallsGame() {
        val ballRadiusDp = 10f
        val density = LocalDensity.current.density

        var ballPositions by remember { mutableStateOf(
            listOf(
                Offset(100f, 100f), // Ball 1
                Offset(200f, 150f), // Ball 2
                Offset(300f, 200f)  // Ball 3
            )
        )}
        var ballVelocities by remember { mutableStateOf(
            listOf(
                Offset(8f, 9f), // Ball 1 Velocity
                Offset(-6f, 7f), // Ball 2 Velocity
                Offset(5f, -5f)  // Ball 3 Velocity
            )
        )}

        LaunchedEffect(key1 = true) {
            while (true) {
                withFrameMillis { _ ->
                    ballPositions = ballPositions.mapIndexed { index, ballPosition ->
                        ballPosition.copy(
                            x = ballPosition.x + ballVelocities[index].x,
                            y = ballPosition.y + ballVelocities[index].y
                        )
                    }
                }
            }
        }

        Canvas(modifier = Modifier.fillMaxSize()) {

            // Convert dimensions to pixels
            val ballRadius = ballRadiusDp * density
            val ballRadiusSquared = (2 * ballRadius) * (2 * ballRadius) // Square of the radius multiplied by 2

            // Check for collisions with the window edges for each ball
            ballPositions = ballPositions.mapIndexed { index, ballPosition ->
                var newVelocity = ballVelocities[index]

                if (ballPosition.x - ballRadius < 0f || ballPosition.x + ballRadius > size.width) {
                    newVelocity = newVelocity.copy(x = -newVelocity.x)
                }
                if (ballPosition.y - ballRadius < 0f || ballPosition.y + ballRadius > size.height) {
                    newVelocity = newVelocity.copy(y = -newVelocity.y)
                }

                // Check for collisions with other balls using squared distance
                ballPositions.forEachIndexed { otherIndex, otherBallPosition ->
                    if (index != otherIndex) {
                        // Calculate the squared distance between two ball positions
                        val distanceSquared = (ballPosition.x - otherBallPosition.x).pow(2) + (ballPosition.y - otherBallPosition.y).pow(2)

                        if (distanceSquared <= ballRadiusSquared) {
                            // Reflect velocities upon collision
                            newVelocity = newVelocity.copy(
                                x = -newVelocity.x,
                                y = -newVelocity.y
                            )
                        }
                    }
                }

                // Update ball position
                ballVelocities = ballVelocities.toMutableList().apply { this[index] = newVelocity }
                ballPosition.copy(
                    x = ballPosition.x + newVelocity.x,
                    y = ballPosition.y + newVelocity.y
                )
            }

            // Draw all balls
            ballPositions.forEach { ballPosition ->
                drawCircle(
                    color = Color.Blue,
                    center = ballPosition,
                    radius = ballRadius
                )
            }
        }
    }




    // Sample 1 reference code from arda k github
/*

@Composable
fun PongGame() {
    val racketWidthDp = 150f
    val racketHeightDp = 20f
    val ballRadiusDp = 10f
    val density = LocalDensity.current.density

    var racketPosition by remember { mutableFloatStateOf(200f) }
    var ballPosition by remember { mutableStateOf(Offset(100f, 100f)) }
    var ballVelocity by remember { mutableStateOf(Offset(8F, 9f)) }

    LaunchedEffect(key1 = true) {
        while (true) {
            withFrameMillis { _ ->
                ballPosition = ballPosition.copy(
                    x = ballPosition.x + ballVelocity.x,
                    y = ballPosition.y + ballVelocity.y
                )
            }
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {

        // Convert dimensions to pixels
        val racketWidth = racketWidthDp * density
        val racketHeight = racketHeightDp * density
        val ballRadius = ballRadiusDp * density

        // Check for collisions with the window edges
        if (ballPosition.x - ballRadius < 0f || ballPosition.x + ballRadius > size.width) {
            ballVelocity = ballVelocity.copy(x = -ballVelocity.x)
        }
        if (ballPosition.y - ballRadius < 0f || ballPosition.y + ballRadius > size.height) {
            ballVelocity = ballVelocity.copy(y = -ballVelocity.y)
        }

        // Check for collisions with the racket
        if (ballPosition.y + ballRadius >= size.height - racketHeight &&
            ballPosition.x in racketPosition..racketPosition + racketWidth
        ) {
            ballVelocity = ballVelocity.copy(y = -ballVelocity.y)
        }

        // Draw the racket
        drawRect(
            color = Color.Green,
            topLeft = Offset(racketPosition, size.height - racketHeight),
            size = androidx.compose.ui.geometry.Size(racketWidth, racketHeight)
        )

        // Draw the ball
        drawCircle(
            color = Color.Blue,
            center = ballPosition,
            radius = ballRadius
        )

        // Update the racket position to follow the ball
        racketPosition = min(max(ballPosition.x - racketWidth/2, 0f), size.width - racketWidth)
    }
    }
 */

