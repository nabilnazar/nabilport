package com.nabilnazar.deck67jetpacklottie

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GamifiedProgressTracker()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamifiedProgressTracker() {
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("progress.json"))
    var progress by remember { mutableStateOf(0f) }
    val animatedProgress by animateFloatAsState(targetValue = progress)
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Gamified Progress Tracker") })
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                LottieAnimation(
                    composition = composition,
                    progress = { animatedProgress },
                    modifier = Modifier
                        .size(200.dp)
                        .padding(16.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Progress: ${(progress * 100).toInt()}%", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    coroutineScope.launch {
                        // Simulate progress updates
                        for (i in 1..10) {
                            delay(500)
                            progress = i / 10f
                        }
                    }
                }) {
                    Text(text = "Start Progress")
                }
            }
        }
    )
}
