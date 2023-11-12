package com.giraffe.weatherforecasapplication


import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.giraffe.weatherforecasapplication.databinding.ActivityMainBinding
import com.giraffe.weatherforecasapplication.utils.Constants


class MainActivity : AppCompatActivity(), OnDrawerClick {
    companion object {
        private const val TAG = "MainActivityTag"
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupWithNavController(binding.navigationView, navController)


        if (intent.hasExtra(Constants.CLICKED_ALERT_ID)){
            navController.navigate(R.id.alertsFragment)
            Log.i(TAG, "onCreate: ${intent.getIntExtra(Constants.CLICKED_ALERT_ID,-1)}")
        }


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                binding.drawerLayout.openDrawer(GravityCompat.START)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick() {
        binding.drawerLayout.openDrawer(GravityCompat.START)
    }
}