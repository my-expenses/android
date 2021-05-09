package com.motawfik.expenses.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.motawfik.expenses.network.UsersApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {
    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val _userStatus = MutableLiveData<Boolean>()
    val userStatus: LiveData<Boolean>
        get() = _userStatus

    init {
        coroutineScope.launch {
            val apiStatus = UsersApi.retrofitService.getUserStatus()
            try {
                apiStatus.await()
                _userStatus.postValue(true)
            } catch (t: Throwable) {
                _userStatus.postValue(false)
                t.printStackTrace()
            }
        }
    }
}