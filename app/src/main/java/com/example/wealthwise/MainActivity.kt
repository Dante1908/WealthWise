package com.example.wealthwise

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.commandiron.compose_loading.FoldingCube
import com.example.wealthwise.screens.Home.HomeScreen
import com.example.wealthwise.screens.auth.ForgotPasswordScreen
import com.example.wealthwise.screens.auth.LoginScreen
import com.example.wealthwise.screens.auth.SignUpScreen
import com.example.wealthwise.ui.theme.BackgroundBlue
import com.example.wealthwise.ui.theme.Purple80
import com.example.wealthwise.viewmodels.AuthState
import com.example.wealthwise.viewmodels.UserAuth
import com.google.firebase.FirebaseApp


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()
        setContent {
            LaunchedApp()
        }
    }
}

@Composable
fun LaunchedApp(authViewModel:UserAuth = viewModel()){
    val authState by authViewModel.authState.observeAsState()

    if(authState == AuthState.Loading){
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize().background(color = BackgroundBlue)) {
            FoldingCube(size = DpSize(80.dp, 80.dp), color = Purple80, durationMillisPerFraction = 300)
        }
    }else {
        if(authState==AuthState.Authenticated){
            HomeScreen(authViewModel)
        } else {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "Login") {
                composable("Login") { LoginScreen(navController = navController, authViewModel = authViewModel) }
                composable("ForgotPass") { ForgotPasswordScreen(navController = navController, authViewModel = authViewModel) }
                composable("SignUp") { SignUpScreen(navController = navController, authViewModel = authViewModel) }
            }
        }
    }
}

