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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.aman.wealthwise.R
import com.aman.wealthwise.viewmodels.AuthState
import com.aman.wealthwise.viewmodels.UserAuth

@Composable
fun SignUpScreen(authViewModel: UserAuth,navController: NavController){
    val email = remember { mutableStateOf("") }
    val password1 = remember { mutableStateOf("") }
    val showPassword1 = remember { mutableStateOf(false) }
    val password2 = remember { mutableStateOf("") }
    val showPassword2 = remember { mutableStateOf(false) }
    val authState by authViewModel.authState.observeAsState()
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(40.dp))
            Image(painter = painterResource(id = R.drawable.sign_up), contentDescription = "Sign Up", modifier = Modifier.size(200.dp))
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "CREATE ACCOUNT", color = Color.White)
            Spacer(modifier = Modifier.height(12.dp))
            TextField(value = email.value, modifier = Modifier.fillMaxWidth(0.9f),
                shape = RoundedCornerShape(10.dp),
                onValueChange = { email.value = it },
                singleLine = true,
                label = {
                    Text(text = "Email")
                })
            Spacer(modifier = Modifier.height(12.dp))
            TextField(
                value = password1.value,
                onValueChange = { password1.value = it },
                modifier = Modifier.fillMaxWidth(0.9f),
                shape = RoundedCornerShape(10.dp),
                label = { Text(text = "Password") },
                visualTransformation = if (showPassword1.value) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = { showPassword1.value = !showPassword1.value }) {
                        Icon(
                            painter = painterResource(id = if (showPassword1.value) R.drawable.visible else R.drawable.invisible),
                            contentDescription = if (showPassword1.value) "Hide Password" else "Show Password"
                        )
                    }
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
            TextField(
                value = password2.value,
                onValueChange = { password2.value = it },
                modifier = Modifier.fillMaxWidth(0.9f),
                shape = RoundedCornerShape(10.dp),
                label = { Text(text = "Re-enter Password") },
                visualTransformation = if (showPassword2.value) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = { showPassword2.value = !showPassword2.value }) {
                        Icon(
                            painter = painterResource(id = if (showPassword2.value) R.drawable.visible else R.drawable.invisible),
                            contentDescription = if (showPassword2.value) "Hide Password" else "Show Password"
                        )
                    }
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = { authViewModel.signup(email = email.value, password1 = password1.value, password2 = password2.value) }, shape = RoundedCornerShape(10.dp)) {
                Text(text = "Sign Up")
            }
            if (authState is AuthState.Error) {
                val errorMessage = (authState as AuthState.Error).message
                Text(text = errorMessage, color = Color.Red, modifier = Modifier.padding(top = 4.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            TextButton(onClick = { navController.navigate("Login") }) {
                Text(text = "Already Have an account!! Log In")
            }
        }
}