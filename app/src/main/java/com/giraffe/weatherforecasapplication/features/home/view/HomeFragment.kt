package com.giraffe.weatherforecasapplication.features.home.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.giraffe.weatherforecasapplication.OnDrawerClick
import com.giraffe.weatherforecasapplication.R
import com.giraffe.weatherforecasapplication.SharedVM
import com.giraffe.weatherforecasapplication.database.ConcreteLocalSource
import com.giraffe.weatherforecasapplication.database.SharedHelper
import com.giraffe.weatherforecasapplication.databinding.FragmentHomeBinding
import com.giraffe.weatherforecasapplication.features.home.view.adapters.DailyAdapter
import com.giraffe.weatherforecasapplication.features.home.view.adapters.HourlyAdapter
import com.giraffe.weatherforecasapplication.features.home.viewmodel.HomeVM
import com.giraffe.weatherforecasapplication.model.ForecastModel
import com.giraffe.weatherforecasapplication.model.repo.Repo
import com.giraffe.weatherforecasapplication.network.ApiClient
import com.giraffe.weatherforecasapplication.utils.Constants
import com.giraffe.weatherforecasapplication.utils.UiState
import com.giraffe.weatherforecasapplication.utils.ViewModelFactory
import com.giraffe.weatherforecasapplication.utils.getAddress
import com.giraffe.weatherforecasapplication.utils.toFahrenheit
import com.giraffe.weatherforecasapplication.utils.toKelvin
import com.giraffe.weatherforecasapplication.utils.toMilesPerHours
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone

class HomeFragment : Fragment() {
    companion object {
        const val REQUEST_CODE = 7007
        const val TAG = "HomeFragment"
    }

    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: HomeVM
    private lateinit var factory: ViewModelFactory
    private lateinit var hourlyAdapter: HourlyAdapter
    private lateinit var dailyAdapter: DailyAdapter
    private lateinit var onDrawerClick: OnDrawerClick
    private lateinit var sharedVM:SharedVM
    private lateinit var tempUnit:String
    private lateinit var windSpeedUnit:String
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var location: Location? = null
    private var currentLat:Double = 0.0
    private var currentLon:Double = 0.0


    private var forecastModel: ForecastModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        factory = ViewModelFactory(Repo.getInstance(ApiClient, ConcreteLocalSource(requireContext())))
        viewModel = ViewModelProvider(this, factory)[HomeVM::class.java]
        sharedVM = ViewModelProvider(requireActivity(), factory)[SharedVM::class.java]
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        if(isConnected()) {
            getLocation()
        }else{
            viewModel.getCurrentForecast()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        sharedVM.getTempUnit()
        sharedVM.getWindSpeedUnit()
        lifecycleScope.launch {
            sharedVM.tempUnit.collect{
                tempUnit = it
            }
        }
        lifecycleScope.launch {
            sharedVM.windSpeedUnit.collect{
                windSpeedUnit = it
            }
        }
        handleInit()
        handleClicks()
        lifecycleScope.launch {
            viewModel.forecast.collect {
                when (it) {
                    is UiState.Fail -> {}
                    UiState.Loading -> {}
                    is UiState.Success -> {
                        if(!isConnected()) {
                            Snackbar.make(requireView(), "please check your network connection", Snackbar.LENGTH_INDEFINITE).show()
                        }
                        forecastModel = it.data
                        val response = it.data
                        val current = response?.current
                        Log.i(TAG, "onViewCreated: $currentLat , $currentLon")
                        binding.tvZone.text = it.data?.timezone
                        binding.tvCurrentTemp.text = convertTempToString(current?.temp?:0.0,tempUnit)
                        Glide.with(requireContext())
                            .load("https://openweathermap.org/img/wn/${current?.weather?.get(0)?.icon ?: "02d"}.png")
                            .into(binding.ivCurrent)
                        binding.tvCurrentDes.text = current?.weather?.get(0)?.description ?: "unknown description"
                        binding.tvCurrentTimeDate.text = getCurrentUTCTime(response?.timezone_offset ?: 0.0)
                        binding.tvWind.text = convertWindSpeedToString(current?.wind_speed?:0.0,windSpeedUnit)
                        binding.tvHumidity.text = current?.humidity.toString().plus(" %")
                        binding.tvPressure.text = current?.pressure.toString().plus(" hPa")
                        binding.tvUv.text = "UV index ".plus(current?.uvi.toString())
                        binding.tvCloudiness.text = current?.clouds.toString().plus(" %")
                        binding.tvDir.text = current?.wind_deg.toString().plus("°")
                        dailyAdapter.updateList(response?.daily ?: listOf())
                        hourlyAdapter.updateList(response?.hourly?.take(24) ?: listOf())
                    }
                }
            }
        }

        /*lifecycleScope.launch {
            sharedVM.selectedForecast.collect{
                when(it){
                    is UiState.Fail -> {}
                    UiState.Loading -> {}
                    is UiState.Success -> {}
                }
            }
        }*/

    }

