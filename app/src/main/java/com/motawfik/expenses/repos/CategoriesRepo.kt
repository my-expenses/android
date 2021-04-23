package com.motawfik.expenses.repos

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.motawfik.expenses.models.Category
import com.motawfik.expenses.models.GroupedTransaction
import com.motawfik.expenses.network.CategoriesApi
import com.motawfik.expenses.viewmodel.CATEGORIES_API_STATUS

class CategoriesRepo(val context: Context) {
    private val categoriesDao = TransactionsDatabase.getINSTANCE(context).categoriesDao()
    private val groupedTransactionsDao =
        TransactionsDatabase.getINSTANCE(context).groupedTransactionsDao()
    private val transactionsDao = TransactionsDatabase.getINSTANCE(context).transactionDao()

    suspend fun addCategoriesToDB(
        addedToDBStatus: MutableLiveData<CATEGORIES_API_STATUS>,
        selectedMonth: String
    ) {
        addedToDBStatus.postValue(CATEGORIES_API_STATUS.LOADING)
        categoriesDao.deleteAll() // delete all the categories
        groupedTransactionsDao.deleteAll() // delete all the grouped transactions
        try {
            // get all the categories from the server
            val categories = CategoriesApi.retrofitService.getCategories().await()
            val groupedTransactions =
                CategoriesApi.retrofitService.getGroupedTransactions(selectedMonth).await()
            // insert the fetched categories to the local DB
            groupedTransactionsDao.insertAll(groupedTransactions.groupedTransactions)
            categoriesDao.insertAll(categories.categories)
            addedToDBStatus.postValue(CATEGORIES_API_STATUS.DONE)
        } catch (t: Throwable) {
            t.printStackTrace()
            addedToDBStatus.postValue(CATEGORIES_API_STATUS.ERROR)
        }
    }

    fun getCachedCategories(): LiveData<List<Category>> {
        return categoriesDao.getAll()
    }

    fun getCachedGroupedTransactions(): LiveData<List<GroupedTransaction>> {
        return groupedTransactionsDao.getAll()
    }


    suspend fun deleteCategory(
        categoryID: Int,
        deleteStatus: MutableLiveData<CATEGORIES_API_STATUS>
    ) {
        deleteStatus.postValue(CATEGORIES_API_STATUS.LOADING)
        try {
            CategoriesApi.retrofitService.deleteCategory(categoryID).await()
            categoriesDao.deleteByID(categoryID)
            transactionsDao.setNullCategory(categoryID)
            groupedTransactionsDao.addAmountToUncategorized(categoryID)
            groupedTransactionsDao.deleteByCategoryID(categoryID)
            deleteStatus.postValue(CATEGORIES_API_STATUS.DONE)
        } catch(t: Throwable) {
            t.printStackTrace()
            deleteStatus.postValue(CATEGORIES_API_STATUS.ERROR)
        }

    }
}