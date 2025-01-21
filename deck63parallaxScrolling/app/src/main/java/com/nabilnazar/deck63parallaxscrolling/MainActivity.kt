package com.nabilnazar.deck63parallaxscrolling

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.nabilnazar.deck63parallaxscrolling.ui.theme.Deck63parallaxScrollingTheme
import androidx.compose.ui.unit.lerp
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Deck63parallaxScrollingTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        ParallaxList()
                    }
                }
            }
        }
    }
}

@Composable
fun ParallaxList() {
    val baseImages = listOf(
        R.drawable.image1,
        R.drawable.image2,
        R.drawable.image3,
        R.drawable.image4
    )

    // Initialize the list state at a "middle" position
    val initialIndex = Int.MAX_VALUE / 2
    val listState = rememberLazyListState(initialIndex)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState
    ) {
        // Simulate infinite scrolling by wrapping indices
        items(Int.MAX_VALUE) { position ->
            val index = ((position % baseImages.size) + baseImages.size) % baseImages.size
            ParallaxImage(
                imageRes = baseImages[index],
                position = position,
                listState = listState
            )
        }
    }
}

@Composable
fun ParallaxImage(imageRes: Int, position: Int, listState: LazyListState) {
    val itemInfo = listState.layoutInfo.visibleItemsInfo.firstOrNull { it.index == position }
    val viewportHeight = listState.layoutInfo.viewportEndOffset - listState.layoutInfo.viewportStartOffset
    val itemCenter = itemInfo?.let { it.offset + it.size / 2f } ?: 0f
    val viewportCenter = viewportHeight / 2f
    val distanceFromCenter = abs(itemCenter - viewportCenter)

    // Adjust progress for zoom and blur effects
    val progress = max(0f, 1f - (distanceFromCenter / viewportHeight))
    val scale = lerp(1f, 1.5f, progress) // Increased maximum scale from 1.3f to 1.5f
    val blurRadius = lerp(20.dp, 0.dp, progress) // Increased maximum blur for stronger effect

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale
            )
            .blur(blurRadius)
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}


fun lerp(start: Float, stop: Float, fraction: Float): Float {
    return start + fraction * (stop - start)
}
