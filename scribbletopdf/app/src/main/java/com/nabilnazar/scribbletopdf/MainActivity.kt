package com.nabilnazar.scribbletopdf

import android.os.Bundle
 import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
 import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
 import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path

import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.nabilnazar.scribbletopdf.ui.theme.ScribbleToPdfTheme

 import androidx.compose.ui.geometry.Offset


import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.lifecycle.viewmodel.compose.viewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ScribbleToPdfTheme {

               ScribbleApp()
            }
        }
    }
}

@Composable
fun ScribbleApp() {
    // State hoisted to the parent
    val viewModel: ScribbleViewModel = viewModel()


    Scaffold(
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                TextButton(onClick = {
                   // generatePdfFromCanvas(paths) // Pass hoisted state to the PDF generator
                }) {
                    Text("Generate PDF")
                }
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Pass state and callback to ScribbleCanvas
           ScribbleCanvas(viewModel)
        }
    }
}



@Composable
fun ScribbleCanvas() {
    // State to store multiple paths
    var paths by remember { mutableStateOf(listOf<List<Offset>>()) }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        // Start a new path segment
                        paths =
                            paths + listOf(listOf(offset)) // Add a new path with the start point
                    },
                    onDrag = { change, _ ->
                        // Add points to the current path
                        paths = paths.mapIndexed { index, path ->
                            if (index == paths.lastIndex) {
                                // Add the new point to the last path
                                path + change.position
                            } else {
                                path // Keep the other paths unchanged
                            }
                        }
                    }
                )
            }
    ) {
        // Draw all paths
        paths.forEach { pathPoints ->
            if (pathPoints.isNotEmpty()) {
                // Create a Path from the points
                val path = Path().apply {
                    moveTo(pathPoints.first().x, pathPoints.first().y)
                    for (point in pathPoints.drop(1)) {
                        lineTo(point.x, point.y)
                    }
                }
                // Draw the Path
                drawPath(
                    path = path,
                    color = Color.Green,
                    style = Stroke(width = 8f)
                )
            }
        }
    }

}


@Composable
fun ScribbleCanvas(
   viewModel: ScribbleViewModel
) {

    val paths = viewModel.paths
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        viewModel.addPath(offset)
                    },
                    onDrag = { change, _ ->
                        viewModel.addPointToCurrentPath(change.position)
                    }
                )
            }
    ) {
        paths.forEach { pathPoints ->
            if (pathPoints.isNotEmpty()) {
                val path = Path().apply {
                    moveTo(pathPoints.first().x, pathPoints.first().y)
                    pathPoints.drop(1).forEach { lineTo(it.x, it.y) }
                }
                drawPath(
                    path = path,
                    color = Color.Green,
                    style = Stroke(width = 8f)
                )
            }
        }
    }
}


