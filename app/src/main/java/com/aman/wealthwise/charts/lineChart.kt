package com.aman.wealthwise.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.yml.charts.common.model.Point
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.pow
import kotlin.math.sqrt

@Composable
fun CustomLineChart(modifier: Modifier = Modifier, dataPoints: List<Point>, lineColor: Color = Color.Green, pointColor: Color = Color.Red, strokeWidth: Float = 4f) {
    if (dataPoints.size < 2) return

    var selectedPointIndex by remember { mutableStateOf<Int?>(null) }
    var showTooltip by remember { mutableStateOf(false) }
    var tooltipPosition by remember { mutableStateOf(Offset.Zero) }

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize().pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val canvasWidth = size.width.toFloat()
                        val canvasHeight = size.height.toFloat()

                        val xMax = dataPoints.maxOf { it.x }
                        val xMin = dataPoints.minOf { it.x }
                        val yMax = dataPoints.maxOf { it.y }
                        val yMin = dataPoints.minOf { it.y }

                        val xRange = xMax - xMin
                        val yRange = yMax - yMin

                        val padding = 32.dp.toPx()

                        // Map data points to canvas coordinates
                        val scaledPoints = dataPoints.mapIndexed { index, point ->
                            val x = ((point.x - xMin) / xRange) * (canvasWidth - 2 * padding) + padding
                            val y = canvasHeight - ((point.y - yMin) / yRange) * (canvasHeight - 2 * padding) - padding
                            index to Offset(x, y)
                        }

                        // Check if tap is near any point (within 24dp radius)
                        val touchRadius = 24.dp.toPx()
                        val tappedPoint = scaledPoints.find { (_, pointOffset) ->
                            val distance = sqrt(
                                (offset.x - pointOffset.x).pow(2) +
                                        (offset.y - pointOffset.y).pow(2)
                            )
                            distance <= touchRadius
                        }

                        if (tappedPoint != null) {
                            selectedPointIndex = tappedPoint.first
                            tooltipPosition = tappedPoint.second
                            showTooltip = true
                        } else {
                            showTooltip = false
                            selectedPointIndex = null
                        }
                    }
                }
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            val xMax = dataPoints.maxOf { it.x }
            val xMin = dataPoints.minOf { it.x }
            val yMax = dataPoints.maxOf { it.y }
            val yMin = dataPoints.minOf { it.y }

            val xRange = xMax - xMin
            val yRange = yMax - yMin

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
            scaledPoints.forEachIndexed { index, point ->
                val isSelected = selectedPointIndex == index
                val radius = if (isSelected) 8.dp.toPx() else 6.dp.toPx()
                val color = if (isSelected) pointColor.copy(alpha = 0.8f) else pointColor

                drawCircle(color = color, radius = radius, center = point)

                // Draw outer ring for selected point
                if (isSelected) {
                    drawCircle(color = pointColor.copy(alpha = 0.3f), radius = 12.dp.toPx(), center = point, style = Stroke(width = 2.dp.toPx()))
                }
            }
        }

        // Tooltip
        if (showTooltip && selectedPointIndex != null) {
            val selectedPoint = dataPoints[selectedPointIndex!!]

            Card(modifier = Modifier.offset(x = with(LocalDensity.current) { (tooltipPosition.x - 50.dp.toPx()).toDp() }, y = with(LocalDensity.current) { (tooltipPosition.y - 60.dp.toPx()).toDp() }).padding(4.dp), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Balance", color = Color.Gray)
                    Text(text = String.format("%.2f", selectedPoint.y), fontWeight = FontWeight.Bold, color = Color.Black)
                    Text(text = formatDate(selectedPoint.x), color = Color.Gray)
                }
            }
        }
    }
}

fun formatDate(timestamp: Float): String {
    return try {
        val date = Date(timestamp.toLong())
        val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        formatter.format(date)
    } catch (e: Exception) {
        // Fallback if timestamp conversion fails
        "Date: ${timestamp.toInt()}"
    }
}