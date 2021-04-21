package com.motawfik.expenses.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "categories")
@Parcelize
data class Category(
    @PrimaryKey
    val ID: Int,
    val userID: Int,
    var title: String,
) : Parcelable