package com.motawfik.expenses.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.motawfik.expenses.models.GroupedTransaction
import com.motawfik.expenses.models.CategoryWithGroupedTransactions

@Dao
interface GroupedTransactionsDao {
    @Transaction
    @Query("SELECT * FROM grouped_transactions")
    fun getAll(): LiveData<List<CategoryWithGroupedTransactions>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(groupedTransactions: List<GroupedTransaction>)

    @Query("SELECT * FROM grouped_transactions WHERE categoryID = :categoryID")
    fun getByCategoryID(categoryID: Int): GroupedTransaction

    @Query("DELETE FROM grouped_transactions")
    fun deleteAll()

    @Query("DELETE FROM grouped_transactions WHERE categoryID = :categoryID")
    fun deleteByCategoryID(categoryID: Int)

    @Query("UPDATE grouped_transactions SET total = total + (SELECT total FROM grouped_transactions WHERE categoryID = :categoryID)")
    fun addAmountToUncategorized(categoryID: Int)
}