package com.motawfik.expenses.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.motawfik.expenses.R
import com.motawfik.expenses.adapters.TransactionListener
import com.motawfik.expenses.adapters.TransactionsAdapter
import com.motawfik.expenses.adapters.TransactionsLoadStateAdapter
import com.motawfik.expenses.databinding.FragmentTransactionsBinding
import com.motawfik.expenses.models.Transaction
import com.motawfik.expenses.viewmodel.CategoriesViewModel
import com.motawfik.expenses.viewmodel.TransactionsViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class TransactionsFragment : Fragment() {
    private val transactionsViewModel by sharedViewModel<TransactionsViewModel>()
    private val categoriesViewModel by viewModel<CategoriesViewModel>()
    private lateinit var transactionsBinding: FragmentTransactionsBinding
    private lateinit var transactionsPagingAdapter: TransactionsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        transactionsBinding = FragmentTransactionsBinding.inflate(inflater)
        transactionsBinding.lifecycleOwner = this
        transactionsBinding.viewModel = transactionsViewModel

        return transactionsBinding.root
    }


    @ExperimentalPagingApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val transactionClickListener = TransactionListener(
            {
                transactionsViewModel.initializeTransaction(it.copy())
                findNavController ().navigate(
                    TransactionsFragmentDirections.actionTransactionsFragmentToTransactionDataFragment()
                )
            }, {
                transactionsViewModel.setTransactionToDelete(it)
            }
        )

        transactionsPagingAdapter = TransactionsAdapter(transactionClickListener)
        initAdapter(transactionsPagingAdapter)

        lifecycleScope.launch {
            transactionsViewModel.pager.collectLatest { pagingData ->
                transactionsPagingAdapter.submitData(pagingData)
            }
        }

        categoriesViewModel.categories.observe(viewLifecycleOwner, {
            it?.let {  }
        })


        transactionsViewModel.deletingTransaction.observe(viewLifecycleOwner, {
            // the user clicked on the delete button
            it?.let {
                if (it) {
                    // make sure by displaying a confirmation dialog
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Confirmation")
                        .setMessage("Are you sure you want to delete this item?")
                        .setNeutralButton("Cancel") { _, _ -> }
                        .setPositiveButton("Delete") { _, _ ->
                            transactionsViewModel.deleteTransaction()
                        }
                        .setOnDismissListener {
                            transactionsViewModel.resetTransactionToDelete()
                        }
                        .show()
                }
            }
        })

        transactionsViewModel.startShowingMonthDialog.observe(viewLifecycleOwner, {
            it?.let {
                if (it) {
                    // show date picker when the user clicks on the appbar
                    val datePicker =
                        MaterialDatePicker.Builder.datePicker()
                            .setSelection(transactionsViewModel.transactionsMonth.value?.time)
                            .setTitleText("Select Month")
                            .build()
                    datePicker.show(childFragmentManager, "month_tag")
                    datePicker.addOnPositiveButtonClickListener { selectedDate ->
                        selectedDate?.let {
                            transactionsViewModel.setTransactionsMonth(selectedDate)
                            transactionsPagingAdapter.refresh()
                            transactionsBinding.dateToolBar.title = transactionsViewModel.strTransactionsMonth
                            transactionsViewModel.resetShowingMonthDialog()
                        }
                    }
                }
            }
        })

        // when plus button is pressed, navigate to the data fragment with an empty transaction
        transactionsViewModel.navigateToDataFragment.observe(viewLifecycleOwner, {
            if (it) {
                transactionsViewModel.initializeTransaction(Transaction())
                findNavController().navigate(
                    TransactionsFragmentDirections.actionTransactionsFragmentToTransactionDataFragment()
                )
                transactionsViewModel.resetNavigationToDataFragment()
            }
        })

        transactionsBinding.swipeRefresh.setOnRefreshListener {
            transactionsPagingAdapter.refresh()
            transactionsBinding.transactionsList.scrollToPosition(0)
            categoriesViewModel.addCategoriesToDB(
                transactionsViewModel.transactionsMonth.value!!
            )
        }

        // ge transactions when the user clicks on the refresh button in the toolbar
        transactionsBinding.dateToolBar.menu.findItem(R.id.menu_refresh).setOnMenuItemClickListener {
            it?.let {
                transactionsPagingAdapter.refresh()
                transactionsBinding.transactionsList.scrollToPosition(0)
                categoriesViewModel.addCategoriesToDB(
                    transactionsViewModel.transactionsMonth.value!!
                )
            }
            true
        }

        transactionsBinding.retryButton.setOnClickListener {
            transactionsPagingAdapter.refresh()
        }


        transactionsBinding.executePendingBindings()
    }

    private fun initAdapter(transactionsPagingAdapter: TransactionsAdapter) {
        transactionsPagingAdapter.addLoadStateListener { loadState ->
            transactionsBinding.swipeRefresh.isRefreshing = loadState.refresh is LoadState.Loading
            val isListEmpty = loadState.refresh is LoadState.NotLoading && transactionsPagingAdapter.itemCount == 0
            showEmptyList(isListEmpty)

            // Only show the list if refresh succeeds.
            transactionsBinding.transactionsList.isVisible = loadState.mediator?.refresh is LoadState.NotLoading
            // Show loading spinner during initial load or refresh.
            transactionsBinding.progressBar.isVisible = loadState.mediator?.refresh is LoadState.Loading
            // Show the retry state if initial load or refresh fails.
            transactionsBinding.retryButton.isVisible = loadState.mediator?.refresh is LoadState.Error

            // Toast on any error, regardless of whether it came from RemoteMediator or PagingSource
            val errorState = loadState.source.append as? LoadState.Error
                ?: loadState.source.prepend as? LoadState.Error
                ?: loadState.append as? LoadState.Error
                ?: loadState.prepend as? LoadState.Error
            errorState?.let {
                Toast.makeText(
                    requireContext(),
                    "\uD83D\uDE28 Wooops ${it.error}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        transactionsBinding.transactionsList.adapter = transactionsPagingAdapter.withLoadStateFooter(
            footer = TransactionsLoadStateAdapter { transactionsPagingAdapter.retry() }
        )
    }

    private fun showEmptyList(emptyList: Boolean) {
        if (emptyList) {
            transactionsBinding.emptyList.visibility = View.VISIBLE
            transactionsBinding.transactionsList.visibility = View.GONE
        } else {
            transactionsBinding.emptyList.visibility = View.GONE
            transactionsBinding.transactionsList.visibility = View.VISIBLE
        }
    }
}