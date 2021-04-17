package com.motawfik.expenses.transactions

import android.graphics.Color
import android.util.Log
import android.widget.TextView

import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.chip.ChipGroup
import com.motawfik.expenses.R
import com.motawfik.expenses.models.Category
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter(value = ["amount", "type"])
fun TextView.bindAmountText(amount: Int, type: Boolean) {
     // to display (plus) or (minus) & change text color signs before amount
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

// boilerplate to be able to add the @InverseBindingAdapter to the checked button
@BindingAdapter("checkedBtnAttrChanged")
fun MaterialButtonToggleGroup.setToggleGroupChangedListener(listener: InverseBindingListener) {
    addOnButtonCheckedListener { _, _, _ -> listener.onChange() }
}

@BindingAdapter("checkedBtn")
fun MaterialButtonToggleGroup.bindTypeButton(type: Boolean) {
    // check the correct button based on the type (income or expense)
    check(if (type) R.id.income_button else R.id.expense_button)
}

@InverseBindingAdapter(attribute = "checkedBtn")
fun MaterialButtonToggleGroup.getTypeButton(): Boolean {
    // return true or false based on the selected button
    when(checkedButtonId) {
        R.id.income_button -> return true
        R.id.expense_button -> return false
    }
    return false
}

@BindingAdapter("selectedCategoryIDAttrChanged")
fun ChipGroup.setListeners(attrChange: InverseBindingListener?) =
    setOnCheckedChangeListener { _, _ -> attrChange?.onChange() }

@BindingAdapter(value = ["selectedCategoryID", "categories"])
fun ChipGroup.bindCustomCheckedChip(selectedCategoryID: Int, categories: List<Category>) {
    // find the correct chip and check it based on its ID
    categories.forEach {
        if (it.ID == selectedCategoryID)
            check(it.ID)
    }
}

@InverseBindingAdapter(attribute = "selectedCategoryID")
fun ChipGroup.getCustomCheckedChip(): Int {
    //  return the checkedChip ID as each chip has resourceID which is the same as its database ID
    return checkedChipId
}