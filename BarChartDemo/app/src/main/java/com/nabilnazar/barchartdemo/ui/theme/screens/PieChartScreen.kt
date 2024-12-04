package com.nabilnazar.barchartdemo.ui.theme.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.jaikeerthick.composable_graphs.composables.pie.PieChart
import com.jaikeerthick.composable_graphs.composables.pie.model.PieData
import com.jaikeerthick.composable_graphs.composables.pie.style.PieChartStyle
import com.jaikeerthick.composable_graphs.composables.pie.style.PieChartVisibility


@Composable
fun PieChartScreen() {


    val pieChartData = listOf(
        PieData(value = 130F, label = "HTC", color = Color.Green),
        PieData(value = 260F, label = "Apple", labelColor = Color.Blue),
        PieData(value = 500F, label = "Google"),
    )

// composable
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        PieChart(
            modifier = Modifier
                .padding(vertical = 20.dp)
                .size(360.dp),
            data = pieChartData,
            style = PieChartStyle(
                visibility = PieChartVisibility(
                    isLabelVisible = true,
                    isPercentageVisible = true
                )
            ),
            onSliceClick = { pieData ->

            }
        )
    }
}