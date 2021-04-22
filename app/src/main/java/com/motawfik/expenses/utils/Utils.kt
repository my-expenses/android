package com.motawfik.expenses.utils

import android.graphics.Color
import android.view.View
import com.google.android.material.snackbar.Snackbar

private fun showSnackbar(rootView: View, color: Int, message: String) {
    Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT)
        .setBackgroundTint(color)
        .setActionTextColor(Color.BLACK)
        .setAction("Close") {}
        .show()
}

fun showSuccessSnackbar(rootView: View, message: String) {
    showSnackbar(rootView, Color.GREEN, message)
}

fun showErrorSnackbar(rootView: View, message: String) {
    showSnackbar(rootView, Color.RED, message)
}