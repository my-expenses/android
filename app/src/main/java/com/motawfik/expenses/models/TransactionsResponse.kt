package com.motawfik.expenses.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TransactionsResponse(
    val transactions: List<Transaction>,
    val numberOfRecords: Int,
) : Parcelable

@Parcelize
data class TransactionResponse(
    val transaction: Transaction
) : Parcelable