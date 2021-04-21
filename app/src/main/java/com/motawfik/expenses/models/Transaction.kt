package com.motawfik.expenses.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.*
import kotlinx.parcelize.Parcelize

@Entity(tableName = "transactions")
@Parcelize
data class Transaction(
    @PrimaryKey
    val ID: Int = 0,
    val userID: Int = 0,
    var categoryID: Int? = 0,
    var amount: Int = 0,
    var title: String = "",
    var type: Boolean = false,
    var date: Date = Date(),
) : Parcelable {
    @Ignore var isNew = false
}