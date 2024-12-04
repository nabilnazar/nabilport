package com.nabilnazar.barchartdemo.ui.theme.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jaikeerthick.composable_graphs.composables.line.LineGraph
import com.jaikeerthick.composable_graphs.composables.line.model.LineData
import com.jaikeerthick.composable_graphs.composables.line.style.LineGraphStyle
import com.jaikeerthick.composable_graphs.composables.line.style.LineGraphVisibility
import com.jaikeerthick.composable_graphs.style.LabelPosition


@Composable
fun LinedGraphScreen() {

    val data = listOf(
        LineData(x = "Sun", y = 200),
        LineData(x = "Mon", y = 400),
        LineData(x = "Mon", y = 100),
        LineData(x = "Mon", y = 500),
        LineData(x = "Mon", y = 700),
        LineData(x = "Mon", y = 50),
        LineData(x = "Mon", y = 100),


        )

    // Wrap in a Box with padding to adjust placement
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), // Add padding to the screen
        contentAlignment = Alignment.Center // Center align the graph
    ) {
        LineGraph(
            modifier = Modifier,
            data = data,
            onPointClick = { value: LineData ->
                // do something with value
            },
            style = LineGraphStyle(
                visibility = LineGraphVisibility(isYAxisLabelVisible = true),
                yAxisLabelPosition = LabelPosition.LEFT
            )
        )
    }
}