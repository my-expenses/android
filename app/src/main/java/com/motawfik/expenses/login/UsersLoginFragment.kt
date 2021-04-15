package com.motawfik.expenses.login

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.snackbar.Snackbar
import com.motawfik.expenses.databinding.UsersLoginFragmentBinding
import com.motawfik.expenses.repos.PrefRepository


class UsersLoginFragment : Fragment() {
    private val loginViewModel = UsersLoginViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val loginBinding = UsersLoginFragmentBinding.inflate(inflater)
        loginBinding.viewModel = loginViewModel
        val sharedPref = PrefRepository(requireActivity())

        // observing login status for changes
        loginViewModel.loginStatus.observe(viewLifecycleOwner, {
            when (it) {
                LOGIN_STATUS.INVALID_CREDENTIALS -> { // if invalid credentials
                    showSnackbar(loginBinding.root, "Invalid Credentials")
                    loginViewModel.resetLoginStatus()
                }
                LOGIN_STATUS.INTERNAL_ERROR -> { // if internal server error
                    showSnackbar(loginBinding.root, "Internal Server Error")
                    loginViewModel.resetLoginStatus()
                }
                LOGIN_STATUS.UNKNOWN_ERROR -> { // if another unknown error
                    showSnackbar(loginBinding.root, "Unknown Error Occurred")
                    loginViewModel.resetLoginStatus()
                }
                else -> {}
            }
        })

        loginViewModel.token.observe(viewLifecycleOwner, {
            if (it.isNotEmpty()) {
                sharedPref.setTokenValue(it)
                loginViewModel.resetToken()
            }
        })

        return loginBinding.root
    }

    private fun showSnackbar(rootView: View, message: String) {
        Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT)
            .setBackgroundTint(Color.RED)
            .setActionTextColor(Color.BLACK)
            .setAction("Close") {}
            .show()
    }
}