package com.aman.wealthwise.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.aman.wealthwise.R
import com.aman.wealthwise.viewmodels.AuthState
import com.aman.wealthwise.viewmodels.UserAuth

@Composable
fun ForgotPasswordScreen(authViewModel: UserAuth, navController: NavController) {
    val email = remember { mutableStateOf("") }
    val authState by authViewModel.authState.observeAsState()
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(40.dp))
            Image(painter = painterResource(id = R.drawable.forget_password), contentDescription = "Forget Password", modifier = Modifier.size(200.dp))
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "Forgot Password", color = White)
            Spacer(modifier = Modifier.height(12.dp))
            TextField(value = email.value,singleLine = true, modifier = Modifier.fillMaxWidth(0.9f), shape = RoundedCornerShape(10.dp), onValueChange = { email.value = it }, label = { Text(text = "Email") })

            if (authState is AuthState.Error) {
                val errorMessage = (authState as AuthState.Error).message
                Text(text = errorMessage, color = Color.Red, modifier = Modifier.padding(top = 4.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = { authViewModel.forgotPassword(email.value) }, shape = RoundedCornerShape(10.dp)) {
                Text(text = "Send Email Authentication")
            }

            Spacer(modifier = Modifier.height(12.dp))
            TextButton(onClick = { navController.navigate("SignUp") }) {
                Text(text = "Don't have an account? Sign Up")
            }
        }
}
