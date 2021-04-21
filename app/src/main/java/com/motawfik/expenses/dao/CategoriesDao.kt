package com.motawfik.expenses.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.motawfik.expenses.models.Category

@Dao
interface CategoriesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(categories: List<Category>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(vararg category: Category)

    @Query("SELECT * FROM categories ORDER BY ID")
    fun getAll(): LiveData<List<Category>>

    @Query("DELETE FROM categories WHERE id = :categoryID")
    fun deleteByID(categoryID: Int)

    @Query("DELETE FROM categories")
    fun deleteAll()
}