package com.motawfik.expenses

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.motawfik.expenses.databinding.ActivityMainBinding
import com.motawfik.expenses.modules.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainActivity : AppCompatActivity() {
    private var appBarConfiguration: AppBarConfiguration? = null
    private var navController: NavController? = null

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setSupportActionBar(binding.topAppBar) //set the toolbar

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navigation_fragment_container) as NavHostFragment
        navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.transactionsFragment,
                R.id.usersLoginFragment,
                R.id.categoriesFragment,
            ), binding.drawerLayout
        )
        setupActionBarWithNavController(navController!!, appBarConfiguration!!)
        binding.navigationView.setupWithNavController(navController!!)

        startKoin {
            androidContext(this@MainActivity)
            modules(appModules)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navigation_fragment_container) as NavHostFragment
        val navController = navHostFragment.navController
        return navController.navigateUp(appBarConfiguration!!) || super.onSupportNavigateUp()
    }
}