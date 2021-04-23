package com.motawfik.expenses.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import com.motawfik.expenses.models.Category
import com.motawfik.expenses.models.GroupedTransaction

class CategoryGroupedLiveData(
    private val categories: LiveData<List<Category>>,
    private val groupedTransactions: LiveData<List<GroupedTransaction>>,
) : MediatorLiveData<Pair<List<Category>?, List<GroupedTransaction>?>>() {

    init {
        addSource(categories) {
            it?.let {
                if (groupedTransactions.value != null)
                    value = it to groupedTransactions.value
            }
        }
        addSource(groupedTransactions) {
            it?.let {
                if (categories.value != null)
                    value = categories.value to it
            }
        }
    }

}