package com.aman.wealthwise.screens.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.aman.wealthwise.datamodels.TransactionInfo
import com.aman.wealthwise.ui.theme.BackgroundBlue
import com.aman.wealthwise.ui.theme.Blue80
import com.aman.wealthwise.ui.theme.Green40
import com.aman.wealthwise.viewmodels.TransactionViewModel
import com.aman.wealthwise.viewmodels.TransactionsState
import com.commandiron.compose_loading.FoldingCube
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun TransactionsScreen(navController: NavController,transactionViewModel: TransactionViewModel) {

    val transactions by transactionViewModel.transactions.observeAsState(emptyList())
    val transactionState by transactionViewModel.transactionState.observeAsState(TransactionsState.Loading)
    val format = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())

    val sortedTransactions = transactions.sortedByDescending { txn ->
        try {
            format.parse("${txn.date} ${txn.time}")?.time ?: 0L
        } catch (e: Exception) {
            0L
        }
    }

    when (transactionState) {
        is TransactionsState.Loading -> {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize().background(color = BackgroundBlue)) {
                FoldingCube(size = DpSize(80.dp, 80.dp), color = Green40, durationMillisPerFraction = 300)
            }
        }
        is TransactionsState.Done -> {
            if (sortedTransactions.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No Transactions Available",color = Color.White)
                }
            } else {

                    LazyColumn(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                        items(sortedTransactions) {
                            TransactionItem(it,
                                onDelete = { transactionViewModel.deleteTransaction(transactionId = it.id) },
                                onEdit = {
                                    transactionViewModel.setEditTransaction(it)
                                    navController.navigate("AddTransaction")
                                })
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }

            }
        }

        is TransactionsState.Error -> {
            val errorMessage = (transactionState as TransactionsState.Error).message
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("There was an error fetching transactions: \n$errorMessage", color = Color.Red)
            }
        }
    }
}
@Composable
fun TransactionItem(transactionInfo: TransactionInfo, onDelete: () -> Unit, onEdit: () -> Unit) {
    var isExpanded by remember { mutableStateOf(false) }
    val contentColor = when(transactionInfo.type) {
        "Expense" -> Color.Red
        "Income" -> Color.Green
        else -> Color.White
    }
    val icon = when(transactionInfo.type) {
        "Expense" -> Icons.Default.KeyboardArrowUp
        "Income" -> Icons.Default.KeyboardArrowDown
        else -> Icons.Default.Refresh
    }
    val animationSpec = tween<Dp>(
        durationMillis = 300,
        easing = FastOutSlowInEasing
    )
    val boxHeightAnimation by animateDpAsState(
        targetValue = if (isExpanded) 160.dp else 60.dp,
        animationSpec = animationSpec,
        label = "boxHeight"
    )

    Box(modifier = Modifier.fillMaxWidth().height(boxHeightAnimation).clip(RoundedCornerShape(12.dp)).border(2.dp, contentColor, shape = RoundedCornerShape(10.dp)).clickable { isExpanded = !isExpanded }.padding(10.dp)) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Icon(imageVector = icon, contentDescription = transactionInfo.type, tint = contentColor)
                Column {
                    Text(text = transactionInfo.title, color = Color.White)
                    Text(text = "₹ ${transactionInfo.amount}", color = Color.White)
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(text = transactionInfo.date, color = Color.White)
                Spacer(modifier = Modifier.width(10.dp))
            }
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn(animationSpec = tween(300)) + expandVertically(animationSpec = tween(300, easing = FastOutSlowInEasing)),
                exit = fadeOut(animationSpec = tween(300)) + shrinkVertically(animationSpec = tween(300, easing = FastOutSlowInEasing))
            ) {
                Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Category : ${transactionInfo.category}", color = Color.White)
                        Text(text = "Account : ${transactionInfo.account}", color = Color.White)
                        Text(text = if (transactionInfo.description.isNotEmpty()) "Description : ${transactionInfo.description}" else "No description available", color = Color.White)
                    }
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        IconButton(onClick = onDelete) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Blue80)
                        }
                        IconButton(onClick = onEdit) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = Blue80)
                        }
                    }
                }
            }
        }
    }
}
