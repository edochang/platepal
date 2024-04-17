package com.example.platepal

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.platepal.databinding.ActivityMainBinding
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.example.platepal.ui.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    companion object {
        // Constants
        const val SPOONACULAR_API_NAME = "SpoonacularApi"

        // Variables
        var globalDebug = false


        fun showSnackbarMessage(view: View, message: String) {
            Snackbar
                .make(
                    view,
                    message,
                    Snackbar.LENGTH_SHORT)
                .show()
        }
    }
    private lateinit var binding : ActivityMainBinding
    private lateinit var navController : NavController
    private lateinit var appBarConfiguration: AppBarConfiguration


    private val viewModel: MainViewModel by viewModels()

    fun progressBarOn() {
        binding.indeterminateBar.visibility = View.VISIBLE
    }

    fun progressBarOff() {
        binding.indeterminateBar.visibility = View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //menuInflater.inflate(R.menu.bottom_nav_menu, menu)
        //super.onCreateOptionsMenu(menu)
        return false  // gets rid of the 3-dot options menu on action bar
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.discoverFragment -> {
                item.onNavDestinationSelected(navController)
                true
            }
            R.id.cookbookFragment -> {
                item.onNavDestinationSelected(navController)
                true
            }
            R.id.createFragment -> {
                item.onNavDestinationSelected(navController)
                true
            }
            R.id.communityFragment -> {
                item.onNavDestinationSelected(navController)
                true
            }
            else -> {false}
        }
    }

    private fun initToolBarMenu() {
        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_main, menu)
            }
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // This could be replaced with return false, but I wanted to show
                // the usual structure for a menu item
                return when (menuItem.itemId) {
                    R.id.profileFragment -> {
                        menuItem.onNavDestinationSelected(navController)
                        true
                    }
                    R.id.inboxFragment -> {
                        menuItem.onNavDestinationSelected(navController)
                        true
                    }
                    else -> false
                }
            }
        })
    }

    private fun initTitleObservers() {
        // Observe title changes
        viewModel.observeTitle().observe(this){
            binding.barTitle.text = it
            Log.d(javaClass.simpleName, it)
        }
    }

    fun initRecipeList() {
        Log.d(TAG, "Retrieving recipes from Repo...")
        progressBarOn()
        viewModel.fetchReposRecipeList {
            Log.d(TAG, "Recipes retrieval listener invoked.")
            progressBarOff()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        //binding.toolbar.setTitle("") //didn't work
        supportActionBar?.setDisplayShowTitleEnabled(false) //disable title in bar, didn't work
        supportActionBar?.setDisplayShowCustomEnabled(true)

        // Retrieve Recipes
        initRecipeList()

        //observe top bar title
        initTitleObservers()

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.main_frame) as NavHostFragment
        navController = navHostFragment.navController

        //bottom navigation
        val navView = binding.navView
        navView.setupWithNavController(navController)

        //toolbar
        initToolBarMenu()
        appBarConfiguration= AppBarConfiguration(
            setOf(R.id.discoverFragment, R.id.cookbookFragment, R.id.createFragment, R.id.communityFragment)
        )
        //appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        //setupActionBarWithNavController(navController, appBarConfiguration)

    }


    // navigateUp:
    // If we came here from within the app, pop the back stack
    // If we came here from another app, return to it.
    //no need for this if using Toolbar; only for action bar
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

}