package com.motawfik.expenses.adapters

import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.databinding.BindingAdapter

@BindingAdapter("spinnerStatus") // to show spinner when loading
fun ProgressBar.bindSpinnerStatus(loading: Boolean) {
    Log.d("LOGIN_STATUS", loading.toString())
    visibility = if(loading)
        View.VISIBLE
    else
        View.GONE
}

@BindingAdapter("nonSpinnerStatus") // to show spinner when loading
fun View.bindNonSpinnerStatus(loading: Boolean) {
    visibility = if(loading)
        View.GONE
    else
        View.VISIBLE
}
