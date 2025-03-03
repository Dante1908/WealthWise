package com.example.wealthwise.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wealthwise.datamodels.TransactionInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class TransactionViewModel : ViewModel() {
    private val _transactions = MutableLiveData<List<TransactionInfo>>()
    private val _transactionState = MutableLiveData<TransactionsState>(TransactionsState.Loading)
    private val _user: FirebaseUser? get() = FirebaseAuth.getInstance().currentUser
    private val _transactionRef = _user?.let { FirebaseFirestore.getInstance().collection("users").document(it.uid).collection("transactions") }
    private val _editTransaction = mutableStateOf<TransactionInfo?>(null)

    val editTransaction: State<TransactionInfo?> = _editTransaction
    val transactions: LiveData<List<TransactionInfo>> get() = _transactions
    val transactionState: LiveData<TransactionsState> get() = _transactionState

    fun setEditTransaction(transaction: TransactionInfo) {
        _editTransaction.value = transaction
    }

    fun fetchTransactions() {

        if (_transactions.value?.isNotEmpty() == true) return
        _transactionState.value = TransactionsState.Loading

        if(_user==null || _transactionRef == null){
            _transactionState.value = TransactionsState.Error("User Not Logged In !!")
            return
        }

        _transactionRef.orderBy("date", Query.Direction.DESCENDING).get()
            .addOnCompleteListener {task->
                if(task.isSuccessful){
                    _transactionState.value = TransactionsState.Done
                    val fetchedTransactions = task.result?.documents?.mapNotNull { doc->
                        doc.toObject(TransactionInfo::class.java)?.copy(id = doc.id)
                    }?: emptyList()
                    _transactions.value = fetchedTransactions
                }
                else _transactionState.value = TransactionsState.Error(task.exception?.message?:"Something went Wrong!!")
            }
    }

    fun addTransaction(transactionInfo: TransactionInfo) {

        if(_user==null || _transactionRef == null){
            _transactionState.value = TransactionsState.Error("User Not Logged In !!")
            return
        }

        _transactionState.value = TransactionsState.Loading
        val newDocRef = _transactionRef.document()
        val transactionWithId = transactionInfo.copy(id = newDocRef.id)
        newDocRef.set(transactionWithId)
        _transactionRef.document().set(transactionWithId).addOnCompleteListener {task->
                if(task.isSuccessful){
                    val updatedList = _transactions.value.orEmpty().toMutableList()
                    updatedList.add(0,transactionWithId)
                    _transactions.postValue(updatedList)
                    _transactionState.value = TransactionsState.Done
                }else _transactionState.value = TransactionsState.Error(task.exception?.message?: "Failed to add transaction")
        }
    }

    fun deleteTransaction(transactionId: String) {

        if(_user==null || _transactionRef == null){
            _transactionState.value = TransactionsState.Error("User Not Logged In !!")
            return
        }

        _transactionState.value = TransactionsState.Loading
        _transactionRef.document(transactionId).delete().addOnCompleteListener {task->
                if(task.isSuccessful){
                    _transactionState.value = TransactionsState.Done
                    val updateList = _transactions.value?.filterNot { it.id == transactionId }
                    _transactions.postValue(updateList!!)
                }else _transactionState.value = TransactionsState.Error(task.exception?.message?: "Failed to delete transaction")
        }
    }

    fun updateTransaction(transactionInfo: TransactionInfo) {

        if(_user==null || _transactionRef == null){
            _transactionState.value = TransactionsState.Error("User Not Logged In !!")
            return
        }

        _transactionState.value = TransactionsState.Loading
        _transactionRef.document(transactionInfo.id).set(transactionInfo).addOnCompleteListener {task->
            if (task.isSuccessful) {
                val updatedList = _transactions.value?.map { transaction ->
                    if (transaction.id == transactionInfo.id) transactionInfo else transaction
                } ?: emptyList()
                _transactions.postValue(updatedList)
                _transactionState.value = TransactionsState.Done
            } else _transactionState.value = TransactionsState.Error(task.exception?.message ?: "Failed to Update transaction")
        }
    }
    fun clearUserData() {
        _transactions.value = emptyList()
    }
}

sealed class TransactionsState {
    data object Done : TransactionsState()
    data object Loading : TransactionsState()
    data class Error(val message: String) : TransactionsState()
}