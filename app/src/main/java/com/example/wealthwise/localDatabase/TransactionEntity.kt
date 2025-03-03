package com.example.wealthwise.localDatabase

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String,
    val Expense: Boolean,
    val amount: Double,
    val title: String,
    val category: String,
    val date: String,
    val Note: String
)