    private fun convertTempToString(temp:Double,tempUnit:String) :String{
        return when(tempUnit){
            Constants.TempUnits.CELSIUS-> temp.toInt().toString().plus(getString(R.string.c))
            Constants.TempUnits.FAHRENHEIT-> temp.toFahrenheit().toInt().toString().plus(getString(R.string.f))
            Constants.TempUnits.KELVIN-> temp.toKelvin().toInt().toString().plus(getString(R.string.k))
            else -> "0.0"
        }
    }

    private fun convertWindSpeedToString(speed:Double,windSpeedUnit:String) :String{
        return when(windSpeedUnit){
            Constants.WindSpeedUnits.METRE-> speed.toInt().toString().plus(getString(R.string.metre_sec))
            Constants.WindSpeedUnits.MILES-> speed.toMilesPerHours().toInt().toString().plus(getString(R.string.miles_hour))
            else -> "0.0"
        }
    }


    private fun handleClicks(){
        onDrawerClick = activity as OnDrawerClick
        binding.ivMore.setOnClickListener {
            onDrawerClick.onClick()
        }
        binding.ivLocation.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToMapFragment()
            findNavController().navigate(action)
        }
        binding.ivFavorite.setOnClickListener {
            forecastModel?.let {
                viewModel.insertForecast(it)
            }
        }
    }

    private fun handleInit(){
        hourlyAdapter = HourlyAdapter(mutableListOf(),tempUnit)
        dailyAdapter = DailyAdapter(mutableListOf(),tempUnit)
        binding.rvHourly.adapter = hourlyAdapter
        binding.rvDaily.adapter = dailyAdapter
    }


    private fun getCurrentUTCTime(shift: Double): String {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendar.add(Calendar.HOUR_OF_DAY, (shift / 3600).toInt())
        val dateFormat = SimpleDateFormat("HH:mm EEEE, MMM d, yyyy")
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        return dateFormat.format(calendar.time)
    }


    //====================location work area======================



    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            location = locationResult.lastLocation
            currentLat = location?.latitude ?: 0.0
            currentLon = location?.longitude ?: 0.0
            viewModel.getForecast(currentLat, currentLon,true)
            fusedLocationProviderClient.removeLocationUpdates(this)
        }
    }

    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                requestNewLocationData()
            } else {
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
        } else {
            requestPermissions()
        }
    }

    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
                ||
                ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED || grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                getLocation()
            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val locationRequest = LocationRequest().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 0
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
    }

    /*fun getAddress(context: Context,latitude: Double, longitude: Double): String {
        val geoCoder = Geocoder(context)
        val address = geoCoder.getFromLocation(latitude, longitude, 1)
        return if (!address.isNullOrEmpty()) {
            "${address[0].adminArea}, ${address[0].countryName}"
        } else {
            "unknown area"
        }
    }*/

    //========================check internet==================
    fun isConnected(): Boolean {
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork) != null
    }

}