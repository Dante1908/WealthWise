package com.example.wealthwise.screens.Home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.commandiron.compose_loading.FoldingCube
import com.example.wealthwise.datamodels.TransactionInfo
import com.example.wealthwise.ui.theme.BackgroundBlue
import com.example.wealthwise.ui.theme.OptionsSelectedBlue
import com.example.wealthwise.ui.theme.Purple80
import com.example.wealthwise.viewmodels.TransactionViewModel
import com.example.wealthwise.viewmodels.TransactionsState
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun TransactionsScreen(navController: NavController,transactionViewModel: TransactionViewModel) {

    val transactions by transactionViewModel.transactions.observeAsState(emptyList())
    val transactionState by transactionViewModel.transactionState.observeAsState(TransactionsState.Loading)

    when (transactionState) {
        is TransactionsState.Loading -> {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize().background(color = BackgroundBlue)) {
                FoldingCube(size = DpSize(80.dp, 80.dp), color = Purple80, durationMillisPerFraction = 300)
            }
        }
        is TransactionsState.Done -> {
            if (transactions.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No Transactions Available")
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                    items(transactions) {
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
fun TransactionItem(transactionInfo: TransactionInfo, onDelete: () -> Unit,onEdit:() ->Unit) {
    var isClicked by remember { mutableStateOf(false) }
    val contentColor = when(transactionInfo.type){
        "Expense"-> Color.Red
        "Income"-> Color.Green
            else-> Color.White
    }
    val icon = when (transactionInfo.type) {
        "Expense" -> Icons.Default.KeyboardArrowUp
        "Income" -> Icons.Default.KeyboardArrowDown
        else-> Icons.Default.Refresh
    }
    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).border(2.dp, contentColor, shape = RoundedCornerShape(10.dp)).clickable { isClicked = !isClicked }.padding(10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = transactionInfo.type, tint = contentColor)
            Column{
                Text(text = transactionInfo.title, color = Color.White)
                Text(text = "₹ ${transactionInfo.amount}", color = Color.White)
                AnimatedVisibility(visible = isClicked, enter = slideInHorizontally { -it },exit = slideOutHorizontally { -it }) {
                    Column{
                        Text(text = "Category : ${transactionInfo.category}", color = Color.White)
                        Text(text = "Account : ${transactionInfo.account}", color = Color.White)
                        Text(
                            text = if (transactionInfo.description != "") "Description : ${transactionInfo.description}" else "No description available",
                            color = Color.White
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(text = transactionInfo.date.toFormattedDate(), color = Color.White)
            Spacer(modifier = Modifier.width(10.dp))
            AnimatedVisibility(visible = isClicked, enter = slideInHorizontally { it },exit = slideOutHorizontally { it }) {
                Column{
                    IconButton(onClick = onDelete) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = OptionsSelectedBlue)
                    }
                    IconButton(onClick = onEdit) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = OptionsSelectedBlue)
                    }
                }
            }
        }
    }
}

// Extension function to format date
fun String.toFormattedDate(): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // Adjust based on your Firestore format
        val outputFormat = SimpleDateFormat("dd-MMM", Locale.getDefault()) // Desired format
        val date = inputFormat.parse(this) ?: return this
        outputFormat.format(date)
    } catch (e: Exception) {
        this // Return original if parsing fails
    }
}

