package com.motawfik.expenses.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.motawfik.expenses.models.Category
import com.motawfik.expenses.models.Transaction

class TransactionDataViewModelFactory(val transaction: Transaction?, val categories: List<Category>): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionDataViewModel::class.java)) {
            return TransactionDataViewModel(transaction, categories) as T
        }
        throw IllegalArgumentException("UNABLE TO CONSTRUCT VIEW MODEL")

    }
}