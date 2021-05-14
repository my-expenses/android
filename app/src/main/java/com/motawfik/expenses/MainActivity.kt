package com.motawfik.expenses

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.motawfik.expenses.databinding.ActivityMainBinding
import com.motawfik.expenses.repos.TokenRepository
import org.koin.java.KoinJavaComponent

class MainActivity : AppCompatActivity() {
    private var appBarConfiguration: AppBarConfiguration? = null
    private var navController: NavController? = null
    private val tokenRepository by KoinJavaComponent.inject(TokenRepository::class.java)

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Expenses)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

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

        navController!!.addOnDestinationChangedListener { controller, destination, arguments ->
            if (destination.id == R.id.usersLoginFragment || destination.id == R.id.registerFragment) {
                tokenRepository.setAccessTokenValue("")
                tokenRepository.setRefreshTokenValue("")
                binding.topAppBar.navigationIcon = null
                binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            } else {
                binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            }
        }

        setContentView(binding.root)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navigation_fragment_container) as NavHostFragment
        val navController = navHostFragment.navController
        return navController.navigateUp(appBarConfiguration!!) || super.onSupportNavigateUp()
    }
}