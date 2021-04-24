package com.motawfik.expenses.models

import androidx.room.Embedded

data class TransactionWithCategory(
    @Embedded val transaction: Transaction,
    @Embedded val category: Category?,
)