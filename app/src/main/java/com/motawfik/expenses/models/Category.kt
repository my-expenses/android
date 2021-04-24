package com.motawfik.expenses.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "categories")
@Parcelize
data class Category(
    @PrimaryKey
    @ColumnInfo(name = "category_id")
    val ID: Int,
    @ColumnInfo(name = "user_id")
    val userID: Int,
    @ColumnInfo(name = "category_title")
    var title: String,
) : Parcelable