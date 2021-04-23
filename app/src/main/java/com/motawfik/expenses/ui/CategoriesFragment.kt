package com.motawfik.expenses.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.motawfik.expenses.adapters.CategoriesAdapter
import com.motawfik.expenses.adapters.CategoryListener
import com.motawfik.expenses.databinding.FragmentCategoriesBinding
import com.motawfik.expenses.models.Category
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
        val categoryClickListener = CategoryListener {
            categoriesViewModel.setCategoryToDelete(it)
        }

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
                        .setMessage("Are you sure you want to delete this category?\n" +
                                "All transactions belonging to this category will be uncategorized")
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

        categoriesViewModel.deleteStatus.observe(viewLifecycleOwner, {
            it?.let {
                if (it == CATEGORIES_API_STATUS.DONE) {
                    showSuccessSnackbar(categoriesBinding.root, "Category deleted successfully")
                    categoriesViewModel.resetAddedToDBStatus()
                } else if (it == CATEGORIES_API_STATUS.ERROR) {
                    showErrorSnackbar(categoriesBinding.root, "Error deleting category")
                    categoriesViewModel.resetAddedToDBStatus()
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

        categoriesViewModel.categoriesWithGrouping.observe(viewLifecycleOwner, {
            Log.d("PAIR_CHANGED", "SOMETHING CHANGED")
            categoriesAdapter = CategoriesAdapter(categoryClickListener, it)
            categoriesBinding.categoriesList.adapter = categoriesAdapter
            it.first?.let {  categoriesList ->
                val categories = categoriesList.toMutableList()
                categories.add(Category(0, 0, "Uncategorized"))
                categoriesAdapter.submitList(categories)
                categoriesBinding.swipeRefresh.isRefreshing = false
            }
        })

        categoriesBinding.swipeRefresh.setOnRefreshListener {
            categoriesViewModel.addCategoriesToDB(transactionsViewModel.transactionsMonth.value!!)
        }

        return categoriesBinding.root
    }

}