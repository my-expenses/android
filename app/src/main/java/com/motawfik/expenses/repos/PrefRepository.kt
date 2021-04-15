package com.motawfik.expenses.repos

import android.content.Context
import android.content.SharedPreferences

private const val TOKEN_VALUE = "TOKEN_VALUE"

class PrefRepository(context: Context) {
    private val pref: SharedPreferences =
        context.getSharedPreferences("JWT_TOKEN_FILE", Context.MODE_PRIVATE)

    private val editor = pref.edit()

    private fun String.put(string: String) {
        editor.putString(this, string)
        editor.commit()
    }

    private fun String.getString() = pref.getString(this, "")!!

    fun setTokenValue(token: String) {
        TOKEN_VALUE.put(token)
    }

    fun getTokenValue() = TOKEN_VALUE.getString()
}