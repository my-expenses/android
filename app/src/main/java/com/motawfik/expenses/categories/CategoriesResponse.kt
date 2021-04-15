package com.motawfik.expenses.categories

import android.os.Parcelable
import com.motawfik.expenses.models.Category
import kotlinx.parcelize.Parcelize

@Parcelize
data class CategoriesResponse(
    val categories: List<Category>
) : Parcelable