package com.example.wealthwise.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.wealthwise.ui.theme.BackgroundBlue
import com.example.wealthwise.ui.theme.Montserrat
import com.example.wealthwise.viewmodels.AuthState
import com.example.wealthwise.viewmodels.UserAuth

@Composable
fun ForgotPasswordScreen(authViewModel: UserAuth, navController: NavController) {
    val email = remember { mutableStateOf("") }
    var isVisible by remember { mutableStateOf(false) }
    val authState by authViewModel.authState.observeAsState()

    LaunchedEffect(Unit) {
        isVisible = true // Triggers animation when the screen is displayed
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInHorizontally(initialOffsetX = { -it }) // Slide from left
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = BackgroundBlue),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.fillMaxHeight(0.4f))
            Text(text = "Forgot Password", color = White)
            Spacer(modifier = Modifier.height(12.dp))
            TextField(
                value = email.value,
                modifier = Modifier.fillMaxWidth(0.9f),
                shape = RoundedCornerShape(10.dp),
                onValueChange = { email.value = it },
                label = { Text(text = "Email") }
            )

            if (authState is AuthState.Error) {
                val errorMessage = (authState as AuthState.Error).message
                Text(text = errorMessage, color = Color.Red, modifier = Modifier.padding(top = 4.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = {
                    authViewModel.forgotPassword(email.value)
                },
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(text = "Send Email Authentication")
            }

            Spacer(modifier = Modifier.height(12.dp))
            TextButton(onClick = { navController.navigate("SignUp") }) {
                Text(text = "Don't have an account? Sign Up")
            }
        }
    }
}
