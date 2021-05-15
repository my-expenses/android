package com.motawfik.expenses.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.motawfik.expenses.network.UsersApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.HttpException

enum class REGISTRATION_STATUS {INITIAL, LOADING, ERROR, DONE}

class RegisterViewModel : ViewModel() {
    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val _backToLogin = MutableLiveData(false)
    val backToLogin: LiveData<Boolean>
        get() = _backToLogin

    val firstName = MutableLiveData("")
    val lastName = MutableLiveData("")
    val email = MutableLiveData("")
    val password = MutableLiveData("")
    val confirmPassword = MutableLiveData("")

    private val _status = MutableLiveData(REGISTRATION_STATUS.INITIAL)
    val status: LiveData<REGISTRATION_STATUS>
        get() = _status
    val isLoading = Transformations.map(_status) {
        it == REGISTRATION_STATUS.LOADING
    }

    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?>
        get() = _errorMessage

    fun navigateToLogin() {
        _backToLogin.value = true
    }

    fun resetNavigateToLogin() {
        _backToLogin.value = false
    }

    fun resetRegisterStatus() {
        _status.value = REGISTRATION_STATUS.INITIAL
        _errorMessage.value = null
    }

    fun register() {
        coroutineScope.launch {
        val registration = UsersApi.retrofitService.registerUser(
            firstName.value + " " + lastName.value,
            email.value!!, password.value!!, confirmPassword.value!!
        )
            try {
                _status.value = REGISTRATION_STATUS.LOADING
                registration.await()
                _status.value = REGISTRATION_STATUS.DONE
            } catch (t: Throwable) {
                t.printStackTrace()
                when (t) {
                    is HttpException -> {
                        if (t.code() in 400..499) {
                            val errorResponse = Gson().fromJson(
                                t.response()?.errorBody()!!.string(), Map::class.java
                            )
                            _errorMessage.value = errorResponse["message"].toString()
                        }
                    }
                    else -> {
                        _errorMessage.value = "Unknown error occurred"
                    }
                }
            }
        }
    }
}