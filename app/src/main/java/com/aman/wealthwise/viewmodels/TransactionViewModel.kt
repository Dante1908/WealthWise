package com.aman.wealthwise.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aman.wealthwise.datamodels.TransactionInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class TransactionViewModel : ViewModel() {
    private val _transactions = MutableLiveData<List<TransactionInfo>>()
    private val _transactionState = MutableLiveData<TransactionsState>(TransactionsState.Loading)
    private var _user: FirebaseUser? = null
    private var _transactionRef = _user?.let { FirebaseFirestore.getInstance().collection("users").document(it.uid).collection("transactions") }
    private val _editTransaction = mutableStateOf<TransactionInfo?>(null)
    val editTransaction: State<TransactionInfo?> = _editTransaction
    val transactions: LiveData<List<TransactionInfo>> get() = _transactions
    val transactionState: LiveData<TransactionsState> get() = _transactionState

    private var listenerRegistration: ListenerRegistration? = null

    fun setEditTransaction(transaction: TransactionInfo) {
        _editTransaction.value = transaction
    }

    fun fetchTransactions() {
        _transactionState.value = TransactionsState.Loading
        _user = FirebaseAuth.getInstance().currentUser
        _transactionRef = FirebaseFirestore.getInstance().collection("users").document(_user!!.uid).collection("transactions")
        if (_user == null || _transactionRef == null) {
            _transactionState.value = TransactionsState.Error("User Not Logged In !!")
            return
        }
        listenerRegistration?.remove()
        listenerRegistration = _transactionRef!!.orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    _transactionState.value = TransactionsState.Error(error.message ?: "Something went wrong!!")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val fetchedTransactions = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(TransactionInfo::class.java)?.copy(id = doc.id)
                    }
                    _transactions.value = fetchedTransactions
                    _transactionState.value = TransactionsState.Done
                }
            }
    }

    fun addTransaction(transactionInfo: TransactionInfo) {
        if (_user == null || _transactionRef == null) {
            _transactionState.value = TransactionsState.Error("User Not Logged In !!")
            return
        }

        _transactionState.value = TransactionsState.Loading
        val newDocRef = _transactionRef!!.document()
        val transactionWithId = transactionInfo.copy(id = newDocRef.id)
        newDocRef.set(transactionWithId).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _transactionState.value = TransactionsState.Done
            } else {
                _transactionState.value = TransactionsState.Error(task.exception?.message ?: "Failed to add transaction")
            }
        }
    }

    fun deleteTransaction(transactionId: String) {
        if (_user == null || _transactionRef == null) {
            _transactionState.value = TransactionsState.Error("User Not Logged In !!")
            return
        }

        _transactionState.value = TransactionsState.Loading
        _transactionRef!!.document(transactionId).delete().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _transactionState.value = TransactionsState.Done
            } else {
                _transactionState.value = TransactionsState.Error(task.exception?.message ?: "Failed to delete transaction")
            }
        }
    }

    fun updateTransaction(transactionInfo: TransactionInfo) {
        if (_user == null || _transactionRef == null) {
            _transactionState.value = TransactionsState.Error("User Not Logged In !!")
            return
        }

        _transactionState.value = TransactionsState.Loading
        _transactionRef!!.document(transactionInfo.id).set(transactionInfo).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _transactionState.value = TransactionsState.Done
            } else {
                _transactionState.value = TransactionsState.Error(task.exception?.message ?: "Failed to Update transaction")
            }
        }
    }

    fun clearUserData() {
        _transactions.value = emptyList()
        listenerRegistration?.remove()
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}

sealed class TransactionsState {
    data object Done : TransactionsState()
    data object Loading : TransactionsState()
    data class Error(val message: String) : TransactionsState()
}