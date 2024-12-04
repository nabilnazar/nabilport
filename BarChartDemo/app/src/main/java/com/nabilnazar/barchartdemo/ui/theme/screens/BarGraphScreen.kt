package com.nabilnazar.barchartdemo.ui.theme.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.jaikeerthick.composable_graphs.composables.bar.BarGraph
import com.jaikeerthick.composable_graphs.composables.bar.model.BarData
import com.jaikeerthick.composable_graphs.composables.bar.style.BarGraphColors
import com.jaikeerthick.composable_graphs.composables.bar.style.BarGraphFillType
import com.jaikeerthick.composable_graphs.composables.bar.style.BarGraphStyle
import com.jaikeerthick.composable_graphs.composables.bar.style.BarGraphVisibility
import com.jaikeerthick.composable_graphs.style.LabelPosition


@Composable
fun BarGraphScreen() {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        BarGraph(
            data = listOf(
                BarData(x = "22", y = 20),
                BarData(x = "23", y = 30),
                BarData(x = "24", y = 40),
                BarData(x = "25", y = 5),
                BarData(x = "26", y = 44),
                BarData(x = "27", y = 22),

                ),

            style = BarGraphStyle(
                visibility = BarGraphVisibility(
                    isYAxisLabelVisible = true,
                    isGridVisible = true
                ),
                yAxisLabelPosition = LabelPosition.LEFT,
                colors = BarGraphColors(
                    fillType = BarGraphFillType.Gradient(
                        brush = Brush.verticalGradient(
                            listOf(
                                Color.Red,
                                Color.Blue
                            )
                        )
                    ),
                    clickHighlightColor = Color.Green
                )
            )

        )
    }
}