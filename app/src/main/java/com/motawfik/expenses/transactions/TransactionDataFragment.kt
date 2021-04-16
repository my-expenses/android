package com.motawfik.expenses.transactions

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.MaterialDatePicker
import com.motawfik.expenses.R
import com.motawfik.expenses.databinding.FragmentTransactionDataBinding
import java.util.*

class TransactionDataFragment : Fragment() {
    private val viewModel = TransactionsViewModel()
    private val args: TransactionDataFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val transactionDataBinding = FragmentTransactionDataBinding.inflate(inflater)

        val transaction = args.transaction
        val categories = args.categories

        viewModel.initializeTransaction(transaction)

        transactionDataBinding.viewModel = viewModel
        viewModel.transactionData.observe(viewLifecycleOwner, {
            it?.let {
                transactionDataBinding.transaction = it
            }
        })


        val chipGroup = transactionDataBinding.chipGroup
        categories.forEach {
            val chip = layoutInflater.inflate(R.layout.single_chip_layout,
                chipGroup, false) as Chip
            chip.text = it.title
            chip.isCheckable = true
            chip.isChecked = transaction?.categoryID == it.ID
            chipGroup.addView(chip)
        }

        transactionDataBinding.dateEditText.setOnClickListener {
            viewModel.transactionData.value?.title?.let { it1 -> Log.d("DATE_CLICKED", it1) }
            val datePicker = MaterialDatePicker.Builder.datePicker()
                    .setSelection(transaction?.date?.time)
                    .setTitleText("Select date")
                    .build()
            datePicker.addOnPositiveButtonClickListener {
                transactionDataBinding.transaction?.date = Date(it)
                viewModel.initializeTransaction(transactionDataBinding.transaction)
            }
            datePicker.show(childFragmentManager, "tag")
        }

        return transactionDataBinding.root
    }
}