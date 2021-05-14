package com.motawfik.expenses.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RegisterViewModel : ViewModel() {
    private val _backToLogin = MutableLiveData(false)
    val backToLogin: LiveData<Boolean>
        get() = _backToLogin

    fun navigateToLogin() {
        _backToLogin.value = true
    }

    fun resetNavigateToLogin() {
        _backToLogin.value = false
    }
}