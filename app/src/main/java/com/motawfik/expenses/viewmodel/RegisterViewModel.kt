package com.motawfik.expenses.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.motawfik.expenses.network.UsersApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.HttpException

enum class RegistrationStatus {INITIAL, LOADING, ERROR, DONE}

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

    private val _status = MutableLiveData(RegistrationStatus.INITIAL)
    val status: LiveData<RegistrationStatus>
        get() = _status
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
        _status.value = RegistrationStatus.INITIAL
        _errorMessage.value = null
    }

    fun register() {
        coroutineScope.launch {
        val registration = UsersApi.retrofitService.registerUser(
            firstName.value + " " + lastName.value,
            email.value!!, password.value!!, confirmPassword.value!!
        )
            try {
                _status.value = RegistrationStatus.LOADING
                val response = registration.await()
                _status.value = RegistrationStatus.DONE
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