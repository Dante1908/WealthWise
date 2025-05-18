package com.aman.wealthwise.datamodels

data class TransactionInfo(
    var type: String = "Expense",
    var amount: String = "",
    var title: String = "",
    var description: String = "",
    var category: String = "",
    var account: String = "",
    var date: String = "",
    var time: String = "",
    val id: String = ""
)