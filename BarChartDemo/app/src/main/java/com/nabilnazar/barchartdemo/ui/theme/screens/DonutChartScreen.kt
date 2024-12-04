package com.nabilnazar.barchartdemo.ui.theme.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jaikeerthick.composable_graphs.composables.donut.DonutChart
import com.jaikeerthick.composable_graphs.composables.donut.model.DonutData
import com.jaikeerthick.composable_graphs.composables.donut.style.DonutChartStyle
import com.jaikeerthick.composable_graphs.composables.donut.style.DonutChartType
import com.jaikeerthick.composable_graphs.composables.donut.style.DonutSliceType


@Composable
fun DonutChartScreen() {

    // sample values
    val donutChartData = listOf(
        DonutData(value = 30F),
        DonutData(value = 60F),
        DonutData(value = 70F),
        DonutData(value = 50F),
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), // Add padding to the screen
        contentAlignment = Alignment.Center // Center align the graph
    ) {
        DonutChart(
            modifier = Modifier
                .padding(vertical = 20.dp)
                .size(360.dp),
            data = donutChartData,
            type = DonutChartType.Normal,
            style =  DonutChartStyle(
                thickness = 90.dp,
                sliceType = DonutSliceType.Rounded,
            )
        )
    }
}