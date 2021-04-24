package com.motawfik.expenses.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.motawfik.expenses.models.Transaction
import com.motawfik.expenses.models.TransactionWithCategory

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(transactions: List<Transaction>)

    @Query("""
        SELECT * FROM transactions
        LEFT JOIN categories ON transactions.categoryID = categories.category_id
        ORDER BY date DESC
        """)
    fun pagingSource(): PagingSource<Int, TransactionWithCategory>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(vararg transaction: Transaction)

    @Query("DELETE FROM transactions WHERE id = :transactionID")
    suspend fun deleteByID(transactionID: Int)

    @Query("DELETE FROM transactions")
    suspend fun deleteAll()

    // this function to be executed when a category is deleted
    // so we need to uncategorize all the transactions in this category
    @Query("UPDATE transactions SET categoryID = null WHERE categoryID = :categoryID")
    fun setNullCategory(categoryID: Int)
}