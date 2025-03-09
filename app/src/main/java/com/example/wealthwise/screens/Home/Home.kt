package com.example.wealthwise.screens.Home

import android.app.Activity
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.wealthwise.R
import com.example.wealthwise.ui.theme.BackgroundBlue
import com.example.wealthwise.ui.theme.Montserrat
import com.example.wealthwise.ui.theme.Blue80
import com.example.wealthwise.viewmodels.TransactionViewModel
import com.example.wealthwise.viewmodels.UserAuth


@Composable
fun HomeScreen(authViewModel: UserAuth, transactionViewModel: TransactionViewModel = viewModel()){
    val navController = rememberNavController()
    val context = LocalContext.current
    val activity = context as? Activity
    var showExitDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var isBlue by remember { mutableStateOf(true) }
    val user by authViewModel.user.observeAsState()
    //val stockViewModel: StockViewModel = viewModel()
    if (showExitDialog) {
        ExitConfirmationDialog(
            onConfirm = {
                activity?.finish() // Close the app
            },
            onDismiss = {
                showExitDialog = false // Return to app
            }
        )
    }
    if (showSettingsDialog) {
        SettingsDialog(
            onDismiss = { showSettingsDialog = false },
            onConfirmChanged = { newIsBlue ->
                isBlue = newIsBlue
            },
            isBluePrev = isBlue
        )
    }

    LaunchedEffect(user) {
        transactionViewModel.fetchTransactions()
    }
    Column(modifier = Modifier.fillMaxSize().background(color = if (isBlue) BackgroundBlue else Black).padding(10.dp)){
        Spacer(modifier = Modifier.height(20.dp))
        Row(modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(), verticalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.width(10.dp))
            IconButton(modifier = Modifier.size(60.dp), onClick = { showExitDialog=true }) {
                Icon(tint = White, imageVector = Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Close App")
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(text = context.getString(R.string.app_name), style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Blue80), fontFamily = Montserrat)
            Spacer(modifier = Modifier.weight(1f))
            IconButton(modifier = Modifier.size(60.dp), onClick = { showSettingsDialog=true }) {
                Icon(imageVector = Icons.Default.Settings, tint = White, contentDescription = "Settings")
            }
            Spacer(modifier = Modifier.width(10.dp))
        }
        Box(modifier = Modifier.weight(1f).fillMaxSize(), contentAlignment = Alignment.Center){
            NavHost(navController = navController, startDestination = "Transactions") {
                composable("Transactions") { TransactionsScreen(navController,transactionViewModel = transactionViewModel) }
                composable("AddTransaction") { AddTransactionScreen(navController,transactionViewModel = transactionViewModel, transactionInfo = transactionViewModel.editTransaction.value) }
                composable("Investment") { InvestmentsScreen(navController) }
                composable("Account") { AccountScreen(authViewModel = authViewModel, transactionViewModel = transactionViewModel) }
            }
        }
        BottomNavBar(navController)
        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
fun ExitConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp), shape = RoundedCornerShape(16.dp), color = White){
            Column(modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Exit App", color = Black)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Are you sure you want to exit the app?", color = Black)
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Button(onClick = onDismiss, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color.Gray), shape = RoundedCornerShape(6.dp)) {
                        Text("Cancel", color = White, fontFamily = Montserrat)
                    }
                    Button(onClick = onConfirm, modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp), colors = ButtonDefaults.buttonColors(containerColor = Red), shape = RoundedCornerShape(6.dp)) {
                        Text("Confirm", color = White, fontFamily = Montserrat)
                    }
                }
            }
        }
    }
}


@Composable
fun SettingsDialog(
    onDismiss: () -> Unit,
    onConfirmChanged: (Boolean) -> Unit,
    isBluePrev: Boolean
) {
    var isBlue by remember { mutableStateOf(isBluePrev) }
    Dialog(onDismissRequest = onDismiss) {

        Surface(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp), shape = RoundedCornerShape(16.dp), color = White){
            Column(modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "App Settings", color = Black)
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = if (isBlue) "Blue Mode" else "Normal Mode", color = if (isBlue) BackgroundBlue else Black)
                    Switch(checked = isBlue, onCheckedChange = { isBlue = it })
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Button(onClick = onDismiss, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color.Gray), shape = RoundedCornerShape(6.dp)) {
                        Text("Cancel", color = White, fontFamily = Montserrat)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onConfirmChanged(isBlue)
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("Save", color = Black  , fontFamily = Montserrat)
                    }
                }
            }
        }
    }
}



@Composable
fun BottomNavBar(navController: NavController){
    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).border(2.dp, White, shape = RoundedCornerShape(10.dp)).background(Black)) {
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
            NavigationIcon(navController, "Transactions", R.drawable.transactions)
            NavigationIcon(navController, "AddTransaction", R.drawable.add)
            NavigationIcon(navController, "Investment", R.drawable.investments)
            NavigationIcon(navController, "Account", R.drawable.account)
        }
    }
}

@Composable
fun NavigationIcon(navController: NavController, route: String, iconResId: Int) {
    Image(painter = painterResource(id = iconResId), contentDescription = route, modifier = Modifier.size(25.dp).clickable { navController.navigate(route) })
}