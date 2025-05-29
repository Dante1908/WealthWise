package com.aman.wealthwise.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.sp
import com.aman.wealthwise.datamodels.PieSlice
import kotlinx.coroutines.delay
import kotlin.math.*

@Composable
fun CustomPieChart(modifier: Modifier = Modifier, data: List<PieSlice>) {
    val total = data.sumOf { it.value.toDouble() }.toFloat()
    val angles = remember(data) { data.map { it to (it.value / total) * 360f } }

    var selectedSlice by remember { mutableStateOf<PieSlice?>(null) }
    var centerOffset by remember { mutableStateOf(Offset.Zero) }
    val sliceAngles = mutableMapOf<PieSlice, Pair<Float, Float>>() // startAngle to sweep

    LaunchedEffect(selectedSlice) {
        if (selectedSlice != null) {
            delay(3000)
            selectedSlice = null
        }
    }

    Canvas(
        modifier = modifier.pointerInput(data) {
            detectTapGestures { offset ->
                val dx = offset.x - centerOffset.x
                val dy = offset.y - centerOffset.y
                val angle = (Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())) + 360) % 360
                val distance = sqrt(dx * dx + dy * dy)

                val radius = size.height / 2f
                if (distance > radius) return@detectTapGestures // Outside pie

                var startAngle = 0f
                for ((slice, sweepAngle) in angles) {
                    val endAngle = startAngle + sweepAngle
                    if (angle >= startAngle && angle < endAngle) {
                        selectedSlice = slice
                        break
                    }
                    startAngle += sweepAngle
                }
            }
        }
    ) {
        val radius = size.minDimension / 2f
        centerOffset = Offset(size.width / 2f, size.height / 2f)
        var startAngle = 0f

        // Draw pie slices
        angles.forEach { (slice, sweepAngle) ->
            drawArc(
                color = slice.color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                topLeft = Offset(centerOffset.x - radius, centerOffset.y - radius),
                size = Size(radius * 2, radius * 2)
            )
            sliceAngles[slice] = Pair(startAngle, sweepAngle)
            startAngle += sweepAngle
        }

        // Draw percentage labels on all slices (always visible)
        drawIntoCanvas { canvas ->
            angles.forEach { (slice, sweepAngle) ->
                val percentage = (slice.value / total * 100).toInt()

                if (percentage >= 3) {
                    val (start, sweep) = sliceAngles[slice] ?: return@forEach
                    val angleRad = Math.toRadians((start + sweep / 2).toDouble())
                    val labelRadius = radius * 0.7f
                    val labelX = centerOffset.x + labelRadius * cos(angleRad).toFloat()
                    val labelY = centerOffset.y + labelRadius * sin(angleRad).toFloat()

                    canvas.nativeCanvas.drawText(
                        "$percentage%",
                        labelX,
                        labelY,
                        android.graphics.Paint().apply {
                            color = android.graphics.Color.WHITE
                            textSize = 12.sp.toPx() // Smaller text size
                            isFakeBoldText = true
                            textAlign = android.graphics.Paint.Align.CENTER
                            // Add shadow for better readability
                            setShadowLayer(4f, 2f, 2f, android.graphics.Color.BLACK)
                        }
                    )
                }
            }
        }

        // Draw category label only for selected slice (temporary)
        selectedSlice?.let { slice ->
            val (start, sweep) = sliceAngles[slice] ?: return@let
            val angleRad = Math.toRadians((start + sweep / 2).toDouble())
            val labelX = centerOffset.x + (radius * 1.1f) * cos(angleRad).toFloat()
            val labelY = centerOffset.y + (radius * 1.1f) * sin(angleRad).toFloat()

            drawIntoCanvas { canvas ->
                canvas.nativeCanvas.drawText(
                    slice.label,
                    labelX,
                    labelY,
                    android.graphics.Paint().apply {
                        color = android.graphics.Color.WHITE
                        textSize = 14.sp.toPx()
                        isFakeBoldText = true
                        textAlign = android.graphics.Paint.Align.CENTER
                        setShadowLayer(4f, 2f, 2f, android.graphics.Color.BLACK)
                    }
                )
            }
        }
    }
}