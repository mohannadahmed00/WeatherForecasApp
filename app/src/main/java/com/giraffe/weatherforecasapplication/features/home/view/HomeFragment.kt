package com.giraffe.weatherforecasapplication.features.home.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.giraffe.weatherforecasapplication.OnDrawerClick
import com.giraffe.weatherforecasapplication.database.ConcreteLocalSource
import com.giraffe.weatherforecasapplication.database.SharedHelper
import com.giraffe.weatherforecasapplication.databinding.FragmentHomeBinding
import com.giraffe.weatherforecasapplication.features.home.view.adapters.DailyAdapter
import com.giraffe.weatherforecasapplication.features.home.view.adapters.HourlyAdapter
import com.giraffe.weatherforecasapplication.features.home.viewmodel.HomeVM
import com.giraffe.weatherforecasapplication.model.Daily
import com.giraffe.weatherforecasapplication.model.ForecastModel
import com.giraffe.weatherforecasapplication.model.repo.Repo
import com.giraffe.weatherforecasapplication.network.ApiClient
import com.giraffe.weatherforecasapplication.utils.Constants
import com.giraffe.weatherforecasapplication.utils.UiState
import com.giraffe.weatherforecasapplication.utils.ViewModelFactory
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone
import kotlin.math.log

class HomeFragment : Fragment() {
    companion object {
        const val TAG = "HomeFragment"
    }

    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: HomeVM
    private lateinit var factory: ViewModelFactory
    private lateinit var hourlyAdapter: HourlyAdapter
    private lateinit var dailyAdapter: DailyAdapter
    private lateinit var onDrawerClick: OnDrawerClick


    private var forecastModel: ForecastModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        factory =
            ViewModelFactory(Repo.getInstance(ApiClient, ConcreteLocalSource(requireContext())))
        viewModel = ViewModelProvider(this, factory)[HomeVM::class.java]
        hourlyAdapter = HourlyAdapter(mutableListOf())
        dailyAdapter = DailyAdapter(mutableListOf())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    //
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val lat =
            SharedHelper.getInstance(requireContext()).read(Constants.LocationKeys.CURRENT_LAT)
                ?.toDouble() ?: 0.0
        val lon =
            SharedHelper.getInstance(requireContext()).read(Constants.LocationKeys.CURRENT_LON)
                ?.toDouble() ?: 0.0
        Log.i(TAG, "onViewCreated: (lat:$lat , lon:$lon)")
        viewModel.getForecast(lat, lon)
        onDrawerClick = activity as OnDrawerClick
        binding.ivMore.setOnClickListener {
            onDrawerClick.onClick()
        }

        binding.ivLocation.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToMapFragment()
            findNavController().navigate(action)
        }
        binding.rvHourly.adapter = hourlyAdapter
        binding.rvDaily.adapter = dailyAdapter


        lifecycleScope.launch {
            viewModel.forecast.collect {
                when (it) {
                    is UiState.Fail -> {
                        Log.e(TAG, "fail: ${it.error}")
                    }

                    UiState.Loading -> {
                        Log.d(TAG, "loading: ")
                    }

                    is UiState.Success -> {
                        Log.i(TAG, "success: ${it.data}")
                        forecastModel = it.data
                        val response = it.data
                        val current = response?.current
                        binding.tvZone.text =
                            response?.timezone?.split("/")?.get(1) ?: "unknown zone"
                        binding.tvCurrentTemp.text = current?.temp?.toInt().toString().plus("°C")
                        //binding.tvCurrentTemp.text = current?.getTemp(SharedHelper.getInstance(requireContext()).read(Constants.TEMP_UNIT)?:"")
                        Glide.with(requireContext())
                            .load("https://openweathermap.org/img/wn/${current?.weather?.get(0)?.icon ?: "02d"}.png")
                            .into(binding.ivCurrent)
                        binding.tvCurrentDes.text =
                            current?.weather?.get(0)?.description ?: "unknown description"
                        binding.tvCurrentTimeDate.text =
                            getCurrentUTCTime(response?.timezone_offset ?: 0.0)
                        binding.tvWind.text = current?.wind_speed.toString().plus(" km/h")
                        binding.tvHumidity.text = current?.humidity.toString().plus(" %")
                        binding.tvPressure.text = current?.pressure.toString().plus(" hPa")
                        binding.tvUv.text = "UV index ".plus(current?.uvi.toString())
                        binding.tvCloudiness.text = current?.clouds.toString().plus(" %")
                        binding.tvDir.text = current?.wind_deg.toString().plus("°")
                        Log.i(TAG, "daily size: ${response?.daily?.size}")
                        dailyAdapter.updateList(response?.daily ?: listOf())
                        Log.i(TAG, "adapter daily size: ${dailyAdapter.itemCount}")
                        Log.i(TAG, "hourly size: ${response?.hourly?.size}")
                        hourlyAdapter.updateList(response?.hourly?.take(24) ?: listOf())
                        Log.i(TAG, "adapter hourly size: ${hourlyAdapter.itemCount}")
                    }
                }
            }
        }

        /*viewModel.forecast.observe(viewLifecycleOwner) {
            if (it.isSuccessful) {
                forecastModel = it.body()
                val response= it?.body()
                val current = response?.current
                binding.tvZone.text = response?.timezone?.split("/")?.get(1)?:"unknown zone"
                binding.tvCurrentTemp.text = current?.temp?.toInt().toString().plus("°C")
                Glide.with(this)
                    .load("https://openweathermap.org/img/wn/${current?.weather?.get(0)?.icon?:"02d"}.png")
                    .into(binding.ivCurrent)
                binding.tvCurrentDes.text = current?.weather?.get(0)?.description?: "unknown description"
                binding.tvCurrentTimeDate.text = getCurrentUTCTime(response?.timezone_offset ?: 0.0)
                binding.tvWind.text = current?.wind_speed.toString().plus(" km/h")
                binding.tvHumidity.text = current?.humidity.toString().plus(" %")
                binding.tvPressure.text = current?.pressure.toString().plus(" hPa")
                binding.tvUv.text = "UV index ".plus(current?.uvi.toString())
                binding.tvCloudiness.text = current?.clouds.toString().plus(" %")
                binding.tvDir.text = current?.wind_deg.toString().plus("°")
                Log.i(TAG, "daily size: ${response?.daily?.size}")
                dailyAdapter.updateList(response?.daily?: listOf())
                Log.i(TAG, "adapter daily size: ${dailyAdapter.itemCount}")
                Log.i(TAG, "hourly size: ${response?.hourly?.size}")
                hourlyAdapter.updateList(response?.hourly?.take(24)?: listOf())
                Log.i(TAG, "adapter hourly size: ${hourlyAdapter.itemCount}")

            } else {
                binding.tvCurrentTemp.text = it.message()
            }
        }*/

        binding.ivFavorite.setOnClickListener {
            forecastModel?.let {
                viewModel.insertForecast(it)
            }
        }


    }


    private fun getCurrentUTCTime(shift: Double): String {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendar.add(Calendar.HOUR_OF_DAY, (shift / 3600).toInt())
        val dateFormat = SimpleDateFormat("HH:mm EEEE, MMM d, yyyy")
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        return dateFormat.format(calendar.time)
    }

}