package com.motawfik.expenses.transactions

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.motawfik.expenses.models.Category
import com.motawfik.expenses.models.Transaction

class TransactionDataViewModel(transaction: Transaction?, categories: List<Category>) : ViewModel() {
    val transactionData = MutableLiveData(transaction)
}