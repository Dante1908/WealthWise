package com.aman.wealthwise.screens.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.aman.wealthwise.ui.theme.BackgroundBlue
import com.aman.wealthwise.ui.theme.Montserrat


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
            .padding(16.dp), shape = RoundedCornerShape(16.dp), color = White
        ){
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