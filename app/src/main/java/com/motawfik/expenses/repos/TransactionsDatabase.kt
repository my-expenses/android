package com.motawfik.expenses.repos

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.motawfik.expenses.models.Category
import com.motawfik.expenses.transactions.dao.TransactionDao
import com.motawfik.expenses.transactions.models.Transaction

@Database(entities = [Transaction::class, Category::class], version = 1)
@TypeConverters(DateConverter::class)
abstract class TransactionsDatabase: RoomDatabase() {
    abstract fun transactionDao(): TransactionDao

    companion object {
        private lateinit var INSTANCE: TransactionsDatabase

        fun getINSTANCE(context: Context): TransactionsDatabase {
            synchronized(this) {
                if (!::INSTANCE.isInitialized) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        TransactionsDatabase::class.java,
                        "transactions_database"
                    ).build()
                    return INSTANCE
                }
            }
            return INSTANCE
        }

    }
}