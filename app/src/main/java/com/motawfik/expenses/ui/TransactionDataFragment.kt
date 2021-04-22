package com.motawfik.expenses.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.motawfik.expenses.R
import com.motawfik.expenses.databinding.FragmentTransactionDataBinding
import com.motawfik.expenses.utils.showErrorSnackbar
import com.motawfik.expenses.utils.showSuccessSnackbar
import com.motawfik.expenses.viewmodel.CategoriesViewModel
import com.motawfik.expenses.viewmodel.TRANSACTIONS_API_STATUS
import com.motawfik.expenses.viewmodel.TransactionsViewModel
import java.util.*

class TransactionDataFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModel = ViewModelProvider(requireActivity()).get(TransactionsViewModel::class.java)
        val categoriesViewModel = ViewModelProvider(requireActivity()).get(CategoriesViewModel::class.java)

        val transactionDataBinding = FragmentTransactionDataBinding.inflate(inflater)

        transactionDataBinding.viewModel = viewModel
        viewModel.transactionData.observe(viewLifecycleOwner, {
            it?.let {
                transactionDataBinding.transaction = it
            }
        })

        val chipGroup = transactionDataBinding.chipGroup
        categoriesViewModel.categories.value?.forEach {
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
            calendar.time = viewModel.transactionData.value?.date!!

            val datePicker = MaterialDatePicker.Builder.datePicker()
                    .setSelection(viewModel.transactionData.value?.date!!.time)
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
                viewModel.transactionData.value?.date = selectedTimeCalendar.time
                viewModel.initializeTransaction(viewModel.transactionData.value!!)
            }
            datePicker.show(childFragmentManager, "date_tag")
        }


        viewModel.saveStatus.observe(viewLifecycleOwner, {
            it?.let {
                if (it == TRANSACTIONS_API_STATUS.DONE) {
                    showSuccessSnackbar(transactionDataBinding.root, "Transaction Saved Successfully")
                    findNavController().popBackStack()
                    viewModel.resetSaveStatus()
                } else if (it == TRANSACTIONS_API_STATUS.ERROR) {
                    showErrorSnackbar(transactionDataBinding.root, viewModel.saveErrorMessage.value!!)
                    viewModel.resetSaveStatus()
                }
            }
        })

        return transactionDataBinding.root
    }
}