package com.motawfik.expenses.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.motawfik.expenses.models.GroupedTransaction
import com.motawfik.expenses.models.CategoryWithGroupedTransactions

@Dao
interface GroupedTransactionsDao {
    @Transaction
    @Query("""
        SELECT * FROM grouped_transactions LEFT JOIN categories ON category_id = categoryID
        UNION ALL
        SELECT category_id categoryID, 0 total, * FROM categories WHERE category_id NOT IN (SELECT categoryID FROM grouped_transactions)
        """)
    fun getAll(): LiveData<List<CategoryWithGroupedTransactions>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(groupedTransactions: List<GroupedTransaction>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(groupedTransaction: GroupedTransaction)

    @Query("SELECT * FROM grouped_transactions WHERE categoryID = :categoryID")
    fun getByCategoryID(categoryID: Int): GroupedTransaction

    @Query("DELETE FROM grouped_transactions")
    fun deleteAll()

    @Query("DELETE FROM grouped_transactions WHERE categoryID = :categoryID")
    fun deleteByCategoryID(categoryID: Int)

    @Query("UPDATE grouped_transactions SET total = total + IFNULL((SELECT total FROM grouped_transactions WHERE categoryID = :categoryID), 0) WHERE categoryID = 0")
    fun addAmountToUncategorized(categoryID: Int)
}