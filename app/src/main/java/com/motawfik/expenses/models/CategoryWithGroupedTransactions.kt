package com.motawfik.expenses.models

import androidx.room.Embedded
import androidx.room.Relation

data class CategoryWithGroupedTransactions(
    @Embedded val groupedTransaction: GroupedTransaction,
    @Relation(
        parentColumn = "categoryID",
        entityColumn = "category_id"
    )
    val category: Category?,
)