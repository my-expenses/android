package com.motawfik.expenses.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.motawfik.expenses.network.UsersApi
import com.motawfik.expenses.repos.PrefRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.HttpException

// enum to hold current login status
enum class LOGIN_STATUS { INITIAL, LOADING, SUCCESS, INVALID_CREDENTIALS, INTERNAL_ERROR, UNKNOWN_ERROR }

class UsersLoginViewModel : ViewModel() {
    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    var email = MutableLiveData("")
    var password = MutableLiveData("")

    private var _loginStatus = MutableLiveData(LOGIN_STATUS.INITIAL)
    val loginStatus: LiveData<LOGIN_STATUS>
        get() = _loginStatus
    val loading: Boolean // to disable sign-in button when loading
        get() = _loginStatus.value == LOGIN_STATUS.LOADING

    private val _token = MutableLiveData("")
    val token: LiveData<String>
        get() = _token

    fun resetLoginStatus() {
        _loginStatus.value = LOGIN_STATUS.INITIAL
    }

    fun resetToken() {
        _token.value = ""
    }

    fun login() {
        coroutineScope.launch {
            val loginDeferred = UsersApi.retrofitService.login(email.value!!, password.value!!)
            try {
                _loginStatus.value = LOGIN_STATUS.LOADING
                val response = loginDeferred.await()
                _loginStatus.value = LOGIN_STATUS.SUCCESS
                _token.value = response["token"]
            } catch (t: Throwable) {
                when (t) {
                    is HttpException -> {
                        if (t.code() == 401)
                            _loginStatus.value = LOGIN_STATUS.INVALID_CREDENTIALS
                        else {
                            _loginStatus.value = LOGIN_STATUS.INTERNAL_ERROR
                        }
                    }
                    else -> {
                        _loginStatus.value = LOGIN_STATUS.UNKNOWN_ERROR
                    }
                }
            }
        }
    }
}