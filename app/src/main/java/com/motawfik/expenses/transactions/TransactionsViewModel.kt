package com.motawfik.expenses.transactions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.google.gson.Gson
import com.motawfik.expenses.categories.CATEGORIES_API_STATUS
import com.motawfik.expenses.models.Category
import com.motawfik.expenses.models.Transaction
import com.motawfik.expenses.network.CategoriesApi
import com.motawfik.expenses.network.TransactionsApi
import com.motawfik.expenses.network.TransactionsApiService
import com.motawfik.expenses.repos.TransactionsPagingSource
import kotlinx.coroutines.*
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


enum class TRANSACTIONS_API_STATUS { INITIAL, LOADING, ERROR, DONE }

class TransactionsViewModel : ViewModel() {
    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    // transactions from the DB
    private val _transactions = MutableLiveData<List<Transaction>>()
    val transactions: LiveData<List<Transaction>>
        get() = _transactions

    // transaction details to edit
    private val _transactionData = MutableLiveData<Transaction>()
    val transactionData: LiveData<Transaction>
        get() = _transactionData

    fun initializeTransaction(transaction: Transaction) {
        _transactionData.value = transaction
    }

    // categories from db
    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>>
        get() = _categories

    private val _status = MutableLiveData(TRANSACTIONS_API_STATUS.INITIAL)
    val status: LiveData<TRANSACTIONS_API_STATUS>
        get() = _status

    // transaction saving status (create/edit)
    private val _saveStatus = MutableLiveData(TRANSACTIONS_API_STATUS.INITIAL)
    val saveStatus: LiveData<TRANSACTIONS_API_STATUS>
        get() = _saveStatus
    // transaction saving error message (create/edit)
    private val _saveErrorMessage = MutableLiveData("")
    val saveErrorMessage: LiveData<String>
        get() = _saveErrorMessage
    // indicate if user created/updated a transaction
    private val _saved = MutableLiveData(false)
    val saved: LiveData<Boolean>
        get() = _saved

    private val _startShowingMonthDialog = MutableLiveData(false)
    val startShowingMonthDialog: LiveData<Boolean>
        get() = _startShowingMonthDialog

    // boolean to indicate if a deletion is in progress
    private val _deletingTransaction = MutableLiveData(false)
    // integer to hold the ID of the transaction being deleted
    val deletingTransaction: LiveData<Boolean>
        get() = _deletingTransaction
    private val _transactionToDelete = MutableLiveData<Int?>()

    private val _transactionsMonth = MutableLiveData(Date())
    val transactionsMonth: LiveData<Date>
        get() = _transactionsMonth
    val strTransactionsMonth: String
        get() {
             val parser = SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy", Locale.US)
            val formatter = SimpleDateFormat("MMMM yyyy", Locale.US)
            return formatter.format(parser.parse(_transactionsMonth.value.toString())!!)
        }

    val pager = Pager(
        PagingConfig(pageSize = TransactionsPagingSource.PAGE_SIZE)
    ) {
        TransactionsPagingSource(TransactionsApi.retrofitService, _transactionsMonth.value!!)
    }.flow.cachedIn(viewModelScope)

    fun resetFetchStatus() {
        _status.value = TRANSACTIONS_API_STATUS.INITIAL
    }

    fun resetSaveStatus() {
        _saveStatus.value = TRANSACTIONS_API_STATUS.INITIAL
        _saveErrorMessage.value = ""
    }

    fun setTransactionToDelete(transactionID: Int) {
        _deletingTransaction.value = true
        _transactionToDelete.value = transactionID
    }

    fun resetTransactionToDelete() {
        _deletingTransaction.value = false
    }

    fun appBarClicked() {
        _startShowingMonthDialog.value = true
    }

    fun resetShowingMonthDialog() {
        _startShowingMonthDialog.value = false
    }

    fun setTransactionsMonth(date: Long) {
        _transactionsMonth.value = Date(date)
    }

    // function to add updated transaction to the transactions list
    fun addSavedTransactionToList() {
        val transaction = transactionData.value
        transaction?.let {
            if (transaction.isNew) {  // new transaction was created
                val updatedList = _transactions.value?.toMutableList()
                updatedList?.add(transaction) // add transaction to list
                _transactions.value = updatedList!!
            }
            else { // transaction was updated
                val updatedList = _transactions.value?.map {
                    if (it.ID == transaction.ID) transaction
                    else it
                }
                _transactions.value = updatedList!!
            }
            _saved.value = false
        }
    }

    private fun removeDeletedTransactionFromList() {
        // remove deleted transaction from the recyclerview
        val updatedList = _transactions.value?.toMutableList()
        updatedList?.let {
            val deletedIndex = updatedList.indexOfFirst {
                it.ID == _transactionToDelete.value!!
            }
            updatedList.removeAt(deletedIndex)
            _transactions.value = updatedList.toList()
            // reset transactionToDelete
            _transactionToDelete.value = null
        }
    }

    private val _categoriesStatus = MutableLiveData(CATEGORIES_API_STATUS.INITIAL)
    val categoriesStatus: LiveData<CATEGORIES_API_STATUS>
        get() = _categoriesStatus

    fun resetCategoriesStatus() {
        _categoriesStatus.value = CATEGORIES_API_STATUS.INITIAL
    }

    private val _navigateToDataFragment = MutableLiveData(false)
    val navigateToDataFragment: LiveData<Boolean>
        get() = _navigateToDataFragment

    fun startNavigationToDataFragment() {
        _navigateToDataFragment.value = true
    }

    fun resetNavigationToDataFragment() {
        _navigateToDataFragment.value = false
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

    fun deleteTransaction() {
        coroutineScope.launch {
            val deleteTransactionDeferred =
                TransactionsApi.retrofitService.deleteTransaction(_transactionToDelete.value!!)
            try {
                deleteTransactionDeferred.await()
                removeDeletedTransactionFromList()
            } catch(t: Throwable) {
                t.printStackTrace()
            }
        }
    }

    fun saveTransaction() {
        coroutineScope.launch {
            val transaction = _transactionData.value
            if (transaction != null) {
                val saveTransactionDeferred = if (transaction.ID > 0)
                    TransactionsApi.retrofitService.updateTransaction(transaction.ID, transaction)
                else
                    TransactionsApi.retrofitService.createTransaction(transaction)

                try {
                    _saveStatus.value = TRANSACTIONS_API_STATUS.LOADING
                    val response = saveTransactionDeferred.await()
                    response.transaction.isNew = (transaction.ID == 0)
                    _transactionData.value = response.transaction
                    _saveStatus.value = TRANSACTIONS_API_STATUS.DONE
                    _saved.value = true
                } catch (t: Throwable) {
                    when (t) {
                        is HttpException -> {
                            if (t.code() == 400) {
                                val errorResponse = Gson().fromJson(
                                    t.response()?.errorBody()!!.string(), Map::class.java
                                )
                                _saveErrorMessage.value = errorResponse["message"].toString()
                            }
                        }
                        else -> {
                            _saveErrorMessage.value = "Unknown error occurred"
                        }
                    }
                    _saveStatus.value = TRANSACTIONS_API_STATUS.ERROR
                }
            }
        }
    }
}