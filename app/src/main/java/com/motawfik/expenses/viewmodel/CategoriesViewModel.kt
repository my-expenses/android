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

    // boolean to indicate if a deletion is in progress
    private val _deletingCategory = MutableLiveData(false)
    // integer to hold the ID of the category being deleted
    val deletingCategory: LiveData<Boolean>
        get() = _deletingCategory
    private val _categoryToDelete = MutableLiveData<Int?>()

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

    private val _deleteStatus = MutableLiveData(CATEGORIES_API_STATUS.INITIAL)
    val deleteStatus: LiveData<CATEGORIES_API_STATUS>
        get() = _deleteStatus


    fun setCategoryToDelete(transactionID: Int) {
        _deletingCategory.value = true
        _categoryToDelete.value = transactionID
    }

    fun resetCategoryToDelete() {
        _deletingCategory.value = false
    }

    fun deleteCategory() {
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                categoriesRepo.deleteCategory(_categoryToDelete.value!!, _deleteStatus)
            }
        }
    }

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