package com.motawfik.expenses.ui

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.motawfik.expenses.viewmodel.CategoriesViewModel

fun showCategoryEditText(
    context: Context,
    categoriesViewModel: CategoriesViewModel,
    categoryTitle: String,
    isNew: Boolean,
): AlertDialog {
    val textInputLayout = TextInputLayout(context)
    textInputLayout.isCounterEnabled = true
    textInputLayout.counterMaxLength = 30

    val textInputEditText = TextInputEditText(context)
    textInputEditText.setText(categoryTitle)
    textInputLayout.isErrorEnabled = true
    textInputLayout.addView(textInputEditText)

    val dialog = MaterialAlertDialogBuilder(context)
        .setTitle("Category Title")
        .setView(textInputLayout)
        .setPositiveButton("Confirm") { _, _ ->
            if (isNew)
                categoriesViewModel.createCategory(textInputEditText.text.toString())
            else
                categoriesViewModel.updateCategory(textInputEditText.text.toString())
        }
        .setNegativeButton("Cancel") { _, _ ->
            categoriesViewModel.resetCategoryToEdit()
        }
        .setOnDismissListener {
            categoriesViewModel.resetCategoryToEdit()
        }
        .create()

    textInputLayout.editText?.doAfterTextChanged { text ->
        text?.let { typedText ->
            val error = categoriesViewModel.validateCategoryName(typedText.toString(), textInputLayout.counterMaxLength)
            textInputLayout.error = error
            Log.d("ERROR_CAT_NAME", error.toString())
            Log.d("ERROR_CAT_NAME", (error!=null).toString())
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = error == null
        }
    }
    return dialog
}