package com.motawfik.expenses.modules

import com.motawfik.expenses.repos.TokenRepository
import org.koin.dsl.module


val appModules = module {
    single {
        TokenRepository(get())
    }
}
