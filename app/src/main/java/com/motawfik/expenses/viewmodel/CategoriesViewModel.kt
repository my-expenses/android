package com.motawfik.expenses.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.motawfik.expenses.models.Category
import com.motawfik.expenses.repos.CategoriesRepo
import kotlinx.coroutines.*

enum class CATEGORIES_API_STATUS { INITIAL, LOADING, ERROR, DONE }

class CategoriesViewModel(context: Context) : ViewModel() {
    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val categoriesRepo = CategoriesRepo(context)

    private val _categories = liveData {
        // populate the _categories with the categories returned from the DB
        emitSource(categoriesRepo.getCachedCategories())
    }
    val categories: LiveData<List<Category>>
        get() = _categories

    init {
        addCategoriesToDB()
    }

    private val _addedToDBStatus = MutableLiveData(CATEGORIES_API_STATUS.INITIAL)
    val addedToDBStatus: LiveData<CATEGORIES_API_STATUS>
        get() = _addedToDBStatus

    fun resetAddedToDBStatus() {
        _addedToDBStatus.value = CATEGORIES_API_STATUS.INITIAL
    }

    fun addCategoriesToDB() {
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                categoriesRepo.addCategoriesToDB(_addedToDBStatus)
            }
        }
    }
}