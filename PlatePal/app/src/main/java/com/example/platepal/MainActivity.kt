package com.example.platepal

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.platepal.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.platepal.ui.DiscoverFragment
import com.example.platepal.ui.CookbookFragment
import com.example.platepal.ui.CreateFragment
import com.example.platepal.ui.CommunityFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var navController : NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.nav_inbox) {
            Toast.makeText(this, "Click Search Icon.", Toast.LENGTH_SHORT).show()
        } else if (item.itemId == R.id.nav_profile) {
            Toast.makeText(this, "Clicked Settings Icon..", Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        //activityMainBinding.toolbar.setTitle("") didn't work
        supportActionBar?.setDisplayShowTitleEnabled(false) //disable title in bar

        //probably not the best way to navigate, but it works for now
        val navView: BottomNavigationView = binding.navView
        navView.setOnItemSelectedListener { menuItem->
            when(menuItem.itemId){
                R.id.navigation_discover -> {
                    replaceFragment(DiscoverFragment())
                    true
                }
                R.id.navigation_create -> {
                    replaceFragment(CreateFragment())
                    true
                }
                R.id.navigation_cookbook -> {
                    replaceFragment(CookbookFragment())
                    true
                }
                R.id.navigation_community -> {
                    replaceFragment(CommunityFragment())
                    true
                }
                else -> {false}
            }
        }
        replaceFragment(DiscoverFragment())
    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.main_frame, fragment)
        fragmentTransaction.commit()
    }

}