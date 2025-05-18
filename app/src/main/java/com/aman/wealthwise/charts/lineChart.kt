package com.aman.wealthwise.charts

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import co.yml.charts.common.model.Point

@Composable
fun CustomLineChart(
    modifier: Modifier = Modifier,
    dataPoints: List<Point>,
    lineColor: Color = Color.Green,
    pointColor: Color = Color.Red,
    strokeWidth: Float = 4f
) {
    if (dataPoints.size < 2) return

    Canvas(modifier = modifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        val xMax = dataPoints.maxOf { it.x }
        val xMin = dataPoints.minOf { it.x }
        val yMax = dataPoints.maxOf { it.y }
        val yMin = dataPoints.minOf { it.y }

        val xRange = xMax - xMin
        val yRange = yMax - yMin

        // Padding for labels or visual margin
        val padding = 32.dp.toPx()

        // Map data points to canvas coordinates
        val scaledPoints = dataPoints.map { point ->
            val x = ((point.x - xMin) / xRange) * (canvasWidth - 2 * padding) + padding
            val y = canvasHeight - ((point.y - yMin) / yRange) * (canvasHeight - 2 * padding) - padding
            Offset(x, y)
        }

        // Draw the line
        for (i in 0 until scaledPoints.lastIndex) {
            drawLine(
                color = lineColor,
                start = scaledPoints[i],
                end = scaledPoints[i + 1],
                strokeWidth = strokeWidth
            )
        }

        // Draw points
        scaledPoints.forEach { point ->
            drawCircle(
                color = pointColor,
                radius = 6.dp.toPx(),
                center = point
            )
        }
    }
}
