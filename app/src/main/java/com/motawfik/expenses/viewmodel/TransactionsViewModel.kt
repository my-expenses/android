package com.motawfik.expenses.viewmodel

import android.content.Context
import androidx.lifecycle.*
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.google.gson.Gson
import com.motawfik.expenses.models.Transaction
import com.motawfik.expenses.network.TransactionsApi
import com.motawfik.expenses.repos.TransactionsDatabase
import com.motawfik.expenses.repos.TransactionsRemoteMediator
import kotlinx.coroutines.*
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


enum class TRANSACTIONS_API_STATUS { INITIAL, LOADING, ERROR, DONE }

class TransactionsViewModel(context: Context) : ViewModel() {
    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    // transaction details to edit
    private val _transactionData = MutableLiveData<Transaction>()
    val transactionData: LiveData<Transaction>
        get() = _transactionData

    fun initializeTransaction(transaction: Transaction) {
        _transactionData.value = transaction
    }

    // transaction saving status (create/edit)
    private val _saveStatus = MutableLiveData(TRANSACTIONS_API_STATUS.INITIAL)
    val saveStatus: LiveData<TRANSACTIONS_API_STATUS>
        get() = _saveStatus
    val isLoading = Transformations.map(_saveStatus) {
        it == TRANSACTIONS_API_STATUS.LOADING
    }

    // transaction saving error message (create/edit)
    private val _saveErrorMessage = MutableLiveData("")
    val saveErrorMessage: LiveData<String>
        get() = _saveErrorMessage

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

    private val _database = TransactionsDatabase.getINSTANCE(context)

    @ExperimentalPagingApi
    val pager = Pager(
        config = PagingConfig(pageSize = TransactionsRemoteMediator.PAGE_SIZE, enablePlaceholders = false),
        remoteMediator = TransactionsRemoteMediator(
            TransactionsApi.retrofitService,
            _database,
            transactionsMonth)
    ) {
        _database.transactionDao().pagingSource()
    }.flow.cachedIn(viewModelScope)

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

    private val _navigateToDataFragment = MutableLiveData(false)
    val navigateToDataFragment: LiveData<Boolean>
        get() = _navigateToDataFragment

    fun startNavigationToDataFragment() {
        _navigateToDataFragment.value = true
    }

    fun resetNavigationToDataFragment() {
        _navigateToDataFragment.value = false
    }

    fun deleteTransaction() {
        coroutineScope.launch {
            val deleteTransactionDeferred =
                TransactionsApi.retrofitService.deleteTransaction(_transactionToDelete.value!!)
            try {
                deleteTransactionDeferred.await()
                _database.transactionDao().deleteByID(_transactionToDelete.value!!)
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
                    _transactionData.value = response.transaction
                    _database.transactionDao().insertOrUpdate(response.transaction)
                    _saveStatus.value = TRANSACTIONS_API_STATUS.DONE
                } catch (t: Throwable) {
                    t.printStackTrace()
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