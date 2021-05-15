package com.motawfik.expenses.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.motawfik.expenses.network.UsersApi
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
    val isLoading = Transformations.map(_loginStatus) {
        it == LOGIN_STATUS.LOADING
    }

    private val _accessToken = MutableLiveData("")
    val accessToken: LiveData<String>
        get() = _accessToken

    private val _refreshToken = MutableLiveData("")
    val refreshToken: LiveData<String>
        get() = _refreshToken

    private val _navigateToRegister = MutableLiveData(false)
    val navigateToRegister: LiveData<Boolean>
        get() = _navigateToRegister

    fun resetLoginStatus() {
        _loginStatus.value = LOGIN_STATUS.INITIAL
    }

    fun resetAccessToken() {
        _accessToken.value = ""
    }

    fun resetRefreshToken() {
        _refreshToken.value = ""
    }

    fun toRegisterClicked() {
        _navigateToRegister.value = true
    }

    fun resetRegisterClicked() {
        _navigateToRegister.value = false
    }

    fun login() {
        coroutineScope.launch {
            val loginDeferred = UsersApi.retrofitService.login(email.value!!, password.value!!)
            try {
                _loginStatus.value = LOGIN_STATUS.LOADING
                val response = loginDeferred.await()
                _accessToken.value = response["accessToken"]
                _refreshToken.value = response["refreshToken"]
                _loginStatus.value = LOGIN_STATUS.SUCCESS
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