package com.giraffe.weatherforecasapplication.features.home.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.giraffe.weatherforecasapplication.OnDrawerClick
import com.giraffe.weatherforecasapplication.R
import com.giraffe.weatherforecasapplication.SharedVM
import com.giraffe.weatherforecasapplication.database.ConcreteLocalSource
import com.giraffe.weatherforecasapplication.databinding.FragmentHomeBinding
import com.giraffe.weatherforecasapplication.features.home.view.adapters.DailyAdapter
import com.giraffe.weatherforecasapplication.features.home.view.adapters.HourlyAdapter
import com.giraffe.weatherforecasapplication.features.home.viewmodel.HomeVM
import com.giraffe.weatherforecasapplication.model.forecast.ForecastModel
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
import java.util.Locale
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
    private lateinit var sharedVM: SharedVM
    private lateinit var tempUnit: String
    private lateinit var windSpeedUnit: String
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var location: Location? = null
    private var currentLat: Double = 0.0
    private var currentLon: Double = 0.0
    private var favorites: MutableList<ForecastModel> = mutableListOf()
    private var selectedForecast: ForecastModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate: ")
        factory =
            ViewModelFactory(Repo.getInstance(ApiClient, ConcreteLocalSource(requireContext())))
        viewModel = ViewModelProvider(this, factory)[HomeVM::class.java]
        sharedVM = ViewModelProvider(requireActivity(), factory)[SharedVM::class.java]
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.i(TAG, "onCreateView: ")
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.i(TAG, "onViewCreated: ")
        viewModel.getFavorites()
        observeFavorites()
        sharedVM.getTempUnit()
        observeTempUnit()
        sharedVM.getWindSpeedUnit()
        observeWindSpeedUnit()
        handleInit()
        observeSelectedForecast()
        observeForecast()
        handleClicks()
    }

    private fun observeFavorites() {
        lifecycleScope.launch {
            viewModel.favorites.collect {
                when (it) {
                    is UiState.Fail -> {
                        favorites.clear()
                        selectedForecast?.apply {
                            handleFavoriteIcon(this, favorites)
                        }
                    }

                    UiState.Loading -> {}
                    is UiState.Success -> {
                        favorites = it.data.toMutableList()
                        selectedForecast?.apply {
                            handleFavoriteIcon(this, favorites)
                        }
                    }
                }
            }
        }
    }

    private fun observeForecast() {
        lifecycleScope.launch {
            viewModel.forecast.collect {
                when (it) {
                    is UiState.Fail -> {
                        Log.e(TAG, "Fail forecast: ${it.error}")
                    }

                    UiState.Loading -> {
                        Log.d(TAG, "Loading forecast:")
                    }

                    is UiState.Success -> {
                        Log.d(TAG, "${it.data}")
                        val f = it.data
                        f?.apply {
                            isCurrent = true
                            viewModel.insertForecast(this)
                        }

                        sharedVM.selectForecast(it.data)
                    }
                }
            }
        }
    }

    private fun observeSelectedForecast() {
        showLoading()
        lifecycleScope.launch {
            sharedVM.selectedForecast.collect {
                if (it == null) {
                    if (isConnected()) {
                        getLocation()
                    } else {
                        viewModel.getCurrentForecast()
                    }
                    //observeForecast()
                } else {
                    handleForecastUI(it)
                }
            }
        }
    }

    private fun handleForecastUI(forecast: ForecastModel) {
        hideLoading()
        selectedForecast = forecast
        val current = forecast.current

        if(forecast.timezone.contains('/')){
            binding.tvZone.text = getAddress(requireContext(),forecast.lat, forecast.lon,forecast.timezone)
        }else{
            binding.tvZone.text = forecast.timezone

        }

        binding.tvCurrentTemp.text = convertTempToString(current?.temp ?: 0.0, tempUnit)
        Glide.with(requireContext())
            .load("https://openweathermap.org/img/wn/${current?.weather?.get(0)?.icon}.png")
            .into(binding.ivCurrent)
        binding.tvCurrentDes.text =
            current?.weather?.get(0)?.description ?: "no description"
        binding.tvCurrentTimeDate.text = getCurrentUTCTime(forecast.timezone_offset)
        binding.tvWind.text =
            convertWindSpeedToString(current?.wind_speed ?: 0.0, windSpeedUnit)
        binding.tvHumidity.text = (current?.humidity ?: 0.0).toString().plus(" %")
        binding.tvPressure.text = (current?.pressure ?: 0.0).toString().plus(" hPa")
        binding.tvUv.text = "UV index ".plus((current?.uvi ?: 0.0).toString())
        binding.tvCloudiness.text = (current?.clouds ?: 0.0).toString().plus(" %")
        binding.tvDir.text = (current?.wind_deg ?: 0.0).toString().plus("°")
        dailyAdapter.updateList(forecast.daily)
        hourlyAdapter.updateList(forecast.hourly.take(24))
        handleFavoriteIcon(forecast, favorites)
    }


    private fun handleFavoriteIcon(forecast: ForecastModel, favorites: List<ForecastModel>) {
        Log.i(TAG, "handleFavoriteIcon: ${forecast.timezone} => favorites size:${favorites.size}")
        favorites.firstOrNull { (it.lat + it.lon) == (forecast.lat + forecast.lon) }.let {
            if (it == null) {
                binding.ivFavorite.setColorFilter(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.gray
                    )
                )
                binding.ivFavorite.setOnClickListener {
                    viewModel.insertForecast(forecast)
                    observeInsertion()
                }
            } else {
                binding.ivFavorite.setColorFilter(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.dark_red
                    )
                )
                binding.ivFavorite.setOnClickListener {
                    viewModel.deleteForecast(forecast)
                    observeDeletion()
                }
            }
        }
    }

    private fun observeInsertion() {
        lifecycleScope.launch {
            viewModel.insert.collect {
                when (it) {
                    is UiState.Fail -> {}
                    UiState.Loading -> {}
                    is UiState.Success -> {
                        viewModel.getFavorites()
                    }
                }
            }
        }
    }

    private fun observeDeletion() {
        lifecycleScope.launch {
            viewModel.delete.collect {
                when (it) {
                    is UiState.Fail -> {}
                    UiState.Loading -> {}
                    is UiState.Success -> {
                        viewModel.getFavorites()
                    }
                }
            }
        }
    }

    private fun observeWindSpeedUnit() {
        lifecycleScope.launch {
            sharedVM.windSpeedUnit.collect {
                windSpeedUnit = it
            }
        }
    }

    private fun observeTempUnit() {
        lifecycleScope.launch {
            sharedVM.tempUnit.collect {
                tempUnit = it
            }
        }
    }

    private fun convertTempToString(temp: Double, tempUnit: String): String {
        return when (tempUnit) {
            Constants.TempUnits.CELSIUS -> temp.toInt().toString().plus(getString(R.string.c))
            Constants.TempUnits.FAHRENHEIT -> temp.toFahrenheit().toInt().toString()
                .plus(getString(R.string.f))

            Constants.TempUnits.KELVIN -> temp.toKelvin().toInt().toString()
                .plus(getString(R.string.k))

            else -> "0.0"
        }
    }

    private fun convertWindSpeedToString(speed: Double, windSpeedUnit: String): String {
        return when (windSpeedUnit) {
            Constants.WindSpeedUnits.METRE -> speed.toInt().toString()
                .plus(getString(R.string.metre_sec))

            Constants.WindSpeedUnits.MILES -> speed.toMilesPerHours().toInt().toString()
                .plus(getString(R.string.miles_hour))

            else -> "0.0"
        }
    }

    private fun handleClicks() {
        onDrawerClick = activity as OnDrawerClick
        binding.ivMore.setOnClickListener {
            onDrawerClick.onClick()
        }
        binding.ivLocation.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToMapFragment()
            findNavController().navigate(action)
        }
        binding.ivCurrentLocation.setOnClickListener {
            if (isConnected()) {
                getLocation()
            } else {
                viewModel.getCurrentForecast()
            }
            //observeForecast()
        }
    }

    private fun handleInit() {
        hourlyAdapter = HourlyAdapter(mutableListOf(), tempUnit)
        dailyAdapter = DailyAdapter(mutableListOf(), tempUnit)
        binding.rvHourly.adapter = hourlyAdapter
        binding.rvDaily.adapter = dailyAdapter
    }


    private fun getCurrentUTCTime(shift: Double): String {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendar.add(Calendar.HOUR_OF_DAY, (shift / 3600).toInt())
        val dateFormat = SimpleDateFormat("HH:mm EEEE, MMM d, yyyy", Locale.US)
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        return dateFormat.format(calendar.time)
    }


    //====================location work area======================


    private val locationCallback: LocationCallback = object : LocationCallback() {

        override fun onLocationResult(locationResult: LocationResult) {
            location = locationResult.lastLocation
            currentLat = location?.latitude ?: 0.0
            currentLon = location?.longitude ?: 0.0
            Log.i(TAG, "onLocationResult: $currentLat , $currentLon ")

            viewModel.getForecast(currentLat, currentLon, true)


            fusedLocationProviderClient.removeLocationUpdates(this)
        }
    }

    private fun getLocation() {
        Log.i(TAG, "getLocation: ")
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
        val locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        Log.i(TAG, "requestNewLocationData: ")
        val locationRequest = LocationRequest().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 0
        }
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )
    }

    //========================check internet==================
    private fun isConnected(): Boolean {
        val connectivityManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork) != null
    }

    private fun showLoading() {
        binding.homeShimmer.startShimmer()
        binding.homeShimmer.visibility = View.VISIBLE
        binding.cardView1.visibility = View.INVISIBLE
        binding.rvDaily.visibility = View.INVISIBLE
        binding.rvHourly.visibility = View.INVISIBLE
        binding.ivCurrentLocation.visibility = View.INVISIBLE
        binding.ivFavorite.visibility = View.INVISIBLE
        binding.ivLocation.visibility = View.INVISIBLE
    }

    private fun hideLoading() {
        binding.homeShimmer.hideShimmer()
        binding.homeShimmer.visibility = View.INVISIBLE
        binding.cardView1.visibility = View.VISIBLE
        binding.rvDaily.visibility = View.VISIBLE
        binding.rvHourly.visibility = View.VISIBLE
        binding.ivCurrentLocation.visibility = View.VISIBLE
        binding.ivFavorite.visibility = View.VISIBLE
        binding.ivLocation.visibility = View.VISIBLE
    }

}