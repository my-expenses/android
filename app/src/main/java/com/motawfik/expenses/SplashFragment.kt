package com.motawfik.expenses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.motawfik.expenses.viewmodel.SplashViewModel


class SplashFragment : Fragment() {
    private val splashViewModel = SplashViewModel()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        splashViewModel.userStatus.observe(viewLifecycleOwner, {
            it?.let {
                if (it) {
                    findNavController().navigate(R.id.action_splashFragment_to_transactionsFragment)
                } else {
                    findNavController().navigate(R.id.action_splashFragment_to_usersLoginFragment)
                }

            }
        })
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }
}