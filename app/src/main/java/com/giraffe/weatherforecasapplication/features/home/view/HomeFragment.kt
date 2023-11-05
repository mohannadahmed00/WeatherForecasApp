package com.giraffe.weatherforecasapplication.features.home.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.giraffe.weatherforecasapplication.database.ConcreteLocalSource
import com.giraffe.weatherforecasapplication.databinding.FragmentHomeBinding
import com.giraffe.weatherforecasapplication.features.home.viewmodel.HomeVM
import com.giraffe.weatherforecasapplication.model.ForecastModel
import com.giraffe.weatherforecasapplication.model.repo.Repo
import com.giraffe.weatherforecasapplication.network.ApiClient
import com.giraffe.weatherforecasapplication.utils.ViewModelFactory
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone

class HomeFragment : Fragment() {
    companion object {
        const val TAG = "HomeFragment"
    }

    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: HomeVM
    private lateinit var factory: ViewModelFactory

    private var forecastModel: ForecastModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        factory =
            ViewModelFactory(Repo.getInstance(ApiClient, ConcreteLocalSource(requireContext())))
        viewModel = ViewModelProvider(this, factory)[HomeVM::class.java]
        viewModel.getForecast()
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
        viewModel.forecast.observe(viewLifecycleOwner) {
            if (it.isSuccessful) {
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

            } else {
                binding.tvCurrentTemp.text = it.message()
            }
        }


    }


    private fun getCurrentUTCTime(shift: Double):String{
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendar.add(Calendar.HOUR_OF_DAY, (shift/3600).toInt())
        val dateFormat = SimpleDateFormat("HH:mm EEEE, MMM d, yyyy" )
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        return dateFormat.format(calendar.time)
    }

}