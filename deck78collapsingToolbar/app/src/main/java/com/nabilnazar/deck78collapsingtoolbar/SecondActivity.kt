package com.nabilnazar.deck78collapsingtoolbar

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.nabilnazar.deck78collapsingtoolbar.ui.theme.Deck78collapsingToolbarTheme

class SecondActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            Deck78collapsingToolbarTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize().padding(innerPadding)
                            .background(Color(0XFFD9CAB3))
                    ) {
                        SmoothSideMenu()
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun SmoothSideMenu() {

        val colors = listOf(
            Color(0XFF5E0B15),
            Color(0xff90323D),
            Color(0XFFBC8034),
            Color(0xff8C7A6B),
            Color(0xff49111C)
        )

        val icons = listOf(
            Icons.Default.Home,
            Icons.Default.Settings,
            Icons.Default.AccountCircle,
            Icons.Default.Notifications,
            Icons.Default.Favorite
        )

        var isMenuExpanded by remember { mutableStateOf(false) }
        var expandedIndex by remember { mutableIntStateOf(-1) }

        Box(modifier = Modifier.fillMaxSize()) {

            if (isMenuExpanded) {
                Box(modifier = Modifier
                    .matchParentSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { isMenuExpanded = false })
            }

            Column(horizontalAlignment = Alignment.Start) {
                Icon(imageVector = Icons.Default.Menu,
                    contentDescription = "Smooth Side Menu",
                    modifier = Modifier
                        .clickable { isMenuExpanded = !isMenuExpanded }
                        .padding(16.dp))

                colors.zip(icons).forEachIndexed { index, (color, icon) ->

                    val rotation by animateFloatAsState(
                        targetValue = if (isMenuExpanded) 0f else -90f,
                        animationSpec = tween(durationMillis = 300, delayMillis = index * 100),
                        label = "rotation"
                    )
                    val width by animateDpAsState(
                        targetValue = if (expandedIndex == index) 150.dp else 100.dp,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                        label = "width"
                    )
                    val borderWidth by animateDpAsState(
                        targetValue = if (expandedIndex == index) 2.dp else (-1).dp,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy), label = "borderWidth"
                    )

                    Box(modifier = Modifier
                        .width(width)
                        .height(100.dp)
                        .graphicsLayer { rotationY = rotation }
                        .background(color)
                        .border(
                            BorderStroke(
                                borderWidth,
                                Brush.linearGradient(listOf(Color(0XFF5E0B15), Color(0xff8C7A6B)))
                            )
                        )
                        .clickable {
                            expandedIndex = if (expandedIndex == index) -1 else index
                        }, contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = "Menu Sample Item",
                            tint = Color.White,
                        )
                    }
                }
            }
        }
    }

}