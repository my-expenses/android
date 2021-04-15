package com.motawfik.expenses.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Category(
    val ID: Int,
    val userID: Int,
    var title: String,
) : Parcelable