package com.motawfik.expenses.models

import android.os.Parcelable
import java.util.*
import kotlinx.parcelize.Parcelize

@Parcelize
data class Transaction(
    val ID: Int = 0,
    val userID: Int = 0,
    var categoryID: Int? = 0,
    var amount: Int = 0,
    var title: String = "",
    var type: Boolean = false,
    var date: Date = Date(),
) : Parcelable {
    var isNew = false
}