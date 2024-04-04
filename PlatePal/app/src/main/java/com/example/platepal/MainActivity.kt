package com.example.platepal

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import com.example.platepal.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var navController : NavController

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
        val activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
        activityMainBinding.toolbar.setTitle("")
        setSupportActionBar(activityMainBinding.toolbar)

        val navView: BottomNavigationView = activityMainBinding.contentMain.navView
        navView.itemIconTintList = null

    }
}