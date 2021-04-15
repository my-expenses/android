package com.motawfik.expenses.transactions

import android.graphics.Color
import android.widget.TextView

import androidx.databinding.BindingAdapter
import com.motawfik.expenses.R
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