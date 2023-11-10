package com.giraffe.weatherforecasapplication.features.settings.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.giraffe.weatherforecasapplication.OnDrawerClick
import com.giraffe.weatherforecasapplication.R
import com.giraffe.weatherforecasapplication.database.ConcreteLocalSource
import com.giraffe.weatherforecasapplication.database.SharedHelper
import com.giraffe.weatherforecasapplication.databinding.FragmentSettingsBinding
import com.giraffe.weatherforecasapplication.features.settings.viewmodel.SettingsVM
import com.giraffe.weatherforecasapplication.model.repo.Repo
import com.giraffe.weatherforecasapplication.network.ApiClient
import com.giraffe.weatherforecasapplication.utils.Constants
import com.giraffe.weatherforecasapplication.utils.ViewModelFactory
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {
    companion object {
        const val TAG = "SettingsFragment"
    }

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var viewModel: SettingsVM
    private lateinit var factory: ViewModelFactory

    private lateinit var onDrawerClick: OnDrawerClick
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        factory =
            ViewModelFactory(Repo.getInstance(ApiClient, ConcreteLocalSource(requireContext())))
        viewModel = ViewModelProvider(this, factory)[SettingsVM::class.java]
        viewModel.getLanguage()
        viewModel.getTempUnit()
        viewModel.getWindSpeedUnit()
        viewModel.getNotificationFlag()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onDrawerClick = activity as OnDrawerClick
        binding.ivMore.setOnClickListener {
            onDrawerClick.onClick()
        }


        lifecycleScope.launch {
            viewModel.language.collect {
                Log.i(TAG, "language: $it")
                when (it) {
                    Constants.Languages.ENGLISH -> {
                        binding.tbLang.check(R.id.btn_en)
                    }

                    Constants.Languages.ARABIC -> {
                        binding.tbLang.check(R.id.btn_ar)
                    }
                }
            }
        }
        lifecycleScope.launch {
            viewModel.tempUnit.collect {
                Log.i(TAG, "tempUnit: $it")
                when (it) {
                    Constants.TempUnits.CELSIUS -> {
                        binding.tbTemp.check(R.id.btn_celsius)
                    }

                    Constants.TempUnits.FAHRENHEIT -> {
                        binding.tbTemp.check(R.id.btn_fahrenheit)
                    }

                    Constants.TempUnits.KELVIN -> {
                        binding.tbTemp.check(R.id.btn_kelvin)
                    }
                }
            }
        }
        lifecycleScope.launch {
            viewModel.windSpeedUnit.collect {
                Log.i(TAG, "windSpeedUnit: $it")
                when (it) {
                    Constants.WindSpeedUnits.METRE -> {
                        binding.tbWind.check(R.id.btn_metre)
                    }

                    Constants.WindSpeedUnits.MILES -> {
                        binding.tbWind.check(R.id.btn_miles)
                    }
                }
            }
        }
        lifecycleScope.launch {
            viewModel.notifyFlag.collect {
                Log.i(TAG, "notifyFlag: $it")
                when (it) {
                    true -> {
                        binding.tbNotification.check(R.id.btn_on)
                    }
                    false -> {
                        binding.tbNotification.check(R.id.btn_off)
                    }
                }
            }
        }




        binding.tbLang.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.btn_ar -> {
                        Log.d(TAG, "select: ${Constants.Languages.ARABIC}")
                        viewModel.setLanguage(Constants.Languages.ARABIC)
                    }

                    R.id.btn_en -> {
                        Log.d(TAG, "select: ${Constants.Languages.ENGLISH}")
                        viewModel.setLanguage(Constants.Languages.ENGLISH)
                    }
                }
            }
        }
        binding.tbTemp.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.btn_celsius -> {
                        Log.d(TAG, "select: ${Constants.TempUnits.CELSIUS}")
                        viewModel.setTempUnit(Constants.TempUnits.CELSIUS)
                    }

                    R.id.btn_fahrenheit -> {
                        Log.d(TAG, "select: ${Constants.TempUnits.FAHRENHEIT}")
                        viewModel.setTempUnit(Constants.TempUnits.FAHRENHEIT)
                    }

                    R.id.btn_kelvin -> {
                        Log.d(TAG, "select: ${Constants.TempUnits.KELVIN}")
                        viewModel.setTempUnit(Constants.TempUnits.KELVIN)
                    }
                }
            }
        }
        binding.tbWind.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.btn_miles -> {
                        Log.d(TAG, "select: ${Constants.WindSpeedUnits.MILES}")
                        viewModel.setWindSpeedUnit(Constants.WindSpeedUnits.MILES)
                    }

                    R.id.btn_metre -> {
                        Log.d(TAG, "select: ${Constants.WindSpeedUnits.METRE}")

                        viewModel.setWindSpeedUnit(Constants.WindSpeedUnits.METRE)
                    }
                }
            }
        }
        binding.tbNotification.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.btn_on -> {
                        viewModel.setNotificationFlag(true)
                    }

                    R.id.btn_off -> {
                        viewModel.setNotificationFlag(false)
                    }
                }
            }
        }
    }
}