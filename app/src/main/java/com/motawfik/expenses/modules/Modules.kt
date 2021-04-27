package com.motawfik.expenses.modules

import com.motawfik.expenses.repos.TokenRepository
import com.motawfik.expenses.viewmodel.CategoriesViewModel
import com.motawfik.expenses.viewmodel.TransactionsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module


val appModules = module {
    single {
        TokenRepository(get())
    }
    viewModel {
        CategoriesViewModel(get())
    }
    viewModel {
        TransactionsViewModel(get())
    }
}
