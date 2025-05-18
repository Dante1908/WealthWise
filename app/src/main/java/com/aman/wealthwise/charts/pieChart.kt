package com.aman.wealthwise.charts

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.aman.wealthwise.datamodels.PieSlice

@Composable
fun CustomPieChart(
    modifier: Modifier = Modifier,
    data: List<PieSlice>
) {
    val total = data.sumOf { it.value.toDouble() }.toFloat()
    val angles = remember(data) {
        data.map { it to (it.value / total) * 360f }
    }

    Canvas(modifier = modifier) {
        var startAngle = 0f
        angles.forEach { (slice, angle) ->
            drawArc(
                color = slice.color,
                startAngle = startAngle,
                sweepAngle = angle,
                useCenter = true
            )
            startAngle += angle
        }
    }
}
