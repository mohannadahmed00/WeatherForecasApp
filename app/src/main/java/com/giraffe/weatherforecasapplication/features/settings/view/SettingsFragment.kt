package com.giraffe.weatherforecasapplication.features.settings.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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
        factory = ViewModelFactory(Repo.getInstance(ApiClient, ConcreteLocalSource(requireContext())))
        viewModel = ViewModelProvider(this,factory)[SettingsVM::class.java]
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
        val lang = SharedHelper.getInstance(requireContext()).read(Constants.LANG)
        val tempUnit = SharedHelper.getInstance(requireContext()).read(Constants.TEMP_UNIT)
        val windSpeedUnit = SharedHelper.getInstance(requireContext()).read(Constants.WIND_SPEED_UNIT)

        when(lang){
            Constants.Languages.ENGLISH->{
                binding.tbLang.check(R.id.btn_en)
            }
            Constants.Languages.ARABIC->{
                binding.tbLang.check(R.id.btn_ar)
            }
            else->{
                SharedHelper.getInstance(requireContext()).store(Constants.LANG,Constants.Languages.ENGLISH)
                binding.tbLang.check(R.id.btn_en)
            }
        }
        when(tempUnit){
            Constants.TempUnits.CELSIUS->{
                binding.tbTemp.check(R.id.btn_celsius)
            }
            Constants.TempUnits.FAHRENHEIT->{
                binding.tbTemp.check(R.id.btn_fahrenheit)
            }
            Constants.TempUnits.KELVIN->{
                binding.tbTemp.check(R.id.btn_kelvin)
            }
            else->{
                SharedHelper.getInstance(requireContext()).store(Constants.TEMP_UNIT,Constants.TempUnits.CELSIUS)
                binding.tbTemp.check(R.id.btn_celsius)
            }
        }
        when(windSpeedUnit){
            Constants.WindSpeedUnits.METRE->{
                binding.tbWind.check(R.id.btn_metre)
            }
            Constants.WindSpeedUnits.MILES->{
                binding.tbWind.check(R.id.btn_miles)
            }
            else->{
                SharedHelper.getInstance(requireContext()).store(Constants.WIND_SPEED_UNIT,"metre")
                binding.tbWind.check(R.id.btn_metre)
            }
        }



        binding.tbLang.addOnButtonCheckedListener { _, checkedId, _ ->
            when(checkedId){
                R.id.btn_ar-> {
                    SharedHelper.getInstance(requireContext()).store(Constants.LANG,Constants.Languages.ARABIC)
                    //binding.tbLang.check(R.id.btn_ar)
                }
                R.id.btn_en-> {
                    SharedHelper.getInstance(requireContext()).store(Constants.LANG,Constants.Languages.ENGLISH)
                    //binding.tbLang.check(R.id.btn_en)
                }
            }
        }
        binding.tbTemp.addOnButtonCheckedListener { _, checkedId, _ ->
            when(checkedId){
                R.id.btn_celsius-> {
                    SharedHelper.getInstance(requireContext()).store(Constants.LANG,Constants.TempUnits.CELSIUS)
                    //binding.tbLang.check(R.id.btn_celsius)
                }
                R.id.btn_fahrenheit-> {
                    SharedHelper.getInstance(requireContext()).store(Constants.LANG,Constants.TempUnits.FAHRENHEIT)
                    //binding.tbLang.check(R.id.btn_fahrenheit)
                }
                R.id.btn_kelvin-> {
                    SharedHelper.getInstance(requireContext()).store(Constants.LANG,Constants.TempUnits.KELVIN)
                    //binding.tbLang.check(R.id.btn_kelvin)
                }
            }
        }
        binding.tbWind.addOnButtonCheckedListener { _, checkedId, _ ->
            when(checkedId){
                R.id.btn_miles-> {
                    SharedHelper.getInstance(requireContext()).store(Constants.LANG,Constants.WindSpeedUnits.MILES)
                    //binding.tbLang.check(R.id.btn_miles)
                }
                R.id.btn_metre-> {
                    SharedHelper.getInstance(requireContext()).store(Constants.LANG,Constants.WindSpeedUnits.METRE)
                    //binding.tbLang.check(R.id.btn_metre)
                }
            }
        }
    }
}