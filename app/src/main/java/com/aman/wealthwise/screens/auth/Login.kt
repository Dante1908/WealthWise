package com.aman.wealthwise.screens.auth

import android.content.Intent
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.aman.wealthwise.R
import com.aman.wealthwise.ui.theme.BackgroundBlue
import com.aman.wealthwise.viewmodels.AuthState
import com.aman.wealthwise.viewmodels.UserAuth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun LoginScreen(authViewModel: UserAuth,navController:NavController){
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val showPassword = remember { mutableStateOf(false) }
    val authState by authViewModel.authState.observeAsState()

    val context = LocalContext.current
    val token = stringResource(id = R.string.Google_Account_Auth_ID)
    val googleSignInLauncher = rememberFirebaseAuthLauncher(onAuthComplete = {authViewModel.handleGoogleAuthResult(it)}, onAuthError = {authViewModel.handleGoogleAuthError(it)})
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(token).requestEmail().build()
    val googleSignInClient =GoogleSignIn.getClient(context,gso)

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.fillMaxHeight(0.4f))
            Text(text = "Login",color = Color.White)
            Spacer(modifier = Modifier.height(12.dp))
            TextField(
                value = email.value,
                modifier = Modifier.fillMaxWidth(0.9f),
                shape = RoundedCornerShape(10.dp),
                onValueChange = { email.value = it },
                label = {
                    Text(text = "Email")
                })
            Spacer(modifier = Modifier.height(12.dp))
            TextField(
                value = password.value,
                onValueChange = { password.value = it },
                modifier = Modifier.fillMaxWidth(0.9f),
                shape = RoundedCornerShape(10.dp),
                label = { Text(text = "Password") },
                visualTransformation = if (showPassword.value) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showPassword.value = !showPassword.value }) {
                        Icon(
                            painter = painterResource(id = if (showPassword.value) R.drawable.visible else R.drawable.invisible),
                            contentDescription = if (showPassword.value) "Hide Password" else "Show Password"
                        )
                    }
                }
            )
            Spacer(modifier = Modifier.height(6.dp))
            TextButton(onClick = { navController.navigate("ForgotPass") }) {
                Text(text = "Forgot Password?")
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = { authViewModel.login(email = email.value, password = password.value) }, shape = RoundedCornerShape(10.dp)) {
                Text(text = "LOG IN")
            }
            if(authState is AuthState.Error){
                val errorMessage = (authState as AuthState.Error).message
                Text(errorMessage,color = Color.Red)
            }
            Spacer(modifier = Modifier.height(12.dp))
            TextButton(onClick = { navController.navigate("SignUp") }) {
                Text(text = "Don't have an account? Sign Up")
            }
            Spacer(modifier = Modifier.fillMaxHeight(0.5f))
            Text(text = "OR Sign Up with",color = Color.LightGray)
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
        }

}


@Composable
fun rememberFirebaseAuthLauncher(onAuthComplete:(AuthResult)-> Unit, onAuthError:(ApiException)-> Unit): ManagedActivityResultLauncher<Intent, ActivityResult> {
    val scope = rememberCoroutineScope()
    return rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            Log.d("GoogleAuth","account $account")
            val credential = GoogleAuthProvider.getCredential(account.idToken!!,null)
            scope.launch {val authResult = Firebase.auth.signInWithCredential(credential).await()
                onAuthComplete(authResult)
            }
        }catch (e: ApiException){
            Log.d("GoogleAuth",e.toString())
            onAuthError(e)
        }
    }
}