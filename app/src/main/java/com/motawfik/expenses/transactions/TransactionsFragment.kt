package com.motawfik.expenses.transactions

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.motawfik.expenses.categories.CATEGORIES_API_STATUS
import com.motawfik.expenses.databinding.FragmentTransactionsBinding
import com.motawfik.expenses.models.Transaction

class TransactionsFragment : Fragment() {
    private lateinit var transactionsViewModel: TransactionsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        // one viewmodel for both the transactions and the details fragments
        transactionsViewModel = ViewModelProvider(requireActivity()).get(TransactionsViewModel::class.java)
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

        val transactionsAdapter = TransactionsAdapter(TransactionListener {
            findNavController().navigate(TransactionsFragmentDirections
                .actionTransactionsFragmentToTransactionDataFragment(
                    it.copy(), transactionsViewModel.categories.value!!.toTypedArray()))
        }, transactionsViewModel.categories)
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

        transactionsViewModel.categoriesStatus.observe(viewLifecycleOwner, {
            when(it) {
                CATEGORIES_API_STATUS.DONE -> {
                    transactionsViewModel.getTransactions()
                    transactionsViewModel.resetCategoriesStatus()
                }
                else -> {}
            }
        })

        // when plus button is pressed, navigate to the data fragment with an empty transaction
        transactionsViewModel.navigateToDataFragment.observe(viewLifecycleOwner, {
            if (it) {
                findNavController().navigate(TransactionsFragmentDirections
                    .actionTransactionsFragmentToTransactionDataFragment(
                        Transaction(), transactionsViewModel.categories.value!!.toTypedArray()))
                transactionsViewModel.resetNavigationToDataFragment()
            }
        })

        return transactionsBinding.root
    }
}