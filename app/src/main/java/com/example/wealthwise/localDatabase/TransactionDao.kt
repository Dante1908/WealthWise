package com.example.wealthwise.localDatabase

import androidx.room.*

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): List<TransactionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTransaction(transaction: TransactionEntity)

    @Delete
    fun deleteTransaction(transaction: TransactionEntity)

    @Query("DELETE FROM transactions WHERE id = :transactionId")
    fun deleteById(transactionId: String)
}
