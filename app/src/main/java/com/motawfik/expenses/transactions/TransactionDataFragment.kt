package com.motawfik.expenses.transactions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.google.android.material.chip.Chip
import com.motawfik.expenses.R
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
        val categories = args.categories
        val transactionDataViewModelFactory = TransactionDataViewModelFactory(
            transaction, categories.toList())

        viewModel = ViewModelProvider(this, transactionDataViewModelFactory)
            .get(TransactionDataViewModel::class.java)

        transactionDataBinding.viewModel = viewModel

        val chipGroup = transactionDataBinding.chipGroup
        categories.forEach {
//            val chip = Chip(chipGroup.context)
            val chip = layoutInflater.inflate(R.layout.single_chip_layout,
                chipGroup, false) as Chip
            chip.text = it.title
            chip.isCheckable = true
            chip.isChecked = transaction?.categoryID == it.ID
            chipGroup.addView(chip)
        }


        return transactionDataBinding.root
    }
}