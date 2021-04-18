package com.motawfik.expenses.transactions

import android.os.Bundle
import android.os.Parcel
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.motawfik.expenses.categories.CATEGORIES_API_STATUS
import com.motawfik.expenses.databinding.FragmentTransactionsBinding
import com.motawfik.expenses.models.Transaction
import java.text.SimpleDateFormat
import java.util.*

class TransactionsFragment : Fragment() {
    private lateinit var transactionsViewModel: TransactionsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        // one viewModel for both the transactions and the details fragments
        transactionsViewModel =
            ViewModelProvider(requireActivity()).get(TransactionsViewModel::class.java)
        // get transactions in onCreate to avoid getting it each time fragment is populated
        // get transactions once and avoid fetching again when back/save button is pressed from the details fragment
        transactionsViewModel.getCategories()
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val transactionsBinding = FragmentTransactionsBinding.inflate(inflater)
        transactionsBinding.viewModel = transactionsViewModel

        val transactionClickListener = TransactionListener(
            {
                transactionsViewModel.initializeTransaction(it.copy())
                findNavController ().navigate(
                    TransactionsFragmentDirections
                        .actionTransactionsFragmentToTransactionDataFragment()
                )
            }, {
                transactionsViewModel.setTransactionToDelete(it)
            }
        )
        val transactionsAdapter = TransactionsAdapter(transactionClickListener,
            transactionsViewModel.categories)

        transactionsBinding.transactionsList.adapter = transactionsAdapter

        transactionsViewModel.transactions.observe(viewLifecycleOwner, {
            transactionsAdapter.submitList(it)
        })

        // listen for (saved) variable to know if there's a new/updated record that has been posted to the DB
        transactionsViewModel.saved.observe(viewLifecycleOwner, {
            it?.let {
                if (it) // if new/updated record
                    transactionsViewModel.addSavedTransactionToList() //show the record in the recyclerview
            }
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
                            transactionsBinding.dateToolBar.title = transactionsViewModel.strTransactionsMonth
                            transactionsViewModel.resetShowingMonthDialog()
                        }
                    }
                }
            }
        })

        transactionsViewModel.categoriesStatus.observe(viewLifecycleOwner, {
            when (it) {
                CATEGORIES_API_STATUS.DONE -> {
                    transactionsViewModel.getTransactions()
                    transactionsViewModel.resetCategoriesStatus()
                }
                else -> {
                }
            }
        })

        // when plus button is pressed, navigate to the data fragment with an empty transaction
        transactionsViewModel.navigateToDataFragment.observe(viewLifecycleOwner, {
            if (it) {
                transactionsViewModel.initializeTransaction(Transaction())
                findNavController().navigate(
                    TransactionsFragmentDirections
                        .actionTransactionsFragmentToTransactionDataFragment()
                )
                transactionsViewModel.resetNavigationToDataFragment()
            }
        })

        return transactionsBinding.root
    }
}