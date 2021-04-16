package com.motawfik.expenses.transactions

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.motawfik.expenses.models.Transaction

class TransactionDataViewModel(transaction: Transaction?) : ViewModel() {
    val transactionData = MutableLiveData(transaction)
}