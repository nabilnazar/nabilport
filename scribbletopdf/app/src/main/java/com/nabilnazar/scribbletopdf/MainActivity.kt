package com.nabilnazar.scribbletopdf

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel


class MainActivity : ComponentActivity() {


    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Notification permission granted!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Notification permission denied!", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissionLauncher.launch("android.permission.POST_NOTIFICATIONS")
        // Turn off the decor fitting system windows, which allows us to handle insets,
        // including IME animations
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
    val context = LocalContext.current

    Scaffold(
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                TextButton(onClick = {
                    generatePdfFromCanvas(context, viewModel.paths)
                }) {
                    Text("Generate PDF")
                }

                TextButton(onClick = {
                    viewModel.clearPaths() // Clear the canvas
                }) {
                    Text("Clear")
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



