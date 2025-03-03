package com.example.wealthwise.screens.Home

import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.wealthwise.ui.theme.OptionsBlue
import com.example.wealthwise.ui.theme.OptionsSelectedBlue
import com.example.wealthwise.viewmodels.TransactionViewModel
import com.example.wealthwise.viewmodels.UserAuth
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.Pie
import kotlin.random.Random

@Composable
fun AccountScreen(authViewModel: UserAuth,transactionViewModel: TransactionViewModel) {
    val currentUserEmail = authViewModel.user.value?.email
    val currentUser = authViewModel.user.value?.displayName?: currentUserEmail?.split("@")?.get(0)
    val userProfilePhoto = authViewModel.user.value?.photoUrl.toString()
    var typeSelected by remember { mutableStateOf("Expense") }

    val transactions = transactionViewModel.transactions.value
    val categorySums = transactions!!.groupBy { it.category }.mapValues { entry -> entry.value.sumOf { it.amount.toDoubleOrNull() ?: 0.0 } }
    val datewise_data = transactions.groupBy { it.date }.toSortedMap().mapValues { entry ->
            entry.value.sumOf {
                when (it.type) {
                    "Income" -> it.amount.toDoubleOrNull() ?: 0.0
                    "Expense" -> -(it.amount.toDoubleOrNull() ?: 0.0)
                    else -> 0.0
                }
            }
        }
        .toList()
        .runningFold("" to 0.0) { acc, (date, amount) ->
            date to (acc.second + amount)
        }
        .drop(1)
    val chartValues = datewise_data.map { (date, balance) ->
        balance // Only the balance as y-axis, assuming x-axis is date index
    }
    var categorical_data by remember {
        mutableStateOf(
            categorySums.map { (category, amount) ->
                Pie(
                    label = category,
                    data = amount,
                    color = Color(Random.nextFloat(), Random.nextFloat(), Random.nextFloat(), 1f),
                    selectedColor = Color.Yellow
                )
            }
        )
    }

    Column(modifier = Modifier.padding(5.dp)
        .fillMaxSize(), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).border(2.dp, White, shape = RoundedCornerShape(10.dp))){
                Row(modifier= Modifier.padding(20.dp)){
                    Image(painter = rememberAsyncImagePainter(userProfilePhoto), contentDescription = "Account Logo", modifier = Modifier.size(40.dp))
                    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally){
                        Text(text = " $currentUser", color = White, fontWeight = FontWeight.Bold)
                        Text(text = " $currentUserEmail", color = Gray)
                    }
                }
            }
            Spacer(modifier = Modifier.size(10.dp))
            Column(verticalArrangement = Arrangement.spacedBy(15.dp)) {

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        listOf("Expense", "Income", "Transfer").forEach { type ->
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.weight(0.3f).border(width = 2.dp, color = Color.Transparent, shape = RoundedCornerShape(10.dp)).background(color = if (typeSelected == type) OptionsSelectedBlue else OptionsBlue, shape = RoundedCornerShape(10.dp)).clickable { typeSelected = type }) {
                                Text(text = type, color = if (typeSelected == type) Color.Black else OptionsSelectedBlue, modifier = Modifier.padding(5.dp))
                            }
                        }
                    }
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                        PieChart(modifier = Modifier.size(200.dp), data = categorical_data,
                            onPieClick = {
                                println("${it.label} Clicked")
                                val pieIndex = categorical_data.indexOf(it)
                                categorical_data = categorical_data.mapIndexed { mapIndex, pie -> pie.copy(selected = pieIndex == mapIndex) }
                            },
                            selectedScale = 1.2f,
                            scaleAnimEnterSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            ),
                            colorAnimEnterSpec = tween(300),
                            colorAnimExitSpec = tween(300),
                            scaleAnimExitSpec = tween(300),
                            spaceDegreeAnimExitSpec = tween(300),
                            style = Pie.Style.Fill
                        )
                    }
                LineChart(
                    indicatorProperties = HorizontalIndicatorProperties(enabled = false),
                    modifier = Modifier.padding(horizontal = 22.dp).aspectRatio(1f),
                    data = remember {
                        listOf(
                            Line(
                                label = "Windows",
                                values = chartValues,
                                color = SolidColor(Color(0xFF23af92)),
                                firstGradientFillColor = Color(0xFF2BC0A1).copy(alpha = .5f),
                                secondGradientFillColor = Color.Transparent,
                                strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
                                gradientAnimationDelay = 1000,
                                drawStyle = DrawStyle.Stroke(width = 2.dp),
                            )
                        )
                    },
                    animationMode = AnimationMode.Together(delayBuilder = {
                        it * 500L
                    }),
                )

            }
            Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).border(2.dp, White, shape = RoundedCornerShape(10.dp)), contentAlignment = Alignment.Center){
                Row(modifier= Modifier.padding(5.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center){
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
