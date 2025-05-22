package com.aman.wealthwise

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.aman.wealthwise.screens.Home.AccountScreen
import com.aman.wealthwise.screens.Home.AddTransactionScreen
import com.aman.wealthwise.screens.Home.TransactionsScreen
import com.aman.wealthwise.screens.auth.ForgotPasswordScreen
import com.aman.wealthwise.screens.auth.LoginScreen
import com.aman.wealthwise.screens.auth.SignUpScreen
import com.aman.wealthwise.screens.dialogs.ExitConfirmationDialog
import com.aman.wealthwise.screens.dialogs.SettingsDialog
import com.aman.wealthwise.ui.theme.BackgroundBlue
import com.aman.wealthwise.ui.theme.Blue80
import com.aman.wealthwise.ui.theme.Green40
import com.aman.wealthwise.ui.theme.Montserrat
import com.aman.wealthwise.viewmodels.AuthState
import com.aman.wealthwise.viewmodels.TransactionViewModel
import com.aman.wealthwise.viewmodels.UserAuth
import com.aman.wealthwise.widget.AddTransactionWidget
import com.commandiron.compose_loading.FoldingCube
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


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
fun LaunchedApp(authViewModel: UserAuth = viewModel(),transactionViewModel: TransactionViewModel = viewModel()) {
    val authState by authViewModel.authState.observeAsState(AuthState.Unauthenticated)
    val user by authViewModel.user.observeAsState(null)
    val navController = rememberNavController()
    val context = LocalContext.current
    val activity = context as? Activity
    var showExitDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var isBlue by remember { mutableStateOf(true) }

    //Google Account Authentication
    val token = stringResource(id = R.string.Google_Account_Auth_ID)
    val googleSignInLauncher = rememberFirebaseAuthLauncher(onAuthComplete = {authViewModel.handleGoogleAuthResult(it)}, onAuthError = {authViewModel.handleGoogleAuthError(it)})
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(token).requestEmail().build()
    val googleSignInClient = GoogleSignIn.getClient(context,gso)

    if (showExitDialog) {
        ExitConfirmationDialog(onConfirm = { activity?.finish() }, onDismiss = { showExitDialog = false })
    }
    if (showSettingsDialog) {
        SettingsDialog(onDismiss = { showSettingsDialog = false }, onConfirmChanged = { newIsBlue -> isBlue = newIsBlue }, isBluePrev = isBlue)
    }

    LaunchedEffect(authState) {
        when (authState) {
            AuthState.Unauthenticated -> navController.navigate("auth")
            AuthState.Authenticated -> {
                transactionViewModel.fetchTransactions()
                navController.navigate("main")
            }
            AuthState.Loading -> navController.navigate("Loading")
            else -> navController.navigate("Error")
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(color = if (isBlue) BackgroundBlue else Black).padding(10.dp)) {
        Spacer(modifier = Modifier.height(20.dp))
        Row(modifier = Modifier.fillMaxWidth().wrapContentSize(), verticalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.width(10.dp))
            IconButton(modifier = Modifier.size(60.dp), onClick = { showExitDialog = true }) {
                Icon(tint = White, imageVector = Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Close App")
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(text = context.getString(R.string.app_name), style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Blue80), fontFamily = Montserrat)
            Spacer(modifier = Modifier.weight(1f))
            IconButton(modifier = Modifier.size(60.dp), onClick = { showSettingsDialog = true }) {
                Icon(imageVector = Icons.Default.Settings, tint = White, contentDescription = "Settings")
            }
            Spacer(modifier = Modifier.width(10.dp))
        }
        Box(modifier = Modifier.weight(1f).fillMaxSize(), contentAlignment = Alignment.Center){
            NavHost(navController = navController, startDestination = if(authState==AuthState.Unauthenticated) "auth" else if(authState==AuthState.Authenticated) "main" else "Loading") {
                composable(route = "Loading", enterTransition = { fadeIn(animationSpec = tween(300)) }, exitTransition = { fadeOut(animationSpec = tween(300)) }) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        FoldingCube(size = DpSize(80.dp, 80.dp), color = Green40, durationMillisPerFraction = 300)
                    }
                }
                navigation(startDestination = "Login", route = "auth") {
                    //----------------------------------------------------------------------------------------------------------
                    composable(
                        route = "Login",
                        enterTransition = {
                            when (initialState.destination.route) {
                                "ForgotPass" -> slideIntoContainer(towards = AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(durationMillis = 500))
                                "SignUp" -> slideIntoContainer(towards = AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(durationMillis = 500))
                                else -> fadeIn(animationSpec = tween(300))
                            }
                        },
                        exitTransition = {
                            when (targetState.destination.route) {
                                "ForgotPass" -> slideOutOfContainer(towards = AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(durationMillis = 500))
                                "SignUp" -> slideOutOfContainer(towards = AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(durationMillis = 500))
                                else -> fadeOut(animationSpec = tween(300))
                            }
                        }
                    ) {
                        LoginScreen(navController = navController, authViewModel = authViewModel)
                    }
                    //----------------------------------------------------------------------------------------------------------
                    composable(
                        route = "ForgotPass",
                        enterTransition = { slideIntoContainer(towards = AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(durationMillis = 500)) },
                        exitTransition = { slideOutOfContainer(towards = AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(durationMillis = 500))
                        }
                    ) {
                        ForgotPasswordScreen(navController = navController, authViewModel = authViewModel)
                    }
                    //----------------------------------------------------------------------------------------------------------
                    composable(
                        route = "SignUp",
                        enterTransition = { slideIntoContainer(towards = AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(durationMillis = 500)) },
                        exitTransition = { slideOutOfContainer(towards = AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(durationMillis = 500)) }
                    ) {
                        SignUpScreen(navController = navController, authViewModel = authViewModel)
                    }
                }
                navigation(startDestination = "Transactions", route = "main") {
                    //------------------------------------------------------------------------------------------------------------
                    composable(
                        route = "Transactions",
                        enterTransition = { slideIntoContainer(towards = AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(durationMillis = 500)) },
                        exitTransition = { slideOutOfContainer(towards = AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(durationMillis = 500))
                        }
                    ) {
                        TransactionsScreen(navController, transactionViewModel = transactionViewModel)
                    }
                    //----------------------------------------------------------------------------------------------------------
                    composable(
                        route = "AddTransaction",
                        enterTransition = {
                            when (initialState.destination.route) {
                                "Transactions" -> slideIntoContainer(towards = AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(durationMillis = 500))
                                "Account" -> slideIntoContainer(towards = AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(durationMillis = 500))
                                else -> fadeIn(animationSpec = tween(300))
                            }
                        },
                        exitTransition = {
                            when (targetState.destination.route) {
                                "Transactions" -> slideOutOfContainer(towards = AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(durationMillis = 500))
                                "Account" -> slideOutOfContainer(towards = AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(durationMillis = 500))
                                else -> fadeOut(animationSpec = tween(300))
                            }
                        }
                    ) {
                        AddTransactionScreen(navController, transactionViewModel = transactionViewModel, transactionInfo = transactionViewModel.editTransaction.value)
                    }
                    //----------------------------------------------------------------------------------------------------------
                    composable(
                        route = "Account",
                        enterTransition = { slideIntoContainer(towards = AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(durationMillis = 500)) },
                        exitTransition = { slideOutOfContainer(towards = AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(durationMillis = 500)) }
                    ) {
                        AccountScreen(authViewModel = authViewModel, transactionViewModel = transactionViewModel, navController = navController)
                    }
                }
            }
        }
        if(authState==AuthState.Authenticated && user!=null){
            BottomNavBar(navController)
        }else if (authState==AuthState.Unauthenticated){
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()){
                Text(text = "Continue with Google", color = Color.LightGray )
                IconButton(
                    onClick = { googleSignInLauncher.launch(googleSignInClient.signInIntent) },
                    modifier = Modifier.wrapContentSize()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.google_logo),
                        contentDescription = "Google Logo",
                        modifier = Modifier.size(40.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
fun BottomNavBar(navController: NavController){
    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).border(2.dp, White, shape = RoundedCornerShape(10.dp)).background(Black)) {
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
            NavigationIcon(navController, "Transactions", R.drawable.transactions)
            NavigationIcon(navController, "AddTransaction", R.drawable.add)
            NavigationIcon(navController, "Account", R.drawable.account)
        }
    }
}


@Composable
fun NavigationIcon(navController: NavController, route: String, iconResId: Int) {
    Image(painter = painterResource(id = iconResId), contentDescription = route, modifier = Modifier.size(25.dp).clickable { navController.navigate(route) })
}


@Composable
fun rememberFirebaseAuthLauncher(onAuthComplete:(AuthResult)-> Unit, onAuthError:(ApiException)-> Unit): ManagedActivityResultLauncher<Intent, ActivityResult> {
    val scope = rememberCoroutineScope()
    return rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()){ result->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            Log.d("GoogleAuth","account $account")
            val credential = GoogleAuthProvider.getCredential(account.idToken!!,null)
            scope.launch {
                val authResult = Firebase.auth.signInWithCredential(credential).await()
                onAuthComplete(authResult)
            }
        }catch (e: ApiException){
            Log.d("GoogleAuth",e.toString())
            onAuthError(e)
        }
    }
}

class HelloWorldWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = AddTransactionWidget()
}
