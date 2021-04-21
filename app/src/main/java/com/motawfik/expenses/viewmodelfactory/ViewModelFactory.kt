package com.motawfik.expenses.viewmodelfactory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.motawfik.expenses.viewmodel.TransactionsViewModel

class ViewModelFactory(val app: Application): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TransactionsViewModel(app) as T
        }
        throw IllegalArgumentException("Unable to construct viewmodel")
    }
}
