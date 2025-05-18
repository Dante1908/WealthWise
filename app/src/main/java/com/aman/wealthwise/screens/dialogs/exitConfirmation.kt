package com.aman.wealthwise.screens.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.aman.wealthwise.ui.theme.Montserrat


@Composable
fun ExitConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp), shape = RoundedCornerShape(16.dp), color = White
        ){
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
                        .padding(start = 8.dp), colors = ButtonDefaults.buttonColors(containerColor = Red), shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("Confirm", color = White, fontFamily = Montserrat)
                    }
                }
            }
        }
    }
}