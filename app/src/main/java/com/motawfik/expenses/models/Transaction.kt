package com.motawfik.expenses.models

import android.os.Parcelable
import java.util.*
import kotlinx.parcelize.Parcelize

@Parcelize
data class Transaction(
    val ID: Int,
    val userID: Int,
    var categoryID: Int?,
    var amount: Int,
    var title: String,
    var type: Boolean,
    var date: Date,
) : Parcelable