package com.nabilnazar.carousel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.nabilnazar.carousel.ui.theme.CarouselTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CarouselTheme() {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AnimatedCarousel(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimatedCarousel(
    modifier: Modifier = Modifier
) {
    val items = listOf(
        "https://wallpapers.com/images/high/cool-4k-ultra-hd-0rnrj3ptdk4wxrbt.webp",
        "https://mrwallpaper.com/images/high/never-walk-alone-liverpool-4k-cxjbrcjnybn1tg32.jpg",
        "https://wallpapers.com/images/high/cool-4k-ultra-hd-7q0o48hqes0wl9cx.webp",
        "https://wallpapers.com/images/high/cool-4k-ultra-hd-3840-x-2160-xb13phe737nu7hav.webp",
        "https://wallpapers.com/images/high/cool-4k-ultra-hd-e2ufamftvvcin2rc.webp",

        )
    val animatedScale = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        while (true) {
            animatedScale.animateTo(
                targetValue = 1.2f,
                animationSpec = tween(durationMillis = 3000, easing = FastOutSlowInEasing)
            )
            animatedScale.animateTo(
                targetValue = 1.1f,
                animationSpec = tween(durationMillis = 3000, easing = FastOutSlowInEasing)
            )
        }
    }

    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
        HorizontalMultiBrowseCarousel(
            state = rememberCarouselState { items.size },
            modifier = modifier,
            preferredItemWidth = 186.dp,
            itemSpacing = 8.dp,
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) { index ->
            AsyncImage(
                model = items[index],
                modifier = Modifier
                    .height(400.dp)
                    .maskClip(MaterialTheme.shapes.extraLarge)
                    .graphicsLayer(
                        scaleX = animatedScale.value,
                        scaleY = animatedScale.value
                    ),
                contentScale = ContentScale.Crop,
                contentDescription = null
            )
        }
    }
}