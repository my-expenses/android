package com.motawfik.expenses.transactions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.motawfik.expenses.categories.CATEGORIES_API_STATUS
import com.motawfik.expenses.models.Category
import com.motawfik.expenses.models.Transaction
import com.motawfik.expenses.network.CategoriesApi
import com.motawfik.expenses.network.TransactionsApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

enum class TRANSACTIONS_API_STATUS { INITIAL, LOADING, ERROR, DONE }

class TransactionsViewModel : ViewModel() {
    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val _transactions = MutableLiveData<List<Transaction>>()
    val transactions: LiveData<List<Transaction>>
        get() = _transactions

    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>>
        get() = _categories

    private val _status = MutableLiveData(TRANSACTIONS_API_STATUS.INITIAL)
    val status: LiveData<TRANSACTIONS_API_STATUS>
        get() = _status

    private val _categoriesStatus = MutableLiveData(CATEGORIES_API_STATUS.INITIAL)
    val categoriesStatus: LiveData<CATEGORIES_API_STATUS>
        get() = _categoriesStatus
    fun resetCategoriesStatus() {
        _categoriesStatus.value = CATEGORIES_API_STATUS.INITIAL
    }

    private val _navigateToDataFragment = MutableLiveData(false)
    val navigateToDataFragment: LiveData<Boolean>
        get() = _navigateToDataFragment
    fun startNavigationToDataFragment () {
        _navigateToDataFragment.value = true
    }
    fun resetNavigationToDataFragment() {
        _navigateToDataFragment.value = false
    }


    fun getTransactions() {
        coroutineScope.launch {
            val getTransactionsDeferred = TransactionsApi.retrofitService.getTransactions(
                0, 10, listOf("created_at"), listOf("true"),
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                    .format(Date())
            )
            try {
                _status.value = TRANSACTIONS_API_STATUS.LOADING
                val response = getTransactionsDeferred.await()
                _status.value = TRANSACTIONS_API_STATUS.DONE
                _transactions.value = response.transactions
            } catch (t: Throwable) {
                t.printStackTrace()
                _status.value = TRANSACTIONS_API_STATUS.ERROR
                _transactions.value = ArrayList()
            }
        }
    }

    fun getCategories() {
        coroutineScope.launch {
            val getCategoriesDeferred = CategoriesApi.retrofitService.getCategories()
            try {
                _categoriesStatus.value = CATEGORIES_API_STATUS.INITIAL
                val response = getCategoriesDeferred.await()
                _categories.value = response.categories
                _categoriesStatus.value = CATEGORIES_API_STATUS.DONE
            } catch (t: Throwable) {
                t.printStackTrace()
                _categoriesStatus.value = CATEGORIES_API_STATUS.ERROR
                _categories.value = ArrayList()
            }
        }
    }
}