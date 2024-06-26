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
import com.google.firebase.auth.FirebaseAuth
import android.content.Intent
import com.example.platepal.ui.viewmodel.OneRecipeViewModel
import com.example.platepal.ui.viewmodel.UserViewModel
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    companion object {
        // Constants
        const val TAG = "MainActivity"
        const val SPOONACULAR_API_NAME = "SpoonacularApi"
        const val SEARCH_FROM_ADDR_ONEPOST = "ONEPOST"
        const val SEARCH_FROM_ADDR_DISCOVER = "DISCOVER"
        const val ONEPOST_TRIGGER_TEXTVIEW = "TEXTVIEW"
        const val ONEPOST_TRIGGER_CAMERA = "CAMERA"
        const val ONEPOST_TRIGGER_SEARCH = "SEARCH"

        // Variables
        var globalDebug = false

        fun showSnackbarMessage(view: View, message: String) {
            Snackbar
                .make(
                    view,
                    message,
                    Snackbar.LENGTH_SHORT
                )
                .show()
        }
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    private val viewModel: MainViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private val oneRecipeViewModel: OneRecipeViewModel by viewModels()

    fun progressBarOn() {
        binding.indeterminateBar.visibility = View.VISIBLE
        Log.d(TAG, "Progress Bar turned on")
    }

    fun progressBarOff() {
        binding.indeterminateBar.visibility = View.GONE
        Log.d(TAG, "Progress Bar turned off")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //menuInflater.inflate(R.menu.bottom_nav_menu, menu)
        //super.onCreateOptionsMenu(menu)
        return false  // gets rid of the 3-dot options menu on action bar
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.discoverFragment -> {
                item.onNavDestinationSelected(navController)
                true
            }

            R.id.cookbookFragment -> {
                item.onNavDestinationSelected(navController)
                true
            }

            R.id.createRecipeFragment -> {
                item.onNavDestinationSelected(navController)
                true
            }

            R.id.communityFragment -> {
                item.onNavDestinationSelected(navController)
                true
            }

            else -> {
                false
            }
        }
    }

    /* Note use alternative approach and turn this into a mediatorlive data with two sources
        1. API Recipe List
        2. User Recipe List
     */
    // Used for search for a combine list of all recipes.
    private fun initAllRecipeList(){
        progressBarOn()
        viewModel.fetchAllRecipeList {
            Log.d(TAG, "All Recipes retrieval listener invoked.")
            progressBarOff()
        }
    }

    private fun initOtherObservers() {
        oneRecipeViewModel.fetchDone.observe(this) {
            Log.d(TAG, "One Recipe observer fetchDone: ${oneRecipeViewModel.fetchDone.value}")
            if (it) {
                progressBarOff()
            }
        }
    }

    fun initRecipeList() {
        //Log.d(TAG, "Retrieving recipes from Repo...")
        progressBarOn()
        viewModel.fetchReposRecipeList {
            //Log.d(TAG, "Recipes retrieval listener invoked.")
            progressBarOff()
        }
    }

    private fun initTitleObservers() {
        // Observe title changes
        viewModel.observeTitle().observe(this) {
            binding.barTitle.text = it
            Log.d(javaClass.simpleName, it)
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

    fun initUserCreatedRecipeList(){
        progressBarOn()
        viewModel.fetchReposUserCreatedRecipeList {
            //Log.d(TAG, "Recipes retrieval listener invoked.")
            progressBarOff()
        }
    }

    private fun initUserSession() {
        userViewModel.fetchUserMeta(userViewModel.getAuthUUID())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        //binding.toolbar.setTitle("") //didn't work
        supportActionBar?.setDisplayShowTitleEnabled(false) //disable title in bar, didn't work
        supportActionBar?.setDisplayShowCustomEnabled(true)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.main_frame) as NavHostFragment
        navController = navHostFragment.navController

        initTitleObservers() //observe top bar title
        initOtherObservers()
        initUserSession() // Initialize User Data

        // Initialize Recipe Lists
        //initRecipeList() // Retrieve Spoonacular Recipes
        //initUserCreatedRecipeList() //retrieve user created recipes
        initAllRecipeList() //Retrieve spoonacular + user created recipes

        //progressBarOn()
        //fetch initial favorite recipe list for user
        userViewModel.fetchInitialFavRecipes {
            //Log.d(TAG, "favorite recipe list listener invoked")
            viewModel.setInitFavList(true)  // will update recipeList via this flag
        }

        //bottom navigation
        val navView = binding.navView
        navView.setupWithNavController(navController)

        //toolbar
        initToolBarMenu()
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.discoverFragment,
                R.id.cookbookFragment,
                R.id.createRecipeFragment,
                R.id.communityFragment
            )
        )
        //appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        //setupActionBarWithNavController(navController, appBarConfiguration)

        // Control which fragments will not have the bottom navigation
        // Listener parameters are navController, destination, arguments
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val fragmentId = destination.id

            if (fragmentId == R.id.createOnePostFragment) {
                binding.toolbar.setNavigationIcon(null)
                binding.navView.visibility = View.GONE
            } else {
                binding.navView.visibility = View.VISIBLE
            }
        }
    }

    //log out current user, go back to sign in page
    fun logout() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
        finish()
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