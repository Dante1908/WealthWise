package com.example.wealthwise.screens.Home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerLayoutType
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.wealthwise.datamodels.Categories
import com.example.wealthwise.datamodels.TransactionInfo
import com.example.wealthwise.ui.theme.OptionsBlue
import com.example.wealthwise.ui.theme.OptionsSelectedBlue
import com.example.wealthwise.viewmodels.TransactionViewModel
import com.example.wealthwise.viewmodels.TransactionsState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(navController: NavController,transactionViewModel: TransactionViewModel,transactionInfo: TransactionInfo?) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    val datePickerValue = rememberDatePickerState()
    val transactionTime = rememberTimePickerState()
    var validationError by remember { mutableStateOf("") }
    var expandedCategoryMenu by remember { mutableStateOf(false) }

    var newTransaction by remember { mutableStateOf(transactionInfo ?: TransactionInfo()) }

    // Set current date/time as fallback
    val currentDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
    val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

    val transactionDate by remember {
        derivedStateOf {
            datePickerValue.selectedDateMillis?.let {
                SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date(it))
            } ?: currentDate
        }
    }

    val categories = when (newTransaction.type) {
        "Expense" -> Categories.expenseCategory()
        "Income" -> Categories.incomeCategory()
        "Transfer" -> Categories.transferCategory()
        else -> emptyList()
    }

    LaunchedEffect(transactionInfo) {
        transactionInfo?.let {
            newTransaction = it
        }
    }

    if (showDatePicker) {
        AlertDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth(0.9f), horizontalAlignment = Alignment.CenterHorizontally) {
                    DatePicker(state = datePickerValue, modifier = Modifier.fillMaxWidth())
                }
            },
            properties = DialogProperties(usePlatformDefaultWidth = false),

            )
    }
    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancel")
                }
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    TimePicker(state = transactionTime, layoutType = TimePickerLayoutType.Vertical)
                }
            }
        )
    }
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(10.dp)){
        Spacer(modifier = Modifier.size(10.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            listOf("Expense", "Income", "Transfer").forEach { type ->
                Box(contentAlignment = Alignment.Center, modifier = Modifier.weight(0.3f)
                    .border(width = 2.dp, color = Color.Transparent, shape = RoundedCornerShape(10.dp))
                    .background(color = if (newTransaction.type == type) OptionsSelectedBlue else OptionsBlue,
                        shape = RoundedCornerShape(10.dp)).clickable { newTransaction =
                        newTransaction.copy(type = type) }) {
                    Text(text = type, color = if (newTransaction.type == type) Color.Black else OptionsSelectedBlue, modifier = Modifier.padding(5.dp))
                }
            }
        }
        Spacer(modifier = Modifier.size(10.dp))
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(), verticalAlignment = Alignment.CenterVertically) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier
                .background(color = OptionsSelectedBlue, shape = RoundedCornerShape(10.dp))
                .weight(0.4f)
                .border(
                    width = 2.dp,
                    color = Color.Transparent,
                    shape = RoundedCornerShape(10.dp)
                )
                .clickable { showDatePicker = true }) {
                Text(text = transactionDate,color = Color.Black,modifier = Modifier.padding(5.dp))
            }
            Spacer(modifier = Modifier.size(5.dp))
            Box(contentAlignment = Alignment.Center, modifier = Modifier
                .background(color = OptionsSelectedBlue, shape = RoundedCornerShape(10.dp))
                .weight(0.4f)
                .border(
                    width = 2.dp,
                    color = Color.Transparent,
                    shape = RoundedCornerShape(10.dp)
                )
                .clickable { showTimePicker = true }) {
                Text(text = "${transactionTime.hour}:${transactionTime.minute}",color = Color.Black,modifier = Modifier.padding(5.dp))
            }
        }
        Spacer(modifier = Modifier.size(10.dp))
        TextField(value = newTransaction.amount, onValueChange = { newTransaction = newTransaction.copy(amount = it) }, label = { Text("Amount *") }, keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
        Spacer(modifier = Modifier.size(10.dp))
        TextField(value = newTransaction.title, onValueChange = {newTransaction = newTransaction.copy(title = it)}, label = {Text("Title *")}, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
        Spacer(modifier = Modifier.size(10.dp))
        Box(contentAlignment = Alignment.CenterStart, modifier = Modifier
            .fillMaxWidth()
            .height(TextFieldDefaults.MinHeight)
            .clickable { expandedCategoryMenu = !expandedCategoryMenu }
            .background(color = Color(0xFFE4DEE7), shape = RoundedCornerShape(10.dp))){
            Text(text = if(newTransaction.category=="") "Select Category *" else newTransaction.category,color = Color.DarkGray,modifier = Modifier.padding(10.dp), fontSize =15.sp)
            DropdownMenu(expanded = expandedCategoryMenu, onDismissRequest = {expandedCategoryMenu = false}) {
                categories.forEach {option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            newTransaction.category = option
                            expandedCategoryMenu = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.size(10.dp))
        TextField(value = newTransaction.account , onValueChange = { newTransaction = newTransaction.copy(account = it) } , label = { Text("Account *") } , modifier = Modifier.fillMaxWidth() , shape = RoundedCornerShape(10.dp))
        Spacer(modifier = Modifier.size(10.dp))
        TextField(value = newTransaction.description , onValueChange = { newTransaction = newTransaction.copy(description = it)} , label = { Text("Description") } , modifier = Modifier.fillMaxWidth() , shape = RoundedCornerShape(10.dp))
        Spacer(modifier = Modifier.size(10.dp))
        if (validationError.isNotEmpty()) {
            Text(validationError, color = Color.Red, modifier = Modifier.padding(10.dp))
            Spacer(modifier = Modifier.size(10.dp))
        }
        Row(modifier = Modifier.fillMaxWidth().padding(5.dp)){
            val buttonColor = ButtonColors(containerColor = OptionsSelectedBlue, contentColor = OptionsBlue, disabledContentColor = OptionsBlue, disabledContainerColor = OptionsSelectedBlue)
            Button(onClick = { navController.navigate("Transactions") }, colors = buttonColor, modifier = Modifier.fillMaxWidth(0.5f)) { Text("Cancel") }
            Spacer(modifier = Modifier.weight(0.025f))
            Button(modifier = Modifier.fillMaxWidth(), colors = buttonColor,onClick = {
                if (newTransaction.amount=="" || newTransaction.account=="" || newTransaction.title=="" || newTransaction.category=="") {
                    validationError = "Please fill all the mandatory fields."
                }
                if (transactionInfo == null) {
                    transactionViewModel.addTransaction(newTransaction)
                    navController.navigate("Transactions")
                }
                else transactionViewModel.updateTransaction(newTransaction)
            }) {
                Text(if (transactionInfo == null) "Add Transaction" else "Update Transaction")
            }
        }
    }
}