package com.motawfik.expenses.models

import android.os.Parcelable
import android.util.Log
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Entity(tableName = "grouped_transactions")
@Parcelize
data class GroupedTransaction(
    @PrimaryKey
    val categoryID: Int?,
    val total: Int,
): Parcelable

class GroupedTransactionJSON(
    val categoryID: Int?,
    val total: Int,
)

@Parcelize
data class GroupedTransactionsResponse(
    val groupedTransactions: List<GroupedTransaction>,
): Parcelable

class GroupedTransactionAdapter {
    @FromJson fun fromJson(groupedTransaction: GroupedTransactionJSON): GroupedTransaction {
        Log.d("JSON_CONV", groupedTransaction.toString())
        if (groupedTransaction.categoryID == null)
            return GroupedTransaction(0, groupedTransaction.total)
        return GroupedTransaction(groupedTransaction.categoryID, groupedTransaction.total)
    }

}