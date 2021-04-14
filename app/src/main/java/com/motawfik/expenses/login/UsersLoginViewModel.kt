package com.motawfik.expenses.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UsersLoginViewModel : ViewModel() {
    var email = MutableLiveData<String>()
    var password = MutableLiveData<String>()

    private var _loadingLogin = MutableLiveData<Boolean>()
    val loginLoading: LiveData<Boolean>
        get() = _loadingLogin

    init {
        email.value = ""
        password.value = ""
    }
}