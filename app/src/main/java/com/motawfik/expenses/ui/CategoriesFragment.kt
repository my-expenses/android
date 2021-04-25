package com.motawfik.expenses.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.motawfik.expenses.adapters.CategoriesAdapter
import com.motawfik.expenses.adapters.CategoryListener
import com.motawfik.expenses.databinding.FragmentCategoriesBinding
import com.motawfik.expenses.utils.showErrorSnackbar
import com.motawfik.expenses.utils.showSuccessSnackbar
import com.motawfik.expenses.viewmodel.CATEGORIES_API_STATUS
import com.motawfik.expenses.viewmodel.CategoriesViewModel
import com.motawfik.expenses.viewmodel.TransactionsViewModel
import com.motawfik.expenses.viewmodelfactory.ViewModelFactory

class CategoriesFragment : Fragment() {
    private lateinit var categoriesViewModel: CategoriesViewModel
    private lateinit var transactionsViewModel: TransactionsViewModel
    private lateinit var categoriesAdapter: CategoriesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        val app = requireNotNull(this.activity).application
        val viewModelFactory = ViewModelFactory(app)
        categoriesViewModel =
            ViewModelProvider(
                requireActivity(),
                viewModelFactory
            ).get(CategoriesViewModel::class.java)

        transactionsViewModel =
            ViewModelProvider(
                requireActivity(),
                viewModelFactory
            ).get(TransactionsViewModel::class.java)

        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val categoriesBinding = FragmentCategoriesBinding.inflate(inflater)
        categoriesBinding.viewModel = categoriesViewModel

        categoriesBinding.lifecycleOwner = this
        val categoryClickListener = CategoryListener(
            { category -> categoriesViewModel.setCategoryToEdit(category) },
            { categoryID -> categoriesViewModel.setCategoryToDelete(categoryID) }
        )

        categoriesAdapter = CategoriesAdapter(categoryClickListener)
        categoriesBinding.categoriesList.adapter = categoriesAdapter

        transactionsViewModel.transactionsMonth.observe(viewLifecycleOwner, {
            it?.let {
                categoriesViewModel.addCategoriesToDB(it)
            }
        })

        categoriesViewModel.deletingCategory.observe(viewLifecycleOwner, {
            it?.let {
                if (it) {
                    // make sure by displaying a confirmation dialog
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Confirmation")
                        .setMessage(
                            "Are you sure you want to delete this category?\n" +
                                    "All transactions belonging to this category will be uncategorized"
                        )
                        .setNeutralButton("Cancel") { _, _ -> }
                        .setPositiveButton("Delete") { _, _ ->
                            categoriesViewModel.deleteCategory()
                        }
                        .setOnDismissListener {
                            categoriesViewModel.resetCategoryToDelete()
                        }
                        .show()
                }
            }
        })

        categoriesViewModel.updateStatus.observe(viewLifecycleOwner, {
            it?.let {
                if (it == CATEGORIES_API_STATUS.DONE) {
                    showSuccessSnackbar(categoriesBinding.root, "Category updated successfully")
                    categoriesViewModel.resetUpdateStatus()
                } else if (it == CATEGORIES_API_STATUS.ERROR) {
                    showErrorSnackbar(categoriesBinding.root, "Error occurred")
                    categoriesViewModel.resetUpdateStatus()
                }
            }
        })

        categoriesViewModel.categoryToEdit.observe(viewLifecycleOwner, {
            it?.let {
                if (it.ID != 0) {
                    val textInputLayout = TextInputLayout(requireContext())
                    textInputLayout.isCounterEnabled = true
                    textInputLayout.counterMaxLength = 30

                    val textInputEditText = TextInputEditText(requireContext())
                    textInputEditText.setText(it.title)
                    textInputLayout.isErrorEnabled = true
                    textInputLayout.addView(textInputEditText)

                    val dialog = MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Category Title")
                        .setView(textInputLayout)
                        .setPositiveButton("Confirm") { _, _ ->
                            categoriesViewModel.updateCategory(textInputEditText.text.toString())
                        }
                        .setNegativeButton("Cancel") { _, _ ->
                            categoriesViewModel.resetCategoryToEdit()
                        }
                        .setOnDismissListener {
                            categoriesViewModel.resetCategoryToEdit()
                        }
                        .create()
                    dialog.show()

                    textInputLayout.editText?.doAfterTextChanged { text ->
                        text?.let { typedText ->
                            when {
                                typedText.length > textInputLayout.counterMaxLength -> {
                                    textInputLayout.error = "Exceeding max length"
                                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
                                }
                                typedText.isEmpty() -> {
                                    textInputLayout.error = "Category name cannot be empty"
                                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
                                }
                                else -> {
                                    textInputLayout.error = null
                                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = true
                                }
                            }
                        }
                    }
                }
            }
        })

        categoriesViewModel.deleteStatus.observe(viewLifecycleOwner, {
            it?.let {
                if (it == CATEGORIES_API_STATUS.DONE) {
                    showSuccessSnackbar(categoriesBinding.root, "Category deleted successfully")
                    categoriesViewModel.resetDeleteStatus()
                } else if (it == CATEGORIES_API_STATUS.ERROR) {
                    showErrorSnackbar(categoriesBinding.root, "Error deleting category")
                    categoriesViewModel.resetDeleteStatus()
                }
            }
        })

        categoriesViewModel.addedToDBStatus.observe(viewLifecycleOwner, {
            it?.let {
                if (it == CATEGORIES_API_STATUS.LOADING) {
                    categoriesBinding.swipeRefresh.isRefreshing = true
                }
            }
        })

        categoriesViewModel.groupedTransactions.observe(viewLifecycleOwner, {
            it?.let {
                categoriesAdapter.submitList(it)
                categoriesBinding.swipeRefresh.isRefreshing = false
            }
        })

        categoriesBinding.swipeRefresh.setOnRefreshListener {
            categoriesViewModel.addCategoriesToDB(transactionsViewModel.transactionsMonth.value!!)
        }

        return categoriesBinding.root
    }

}