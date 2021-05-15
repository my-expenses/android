package com.motawfik.expenses.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.motawfik.expenses.databinding.RegisterFragmentBinding
import com.motawfik.expenses.utils.showErrorSnackbar
import com.motawfik.expenses.utils.showSuccessSnackbar

import com.motawfik.expenses.viewmodel.RegisterViewModel
import com.motawfik.expenses.viewmodel.RegistrationStatus

class RegisterFragment : Fragment() {
    private val viewModel = RegisterViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val registerBinding = RegisterFragmentBinding.inflate(inflater)
        registerBinding.lifecycleOwner = this
        registerBinding.viewModel = viewModel

        viewModel.backToLogin.observe(viewLifecycleOwner, {
            it?.let {
                if (it) {
                    findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToUsersLoginFragment())
                    viewModel.resetNavigateToLogin()
                }
            }
        })

        viewModel.errorMessage.observe(viewLifecycleOwner, {
            it?.let {
                showErrorSnackbar(registerBinding.root, it)
                viewModel.resetRegisterStatus()
            }
        })

        viewModel.status.observe(viewLifecycleOwner, {
            it?.let {
                if(it == RegistrationStatus.DONE) {
                    showSuccessSnackbar(registerBinding.root, "User created successfully")
                    findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToUsersLoginFragment())
                    viewModel.resetRegisterStatus()
                }
            }
        })


        return registerBinding.root
    }

}