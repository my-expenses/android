package com.motawfik.expenses.transactions.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.motawfik.expenses.transactions.models.Transaction
import java.util.*

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(transactions: List<Transaction>)

    @Query("SELECT * FROM transactions WHERE date BETWEEN :firstOfMonth AND :lastOfMonth ORDER BY date DESC")
    fun pagingSource(firstOfMonth: Date, lastOfMonth: Date): PagingSource<Int, Transaction>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(vararg transaction: Transaction)

    @Query("DELETE FROM transactions WHERE id = :transactionID")
    suspend fun deleteByID(transactionID: Int)

    @Query("DELETE FROM transactions")
    suspend fun deleteAll()
}