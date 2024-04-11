package com.example.platepal

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.platepal.databinding.ActivityMainBinding
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController


class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var navController : NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.bottom_nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.discover -> {
                item.onNavDestinationSelected(navController)
                true
            }
            R.id.cookbook -> {
                item.onNavDestinationSelected(navController)
                true
            }
            R.id.create -> {
                item.onNavDestinationSelected(navController)
                true
            }
            R.id.community -> {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        //binding.toolbar.setTitle("") //didn't work
        supportActionBar?.setDisplayShowTitleEnabled(false) //disable title in bar, didn't work


        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.main_frame) as NavHostFragment
        navController = navHostFragment.navController

        //bottom navigation
        val navView = binding.navView
        navView.setupWithNavController(navController)

        //toolbar
        initToolBarMenu()
        appBarConfiguration= AppBarConfiguration(
            setOf(R.id.discover, R.id.cookbook, R.id.create, R.id.community)
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