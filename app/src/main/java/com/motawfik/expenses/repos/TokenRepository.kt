package com.motawfik.expenses.repos

import android.content.Context
import android.content.SharedPreferences

private const val ACCESS_TOKEN_VALUE = "ACCESS_TOKEN_VALUE"
private const val REFRESH_TOKEN_VALUE = "REFRESH_TOKEN_VALUE"

class TokenRepository(context: Context) {
    private val pref: SharedPreferences =
        context.getSharedPreferences("JWT_TOKEN_FILE", Context.MODE_PRIVATE)

    private val editor = pref.edit()

    private fun String.put(string: String) {
        editor.putString(this, string)
        editor.commit()
    }

    private fun String.getString() = pref.getString(this, "")!!

    fun setAccessTokenValue(token: String) {
        ACCESS_TOKEN_VALUE.put(token)
    }

    fun getAccessTokenValue() = ACCESS_TOKEN_VALUE.getString()

    fun setRefreshTokenValue(token: String) {
        REFRESH_TOKEN_VALUE.put(token)
    }

    fun getRefreshTokenValue() = REFRESH_TOKEN_VALUE.getString()
}