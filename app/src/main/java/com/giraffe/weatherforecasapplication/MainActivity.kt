package com.giraffe.weatherforecasapplication


import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.giraffe.weatherforecasapplication.database.ConcreteLocalSource
import com.giraffe.weatherforecasapplication.databinding.ActivityMainBinding
import com.giraffe.weatherforecasapplication.features.settings.viewmodel.SettingsVM
import com.giraffe.weatherforecasapplication.model.repo.Repo
import com.giraffe.weatherforecasapplication.network.ApiClient
import com.giraffe.weatherforecasapplication.utils.Constants
import com.giraffe.weatherforecasapplication.utils.ViewModelFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.Locale


class MainActivity : AppCompatActivity(), OnDrawerClick {
    companion object {
        private const val TAG = "MainActivityTag"
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var sharedVM: SharedVM
    private lateinit var factory: ViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        factory = ViewModelFactory(Repo.getInstance(ApiClient, ConcreteLocalSource(this)))
        sharedVM = ViewModelProvider(this, factory)[SharedVM::class.java]
        sharedVM.getLanguage()
        lifecycleScope.launch {
            sharedVM.language.collect{
                when(it){
                    Constants.Languages.ENGLISH -> {
                        setLocale(this@MainActivity,"en")
                    }

                    Constants.Languages.ARABIC -> {
                        setLocale(this@MainActivity,"ar")
                    }
                }
            }
        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupWithNavController(binding.navigationView, navController)
        /*if (intent.hasExtra(Constants.CLICKED_ALERT_ID)){
            val lat = intent.getDoubleExtra(Constants.EXTRA_LAT,0.0)
            val lon = intent.getDoubleExtra(Constants.EXTRA_LON,0.0)
            sharedVM.selectLocation(lat,lon)
            Log.i(TAG, "onCreate: ${intent.getIntExtra(Constants.CLICKED_ALERT_ID,-1)}")
        }*/




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


    private fun setLocale(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        @Suppress("DEPRECATION")
        context.resources.updateConfiguration(
            config,
            context.resources.displayMetrics
        )
    }

    private fun refresh(){
        finish()
        startActivity(Intent(this,MainActivity::class.java))
    }
}