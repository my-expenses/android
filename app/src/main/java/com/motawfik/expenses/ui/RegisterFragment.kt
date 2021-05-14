package com.motawfik.expenses.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.motawfik.expenses.databinding.RegisterFragmentBinding

import com.motawfik.expenses.viewmodel.RegisterViewModel

class RegisterFragment : Fragment() {
    private val viewModel = RegisterViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val registerBinding = RegisterFragmentBinding.inflate(inflater)
        registerBinding.viewModel = viewModel

        viewModel.backToLogin.observe(viewLifecycleOwner, {
            it?.let {
                if (it) {
                    findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToUsersLoginFragment())
                    viewModel.resetNavigateToLogin()
                }
            }
        })

        return registerBinding.root
    }

}