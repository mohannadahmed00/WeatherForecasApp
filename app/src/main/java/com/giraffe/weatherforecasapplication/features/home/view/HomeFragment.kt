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
import com.giraffe.weatherforecasapplication.utils.getIconRes
import com.giraffe.weatherforecasapplication.utils.toFahrenheit
import com.giraffe.weatherforecasapplication.utils.toKelvin
import com.giraffe.weatherforecasapplication.utils.toMilesPerHours
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Job
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
    private lateinit var selectedForecast: ForecastModel
    private lateinit var tempUnit: String
    private lateinit var windSpeedUnit: String
    private lateinit var favorites: List<ForecastModel>
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var location: Location? = null
    private var selectedLat: Double = 0.0
    private var selectedLon: Double = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v(TAG, "onCreate: ")
        handleInit()
        sharedVM.getFavorites()
        sharedVM.getTempUnit()
        sharedVM.getWindSpeedUnit()
        observeFavorites()
    }

    private fun handleInit() {
        factory =
            ViewModelFactory(Repo.getInstance(ApiClient, ConcreteLocalSource(requireContext())))
        viewModel = ViewModelProvider(this, factory)[HomeVM::class.java]
        sharedVM = ViewModelProvider(requireActivity(), factory)[SharedVM::class.java]
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.v(TAG, "onCreateView: ")
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.v(TAG, "onViewCreated: ")
        handleAdapters()
        observeSelectedForecast()
        handleClicks()

    }

    private fun handleAdapters() {
        Log.w(TAG, "handleAdapters: ")
        hourlyAdapter = HourlyAdapter(mutableListOf(), sharedVM.tempUnit.value)
        dailyAdapter = DailyAdapter(mutableListOf(), sharedVM.tempUnit.value)
        binding.rvHourly.adapter = hourlyAdapter
        binding.rvDaily.adapter = dailyAdapter
    }

    private var selectedForecastJob: Job? = null
    private fun observeSelectedForecast() {
        selectedForecastJob = lifecycleScope.launch {
            sharedVM.selectedForecast.collect {
                when (it) {
                    is UiState.Fail -> {
                        Log.e(TAG, "observeSelectedForecast: ${it.error}")
                        if (isConnected()) {
                            getLocation()
                        }
                    }

                    is UiState.Loading -> {
                        Log.i(TAG, "observeSelectedForecast: loading")
                        showLoading()
                    }

                    is UiState.Success -> {
                        Log.d(TAG, "observeSelectedForecast: ${it.data}")
                        Log.d(TAG, "observeSelectedForecast: isFavorite: ${it.data?.isFavorite}")
                        hideLoading()
                        it.data?.let { selectedForecast ->
                            selectedLat = selectedForecast.lat
                            selectedLon = selectedForecast.lon
                            handleForecastUI(selectedForecast)
                        }
                    }
                }

            }
        }
    }

    private fun handleForecastUI(forecast: ForecastModel) {
        Log.w(TAG, "handleForecastUI: $forecast")
        handleCurrentTemp(forecast)
        handleTimeZone(forecast)
        handleCurrentIcon(forecast)
        handleCurrentDescription(forecast)
        handleCurrentTimeDate(forecast)
        handleCurrentWind(forecast)
        handleCurrentHumidity(forecast)
        handleCurrentPressure(forecast)
        handleCurrentUV(forecast)
        handleCurrentCloudiness(forecast)
        handleCurrentDirection(forecast)
        dailyAdapter.updateList(forecast.daily)
        hourlyAdapter.updateList(forecast.hourly.take(24))
        handleFavoriteIcon(forecast)
    }


    private fun handleClicks() {
        Log.w(TAG, "handleClicks: ")
        onDrawerClick = activity as OnDrawerClick
        binding.ivMore.setOnClickListener {
            onDrawerClick.onClick()
        }
        binding.ivLocation.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToMapFragment()
            action.lat = selectedLat.toFloat()
            action.lon = selectedLon.toFloat()
            findNavController().navigate(action)
        }
        binding.ivCurrentLocation.setOnClickListener {
            if (isConnected()) {
                getLocation()
            } else {
                sharedVM.getSelectedForecast()
            }
        }
    }


    private fun observeFavorites() {
        lifecycleScope.launch {
            sharedVM.favorites.collect {
                Log.d(TAG, "observeFavorites: ${it.size}")
                sharedVM.getSelectedForecast()
            }
        }
    }


    private fun handleTimeZone(forecast: ForecastModel) {
        binding.tvZone.text = forecast.timezone

    }

    private fun handleCurrentTemp(forecast: ForecastModel) {
        binding.tvCurrentTemp.text =
            convertTempToString(forecast.current?.temp ?: 0.0, sharedVM.tempUnit.value)
    }

    private fun handleCurrentIcon(forecast: ForecastModel) {
        /*Glide.with(requireContext())
            .load("https://openweathermap.org/img/wn/${forecast.current?.weather?.get(0)?.icon}.png")
            .into(binding.ivCurrent)*/

        binding.ivCurrent.setImageResource(getIconRes(forecast.current?.weather?.get(0)?.icon?:""))
    }


    private fun handleCurrentDescription(forecast: ForecastModel) {
        binding.tvCurrentDes.text =
            forecast.current?.weather?.get(0)?.description ?: "no description"
    }

    private fun handleCurrentTimeDate(forecast: ForecastModel) {
        binding.tvCurrentTimeDate.text = getCurrentUTCTime(forecast.timezone_offset)
    }

    private fun handleCurrentWind(forecast: ForecastModel) {
        binding.tvWind.text =
            convertWindSpeedToString(
                forecast.current?.wind_speed ?: 0.0,
                sharedVM.windSpeedUnit.value
            )
    }

    private fun handleCurrentHumidity(forecast: ForecastModel) {
        binding.tvHumidity.text = (forecast.current?.humidity ?: 0.0).toString().plus(" %")
    }

    private fun handleCurrentPressure(forecast: ForecastModel) {
        binding.tvPressure.text = (forecast.current?.pressure ?: 0.0).toString().plus(" hPa")
    }

    private fun handleCurrentUV(forecast: ForecastModel) {
        binding.tvUv.text = "UV index ".plus((forecast.current?.uvi ?: 0.0).toString())
    }

    private fun handleCurrentCloudiness(forecast: ForecastModel) {
        binding.tvCloudiness.text = (forecast.current?.clouds ?: 0.0).toString().plus(" %")
    }

    private fun handleCurrentDirection(forecast: ForecastModel) {
        binding.tvDir.text = (forecast.current?.wind_deg ?: 0.0).toString().plus("°")
    }

    private fun handleFavoriteIcon(forecast: ForecastModel) {
        if (forecast.isFavorite) {
            binding.ivFavorite.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.dark_red
                )
            )
            binding.ivFavorite.setOnClickListener {
                sharedVM.deleteFavorite(forecast)
            }
        } else {
            binding.ivFavorite.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.gray
                )
            )
            binding.ivFavorite.setOnClickListener {
                sharedVM.insertFavorite(forecast)
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
                .plus(" ${getString(R.string.metre_sec)}")

            Constants.WindSpeedUnits.MILES -> speed.toMilesPerHours().toInt().toString()
                .plus(" ${getString(R.string.miles_hour)}")

            else -> "0.0"
        }
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
            selectedLat = location?.latitude ?: 0.0
            selectedLon = location?.longitude ?: 0.0
            Log.i(TAG, "onLocationResult: $selectedLat , $selectedLon ")
            sharedVM.getForecast(selectedLat, selectedLon)
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
        //binding.homeShimmer.visibility = View.VISIBLE
        showLoading()
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
        binding.mainUi.visibility = View.INVISIBLE
        binding.homeShimmer.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        binding.homeShimmer.stopShimmer()
        binding.mainUi.visibility = View.VISIBLE
        binding.homeShimmer.visibility = View.INVISIBLE
    }

    override fun onStart() {
        super.onStart()
        Log.v(TAG, "onStart: ")
    }

    override fun onResume() {
        super.onResume()
        Log.v(TAG, "onResume: ")
    }

    override fun onPause() {
        super.onPause()
        Log.v(TAG, "onPause: ")
    }

    override fun onStop() {
        super.onStop()
        Log.v(TAG, "onStop: ")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.v(TAG, "onDestroyView: ")
        selectedForecastJob?.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.v(TAG, "onDestroy: ")
    }

    override fun onDetach() {
        super.onDetach()
        Log.v(TAG, "onDetach: ")
    }

}