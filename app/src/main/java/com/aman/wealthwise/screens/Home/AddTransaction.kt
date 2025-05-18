package com.aman.wealthwise.screens.Home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.aman.wealthwise.datamodels.Categories
import com.aman.wealthwise.datamodels.TransactionInfo
import com.aman.wealthwise.ui.theme.Blue40
import com.aman.wealthwise.ui.theme.Blue80
import com.aman.wealthwise.viewmodels.TransactionViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(navController: NavController, transactionViewModel: TransactionViewModel, transactionInfo: TransactionInfo?) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    val currentDateMillis = System.currentTimeMillis()
    val calendar = Calendar.getInstance()
    val datePickerValue = rememberDatePickerState(initialSelectedDateMillis = currentDateMillis)
    val transactionTime = rememberTimePickerState(initialHour = calendar.get(Calendar.HOUR_OF_DAY), initialMinute = calendar.get(Calendar.MINUTE))
    var validationError by remember { mutableStateOf("") }
    var expandedCategoryMenu by remember { mutableStateOf(false) }

    var newTransaction by remember {
        mutableStateOf(
            transactionInfo ?: TransactionInfo(
                date = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date(currentDateMillis)),
                time = String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))
            )
        )
    }

    LaunchedEffect(transactionTime.hour, transactionTime.minute) {
        newTransaction = newTransaction.copy(
            time = String.format("%02d:%02d", transactionTime.hour, transactionTime.minute)
        )
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
                TextButton(onClick = {
                    datePickerValue.selectedDateMillis?.let { millis ->
                        val selectedDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date(millis))
                        newTransaction = newTransaction.copy(date = selectedDate)
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    DatePicker(state = datePickerValue, modifier = Modifier.fillMaxWidth())
                }
            },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        )
    }

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    newTransaction = newTransaction.copy(
                        time = String.format("%02d:%02d", transactionTime.hour, transactionTime.minute)
                    )
                    showTimePicker = false
                }) {
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

    Column(modifier = Modifier.fillMaxSize().padding(10.dp)) {
        Spacer(modifier = Modifier.size(10.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            listOf("Expense", "Income", "Transfer").forEach { type ->
                Box(contentAlignment = Alignment.Center, modifier = Modifier.weight(0.3f).border(width = 2.dp, color = Color.Transparent, shape = RoundedCornerShape(10.dp)).background(color = if (newTransaction.type == type) Blue80 else Blue40, shape = RoundedCornerShape(10.dp)).clickable { newTransaction = newTransaction.copy(type = type, category = "") }) {
                    Text(text = type, color = if (newTransaction.type == type) Color.Black else Blue80, modifier = Modifier.padding(5.dp))
                }
            }
        }
        Spacer(modifier = Modifier.size(10.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.background(color = Blue80, shape = RoundedCornerShape(10.dp)).weight(0.4f).border(width = 2.dp, color = Color.Transparent, shape = RoundedCornerShape(10.dp)).clickable { showDatePicker = true }) {
                Text(text = newTransaction.date, color = Color.Black, modifier = Modifier.padding(5.dp))
            }
            Spacer(modifier = Modifier.size(5.dp))
            Box(contentAlignment = Alignment.Center, modifier = Modifier.background(color = Blue80, shape = RoundedCornerShape(10.dp)).weight(0.4f).border(width = 2.dp, color = Color.Transparent, shape = RoundedCornerShape(10.dp)).clickable { showTimePicker = true }) {
                Text(text = newTransaction.time, color = Color.Black, modifier = Modifier.padding(5.dp))
            }
        }
        Spacer(modifier = Modifier.size(10.dp))
        TextField(value = newTransaction.amount, onValueChange = { newTransaction = newTransaction.copy(amount = it) }, label = { Text("Amount *") }, keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
        Spacer(modifier = Modifier.size(10.dp))
        TextField(value = newTransaction.title, onValueChange = { newTransaction = newTransaction.copy(title = it) }, label = { Text("Title *") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
        Spacer(modifier = Modifier.size(10.dp))
        Box(contentAlignment = Alignment.CenterStart, modifier = Modifier.fillMaxWidth().height(TextFieldDefaults.MinHeight).clickable { expandedCategoryMenu = !expandedCategoryMenu }.background(color = Color(0xFFE4DEE7), shape = RoundedCornerShape(10.dp))) {
            Text(text = if (newTransaction.category == "") "Select Category *" else newTransaction.category, color = Color.DarkGray, modifier = Modifier.padding(10.dp), fontSize = 15.sp)
            DropdownMenu(expanded = expandedCategoryMenu, onDismissRequest = { expandedCategoryMenu = false }) {
                val categories = when (newTransaction.type) {
                    "Expense" -> Categories.expenseCategory()
                    "Income" -> Categories.incomeCategory()
                    "Transfer" -> Categories.transferCategory()
                    else -> emptyList()
                }
                categories.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            newTransaction = newTransaction.copy(category = option)
                            expandedCategoryMenu = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.size(10.dp))
        TextField(value = newTransaction.account, onValueChange = { newTransaction = newTransaction.copy(account = it) }, label = { Text("Account *") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
        Spacer(modifier = Modifier.size(10.dp))
        TextField(value = newTransaction.description, onValueChange = { newTransaction = newTransaction.copy(description = it) }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
        Spacer(modifier = Modifier.size(10.dp))
        if (validationError.isNotEmpty()) {
            Text(validationError, color = Color.Red, modifier = Modifier.padding(10.dp))
            Spacer(modifier = Modifier.size(10.dp))
        }
        Row(modifier = Modifier.fillMaxWidth().padding(5.dp)) {
            val buttonColor = ButtonColors(containerColor = Blue80, contentColor = Blue40, disabledContentColor = Blue40, disabledContainerColor = Blue80)
            Button(onClick = { navController.navigate("Transactions") }, colors = buttonColor, modifier = Modifier.fillMaxWidth(0.5f)) { Text("Cancel") }
            Spacer(modifier = Modifier.size(5.dp))
            Button(modifier = Modifier.fillMaxWidth(), colors = buttonColor,
                onClick = {
                    if (newTransaction.amount.isEmpty() || newTransaction.account.isEmpty() || newTransaction.title.isEmpty() || newTransaction.category.isEmpty()) {
                        validationError = "Please fill all the mandatory fields."
                    } else {
                        if (transactionInfo == null) {
                            transactionViewModel.addTransaction(newTransaction)
                            navController.navigate("Transactions")
                        } else {
                            transactionViewModel.updateTransaction(newTransaction)
                        }
                    }
                }
            ) {
                Text(if (transactionInfo == null) "Add Transaction" else "Update Transaction")
            }
        }
    }
}