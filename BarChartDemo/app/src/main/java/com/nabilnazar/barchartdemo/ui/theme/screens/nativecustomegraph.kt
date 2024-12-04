package com.nabilnazar.barchartdemo.ui.theme.screens



import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp


@Composable
fun BarChartApp() {
    val data = listOf(
        "Mon " to 10,
        "Tue " to 20,
        "Wed " to 15,
        "Thu " to 25,
        "Fri " to 18,
        "Sat " to 30,
        "Sun " to 22
    )
    BarChart(data = data)
}


@Composable
fun BarChart(data: List<Pair<String, Int>>) {
    // Get the max value for scaling
    val maxValue = data.maxOf { it.second }
    val barColor = Color(0xffff0000)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Drawing the chart


        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            val barWidth = size.width / (data.size * 2) // Adjust spacing
            val maxBarHeight = size.height

            data.forEachIndexed { index, entry ->
                val barHeight = (entry.second.toFloat() / maxValue) * maxBarHeight
                val startX = index * (2 * barWidth) + barWidth / 2

                // Draw the bar
                drawRect(
                    color = barColor,
                    topLeft = Offset(startX, maxBarHeight - barHeight),
                    size = Size(barWidth, barHeight)
                )
            }
        }

        // X-axis labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            data.forEach { entry ->
                Text(
                    text = entry.first,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.width(48.dp),
                    textAlign = TextAlign.Center
                )
            }
        }


    }
}
