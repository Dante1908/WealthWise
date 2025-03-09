package com.example.wealthwise.screens.Home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.yml.charts.axis.AxisData
import co.yml.charts.common.extensions.formatToSinglePrecision
import co.yml.charts.common.model.PlotType
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.piechart.charts.PieChart
import co.yml.charts.ui.piechart.models.PieChartConfig
import co.yml.charts.ui.piechart.models.PieChartData
import coil.compose.rememberAsyncImagePainter
import com.example.wealthwise.ui.theme.Blue40
import com.example.wealthwise.ui.theme.Blue80
import com.example.wealthwise.viewmodels.TransactionViewModel
import com.example.wealthwise.viewmodels.UserAuth

@Composable
fun AccountScreen(authViewModel: UserAuth, transactionViewModel: TransactionViewModel) {
    val currentUserEmail = authViewModel.user.value?.email
    val currentUser = authViewModel.user.value?.displayName ?: currentUserEmail?.split("@")?.get(0)
    val userProfilePhoto = authViewModel.user.value?.photoUrl.toString()
    var typeSelected by remember { mutableStateOf("Expense") }

    val sortedTransactions = transactionViewModel.transactions.value!!.reversed()
    val axisData = mutableMapOf<String,Float>()
    val pointsData = mutableListOf<Point>()

    var currBalance = 0.0
    var xDist = 0.0

    val dailyChanges = mutableMapOf<String,Double>()
    for(transaction in sortedTransactions){
        val dateStr = transaction.date
        val amount = transaction.amount.toDouble()
        val currentChange = dailyChanges.getOrDefault(dateStr,0.0)
        when (transaction.type) {
            "Expense" -> dailyChanges[dateStr] = currentChange - amount
            "Income" -> dailyChanges[dateStr] = currentChange + amount
            "Transfer" -> dailyChanges[dateStr] = currentChange
        }
    }
    for((date,change) in dailyChanges.toSortedMap()){
        currBalance += change
        axisData[date] = currBalance.toFloat()
        pointsData.add(Point(xDist.toFloat(),currBalance.toFloat()))
        xDist+=1.0
    }
    val dates = axisData.keys.toList()
    val balances = axisData.values.toList()

    val xAxisData = AxisData.Builder().axisStepSize(100.dp).steps(dates.size - 1).labelData { i -> dates[i] }.labelAndAxisLinePadding(15.dp).build()
    val minBalance = balances.minOrNull() ?: 0f
    val maxBalance = balances.maxOrNull() ?: 0f
    val steps = if (maxBalance == minBalance) 1 else 5
    val yRange = if (maxBalance == minBalance) 100f else maxBalance - minBalance
    val yStepSize = yRange / steps
    val yAxisData = AxisData.Builder().steps(steps).labelAndAxisLinePadding(20.dp)
        .labelData { i ->
            val value = minBalance + (i * yStepSize)
            value.formatToSinglePrecision()
        }
        .build()
    val lineChartData = LineChartData(linePlotData = LinePlotData(lines = listOf(Line(dataPoints = pointsData, lineStyle = LineStyle(color = Color.Black)))), xAxisData = xAxisData, yAxisData = yAxisData, backgroundColor = Color.Transparent)
    Column(modifier = Modifier.padding(5.dp).fillMaxSize(), verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).border(2.dp, White, shape = RoundedCornerShape(10.dp))) {
            Row(modifier = Modifier.padding(20.dp)) {
                Image(painter = rememberAsyncImagePainter(userProfilePhoto), contentDescription = "Account Logo", modifier = Modifier.size(40.dp))
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = " $currentUser", color = White, fontWeight = FontWeight.Bold)
                    Text(text = " $currentUserEmail", color = Gray)
                }
            }
        }
        Spacer(modifier = Modifier.size(10.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(15.dp)) {
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    listOf("Expense", "Income", "Transfer").forEach { type ->
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.weight(0.3f).border(width = 2.dp, color = Color.Transparent, shape = RoundedCornerShape(10.dp)).background(color = if (typeSelected == type) Blue80 else Blue40, shape = RoundedCornerShape(10.dp)).clickable { typeSelected = type }) {
                            Text(text = type, color = if (typeSelected == type) Color.Black else Blue80, modifier = Modifier.padding(5.dp))
                        }
                    }
                }
                Box(modifier = Modifier.fillMaxWidth()){
                    val categoricalData = mutableMapOf<String, Float>()
                    val typeTransactions = sortedTransactions.filter { it.type == typeSelected }.groupBy { it.category }
                    for ((category, transactions) in typeTransactions) {
                        val totalAmount = transactions.sumOf { it.amount.toDouble() }.toFloat()
                        categoricalData[category] = totalAmount
                    }
                    val sliceColors = listOf(Color(0xFFDA0FFF), Color(0xFF9745FF), Color(0xFF0052FF), Color(0xFFF53844),
                        Color(0xFF18FFB6), Color(0xFFFFD166),Color(0xFF07FF00)
                    )
                    val slices = categoricalData.entries.mapIndexed { index, entry ->
                        PieChartData.Slice(label = entry.key, value = entry.value, color = sliceColors[index % sliceColors.size])
                    }.filter { it.value > 0f }
                    val pieChartData = PieChartData(slices = slices, plotType = PlotType.Pie)
                    PieChart(
                        modifier = Modifier.width(400.dp).height(400.dp),
                        pieChartData = pieChartData,
                        pieChartConfig = PieChartConfig(isAnimationEnable = true, showSliceLabels = true, animationDuration = 500)
                    )
                }
                Box(modifier = Modifier.fillMaxWidth()){
                    LineChart(lineChartData = lineChartData, modifier = Modifier.aspectRatio(1f).background(color = Color.Transparent))
                }
            }
        }
        Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).border(2.dp, White, shape = RoundedCornerShape(10.dp)), contentAlignment = Alignment.Center) {
            Row(modifier = Modifier.padding(5.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                IconButton(onClick = {
                    transactionViewModel.clearUserData()
                    authViewModel.signout()
                }) {
                    Icon(tint = White, imageVector = Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Close App")
                }
                Text(text = "Sign Out", color = White)
            }
        }
    }
}