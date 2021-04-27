package com.motawfik.expenses

import android.app.Application
import com.motawfik.expenses.modules.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class KoinApp: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@KoinApp)
            modules(appModules)
        }
    }
}