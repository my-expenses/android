package com.motawfik.expenses.transactions

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.motawfik.expenses.databinding.FragmentTransactionsBinding

class TransactionsFragment : Fragment() {
    private val transactionsViewModel = TransactionsViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val transactionsBinding = FragmentTransactionsBinding.inflate(inflater)
        transactionsBinding.viewModel = transactionsViewModel

        val transactionsAdapter = TransactionsAdapter(TransactionListener {
            Log.d("TRANSACTION_ID", it.toString())
        })
        transactionsBinding.transactionsList.adapter = transactionsAdapter

        transactionsViewModel.getTransactions()
        transactionsViewModel.transactions.observe(viewLifecycleOwner, {
            transactionsAdapter.submitList(it)
        })

        return transactionsBinding.root
    }
}