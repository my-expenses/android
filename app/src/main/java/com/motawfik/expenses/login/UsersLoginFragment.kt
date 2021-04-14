package com.motawfik.expenses.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.motawfik.expenses.databinding.UsersLoginFragmentBinding


class UsersLoginFragment : Fragment() {
    private var loginViewModel: UsersLoginViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val loginBinding = UsersLoginFragmentBinding.inflate(inflater)

        loginBinding.viewModel = loginViewModel

        return loginBinding.root
    }
}