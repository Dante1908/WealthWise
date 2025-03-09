package com.example.wealthwise

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.commandiron.compose_loading.FoldingCube
import com.example.wealthwise.screens.Home.HomeScreen
import com.example.wealthwise.screens.auth.ForgotPasswordScreen
import com.example.wealthwise.screens.auth.LoginScreen
import com.example.wealthwise.screens.auth.SignUpScreen
import com.example.wealthwise.ui.theme.BackgroundBlue
import com.example.wealthwise.ui.theme.Green40
import com.example.wealthwise.viewmodels.AuthState
import com.example.wealthwise.viewmodels.UserAuth
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.delay


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
fun LaunchedApp(authViewModel: UserAuth = viewModel()) {
    val authState by authViewModel.authState.observeAsState()
    val navController = rememberNavController()
    LaunchedEffect(authState) {
        when (authState) {
            AuthState.Loading -> navController.navigate("auth_loading")
            AuthState.Authenticated -> navController.navigate("authenticated") {
                popUpTo("splash_screen") { inclusive = true }
            }
            else -> navController.navigate("Login") {
                popUpTo("splash_screen") { inclusive = true }
            }
        }
    }
    NavHost(navController = navController, startDestination = "splash_screen") {
        composable("splash_screen") {
            SplashScreen(navController)
        }
        composable("auth_loading") {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize().background(color = BackgroundBlue)) {
                FoldingCube(size = DpSize(80.dp, 80.dp), color = Green40, durationMillisPerFraction = 300)
            }
        }
        composable("authenticated") { HomeScreen(authViewModel) }
        composable("Login") { LoginScreen(navController = navController, authViewModel = authViewModel) }
        composable("ForgotPass") { ForgotPasswordScreen(navController = navController, authViewModel = authViewModel) }
        composable("SignUp") { SignUpScreen(navController = navController, authViewModel = authViewModel) }
    }

}

@Composable
fun SplashScreen(navController: NavController) {
    val scale by animateFloatAsState(targetValue = 1f, animationSpec = tween(durationMillis = 1500))
    Box(modifier = Modifier.fillMaxSize().background(Color.White), contentAlignment = Alignment.Center) {
        Image(painter = painterResource(id = R.drawable.app_logo), contentDescription = "App Logo", modifier = Modifier.size(100.dp * scale))
    }
    LaunchedEffect(Unit) {
        delay(5000) // Show splash for 2 seconds with expanding animation
        navController.navigate("Login") {
            popUpTo("splash_screen") { inclusive = true }
        }
    }
}