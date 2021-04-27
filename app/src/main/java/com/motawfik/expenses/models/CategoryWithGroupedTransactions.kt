package com.motawfik.expenses.models

import androidx.room.Embedded
import androidx.room.Relation

data class CategoryWithGroupedTransactions(
    @Embedded val category: Category?,
    @Embedded val groupedTransaction: GroupedTransaction?,
)