package com.motawfik.expenses.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.motawfik.expenses.models.GroupedTransaction

@Dao
interface GroupedTransactionsDao {
    @Query("SELECT * FROM grouped_transactions")
    fun getAll(): LiveData<List<GroupedTransaction>>

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