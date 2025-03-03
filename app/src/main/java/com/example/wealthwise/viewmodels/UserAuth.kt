package com.example.wealthwise.viewmodels

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wealthwise.datamodels.TransactionInfo
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class UserAuth() : ViewModel() {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _authState = MutableLiveData<AuthState>()
    private val _user = MutableLiveData<FirebaseUser?>()
    val authState : LiveData<AuthState> get() = _authState
    val user : LiveData<FirebaseUser?> get() = _user

    init {
        checkAuthState()
    }

    private fun checkAuthState(){
        if(firebaseAuth.currentUser==null) _authState.value = AuthState.Unauthenticated
        else {
            _authState.value = AuthState.Authenticated
            _user.value = firebaseAuth.currentUser
        }
    }

    fun login(email: String, password: String) {
        if(email.isEmpty() || password.isEmpty()){
            _authState.value = AuthState.Error("Email and Password cannot be empty")
        }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            _authState.value = AuthState.Error("Enter a valid Email !!")
        }else{
            _authState.value = AuthState.Loading
            firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener{
                if(it.isSuccessful) {
                    _authState.value = AuthState.Authenticated
                    _user.value = firebaseAuth.currentUser
                }
                else _authState.value = AuthState.Error(it.exception?.message?:"Something went Wrong!!")
            }
        }
    }

    fun signup(email: String, password1: String,password2:String) {
        if (email.isEmpty() || password1.isEmpty() || password2.isEmpty()) {
            val errorMessage = when {
                email.isEmpty() -> "Email field cannot be empty."
                password1.isEmpty() -> "Password field cannot be empty."
                password2.isEmpty() -> "Confirm Password field cannot be empty."
                else -> "All fields are required."
            }
            _authState.value = AuthState.Error(errorMessage)
        }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            _authState.value = AuthState.Error("Enter a valid Email !!")
        }else if(password1!=password2){
            _authState.value = AuthState.Error("Both Passwords should Match !!")
        }else{
            _authState.value = AuthState.Loading
            firebaseAuth.createUserWithEmailAndPassword(email,password1).addOnCompleteListener{
                if(it.isSuccessful) {
                    _authState.value = AuthState.Authenticated
                    _user.value = firebaseAuth.currentUser
                    firebaseAuth.currentUser?.let {
                        initializeTransactionsDatabase(it.uid)
                    }
                }
                else _authState.value = AuthState.Error(it.exception?.message ?: "Something went Wrong")
            }
        }
    }

    private fun initializeTransactionsDatabase(userId: String) {
        val firestore = FirebaseFirestore.getInstance()
        val transactionsRef = firestore.collection("users").document(userId).collection("transactions")
        val placeholderTransactionInfo = TransactionInfo()
        transactionsRef.add(placeholderTransactionInfo)
            .addOnSuccessListener {
                Log.d("TransactionViewModel", "Initialized transactions database for user: $userId")
            }
            .addOnFailureListener { e ->
                Log.e("TransactionViewModel", "Error initializing transactions: ${e.message}")
            }
    }

    fun signout() {
        firebaseAuth.signOut()
        _user.value = null
        _authState.value = AuthState.Unauthenticated
    }

    fun forgotPassword(email: String) {
        if (email.isEmpty()) {
            _authState.value = AuthState.Error("Email cannot be empty !!")
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _authState.value = AuthState.Error("Enter a valid Email !!")
        } else {
            _authState.value = AuthState.Loading
            firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _authState.value = AuthState.Unauthenticated
                    } else {
                        _authState.value = AuthState.Error(task.exception?.message ?: "Failed to send reset email")
                    }
                }
        }
    }

    fun handleGoogleAuthResult(authResult: AuthResult) {
        _user.value = authResult.user
        _authState.value = AuthState.Authenticated

    }

    fun handleGoogleAuthError(exception: ApiException) {
        _authState.value = AuthState.Error("Google Sign-In failed: ${exception.message}")
    }
}

sealed class AuthState{
    data object Authenticated:AuthState()
    data object Unauthenticated:AuthState()
    data object Loading:AuthState()
    data class Error(val message:String):AuthState()
}

