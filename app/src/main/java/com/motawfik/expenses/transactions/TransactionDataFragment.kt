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
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
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
        transactionDataBinding.categories = categories.toList()
        viewModel.transactionData.observe(viewLifecycleOwner, {
            it?.let {
                transactionDataBinding.transaction = it
            }
        })


        val chipGroup = transactionDataBinding.chipGroup
        categories.forEach {
            val chip = layoutInflater.inflate(R.layout.single_chip_layout,
                chipGroup, false) as Chip
            chip.id = it.ID
            chip.text = it.title
            chip.isCheckable = true
            chipGroup.addView(chip)
        }

        val selectedTimeCalendar = Calendar.getInstance()
        transactionDataBinding.dateEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            if (transaction?.date != null)
                calendar.time = transaction.date
            else
                calendar.time = Date()

            val datePicker = MaterialDatePicker.Builder.datePicker()
                    .setSelection(transaction?.date?.time)
                    .setTitleText("Select date")
                    .build()
            val timePicker = MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_12H)
                    .setHour(calendar.get(Calendar.HOUR))
                    .setMinute(calendar.get(Calendar.MINUTE))
                    .setTitleText("Select time")
                    .build()

            datePicker.addOnPositiveButtonClickListener {
                selectedTimeCalendar.time = Date(it)
                timePicker.show(childFragmentManager, "time_tag")
            }
            timePicker.addOnPositiveButtonClickListener {
                selectedTimeCalendar.set(Calendar.HOUR, timePicker.hour)
                selectedTimeCalendar.set(Calendar.MINUTE, timePicker.minute)
                transactionDataBinding.transaction?.date = selectedTimeCalendar.time
                viewModel.initializeTransaction(transactionDataBinding.transaction)
            }
            datePicker.show(childFragmentManager, "date_tag")
        }

        return transactionDataBinding.root
    }
}