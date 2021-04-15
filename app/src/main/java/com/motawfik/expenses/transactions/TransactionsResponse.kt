package com.motawfik.expenses.transactions

import android.os.Parcelable
import com.motawfik.expenses.models.Transaction
import kotlinx.parcelize.Parcelize

@Parcelize
data class TransactionsResponse(
    val transactions: List<Transaction>,
    val numberOfRecords: Int,
) : Parcelable {}