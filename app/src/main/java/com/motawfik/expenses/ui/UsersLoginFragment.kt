package com.motawfik.expenses.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.motawfik.expenses.databinding.UsersLoginFragmentBinding
import com.motawfik.expenses.repos.TokenRepository
import com.motawfik.expenses.utils.showErrorSnackbar
import com.motawfik.expenses.viewmodel.LOGIN_STATUS
import com.motawfik.expenses.viewmodel.UsersLoginViewModel
import org.koin.java.KoinJavaComponent


class UsersLoginFragment : Fragment() {
    private val loginViewModel = UsersLoginViewModel()
    private val tokenRepository by KoinJavaComponent.inject(TokenRepository::class.java)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val loginBinding = UsersLoginFragmentBinding.inflate(inflater)
        loginBinding.viewModel = loginViewModel

        // observing login status for changes
        loginViewModel.loginStatus.observe(viewLifecycleOwner, {
            when (it) {
                LOGIN_STATUS.SUCCESS -> {
                    findNavController().navigate(UsersLoginFragmentDirections.actionUsersLoginFragmentToTransactionsFragment())
                    loginViewModel.resetLoginStatus()
                }
                LOGIN_STATUS.INVALID_CREDENTIALS -> { // if invalid credentials
                    showErrorSnackbar(loginBinding.root, "Invalid Credentials")
                    loginViewModel.resetLoginStatus()
                }
                LOGIN_STATUS.INTERNAL_ERROR -> { // if internal server error
                    showErrorSnackbar(loginBinding.root, "Internal Server Error")
                    loginViewModel.resetLoginStatus()
                }
                LOGIN_STATUS.UNKNOWN_ERROR -> { // if another unknown error
                    showErrorSnackbar(loginBinding.root, "Unknown Error Occurred")
                    loginViewModel.resetLoginStatus()
                }
                else -> {}
            }
        })

        loginViewModel.accessToken.observe(viewLifecycleOwner, {
            if (it.isNotEmpty()) {
                // saving token to sharedPreferences
                tokenRepository.setAccessTokenValue(it)
                loginViewModel.resetAccessToken()
            }
        })
        loginViewModel.refreshToken.observe(viewLifecycleOwner, {
            if (it.isNotEmpty()) {
                // saving token to sharedPreferences
                tokenRepository.setRefreshTokenValue(it)
                loginViewModel.resetRefreshToken()
            }
        })
        loginViewModel.navigateToRegister.observe(viewLifecycleOwner, {
            it?.let {
                if(it) {
                    findNavController().navigate(UsersLoginFragmentDirections.actionUsersLoginFragmentToRegisterFragment())
                    loginViewModel.resetRegisterClicked()
                }
            }
        })

        return loginBinding.root
    }
}