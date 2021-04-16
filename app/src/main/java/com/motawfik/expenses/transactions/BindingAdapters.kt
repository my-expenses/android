package com.motawfik.expenses.transactions

import android.graphics.Color
import android.widget.TextView

import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.google.android.material.button.MaterialButtonToggleGroup
import com.motawfik.expenses.R
import com.motawfik.expenses.models.Category
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter(value = ["amount", "type"])
fun TextView.bindAmountText(amount: Int, type: Boolean) {
    if (type) {
        text = context.getString(R.string.positive_amount, amount)
        setTextColor(Color.GREEN)
    } else {
        text = context.getString(R.string.negative_amount, amount)
        setTextColor(Color.RED)
    }
}

@BindingAdapter("date")
fun TextView.bindDate(date: Date) {
    val parser = SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy", Locale.US)
    val formatter = SimpleDateFormat("EEE, MMM dd, yyyy hh:mm a", Locale.US)
    text = formatter.format(parser.parse(date.toString())!!)
}

@BindingAdapter("categoryName")
fun TextView.bindCategoryName(category: Category?) {
    // if the category is null, then set the text to be "uncategorized"
    // else set the text to be category.title
    text = category?.title ?: context.getString(R.string.uncategorized_chip)
}

@BindingAdapter("checkedBtnAttrChanged")
fun MaterialButtonToggleGroup.setToggleGroupChangedListener(listener: InverseBindingListener) {
    addOnButtonCheckedListener { group, checkedId, isChecked -> listener.onChange() }
}

@BindingAdapter("checkedBtn")
fun MaterialButtonToggleGroup.bindTypeButton(type: Boolean) {
    check(if (type) R.id.income_button else R.id.expense_button)
}

@InverseBindingAdapter(attribute = "checkedBtn")
fun MaterialButtonToggleGroup.getTypeButton(): Boolean {
    when(checkedButtonId) {
        R.id.income_button -> return true
        R.id.expense_button -> return false
    }
    return false
}