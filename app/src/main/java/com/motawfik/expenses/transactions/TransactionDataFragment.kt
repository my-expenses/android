package com.motawfik.expenses.transactions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.motawfik.expenses.databinding.FragmentTransactionDataBinding

class TransactionDataFragment : Fragment() {
    private lateinit var viewModel: TransactionDataViewModel
    private val args: TransactionDataFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val transactionDataBinding = FragmentTransactionDataBinding.inflate(inflater)

        val transaction = args.transaction
        val transactionDataViewModelFactory = TransactionDataViewModelFactory(transaction)

        viewModel = ViewModelProvider(this, transactionDataViewModelFactory)
            .get(TransactionDataViewModel::class.java)

        transactionDataBinding.viewModel = viewModel

        return transactionDataBinding.root
    }
}