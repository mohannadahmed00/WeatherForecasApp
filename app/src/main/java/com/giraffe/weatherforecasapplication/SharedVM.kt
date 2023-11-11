package com.giraffe.weatherforecasapplication

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.giraffe.weatherforecasapplication.features.home.view.HomeFragment
import com.giraffe.weatherforecasapplication.model.ForecastModel
import com.giraffe.weatherforecasapplication.model.repo.RepoInterface
import com.giraffe.weatherforecasapplication.utils.Constants
import com.giraffe.weatherforecasapplication.utils.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SharedVM(val repo: RepoInterface) : ViewModel() {


    private val _language = MutableStateFlow(Constants.Languages.ENGLISH)
    val language: StateFlow<String> = _language.asStateFlow()

    private val _tempUnit = MutableStateFlow(Constants.TempUnits.CELSIUS)
    val tempUnit: StateFlow<String> = _tempUnit.asStateFlow()

    private val _windSpeedUnit = MutableStateFlow(Constants.WindSpeedUnits.METRE)
    val windSpeedUnit: StateFlow<String> = _windSpeedUnit.asStateFlow()

    private val _notifyFlag = MutableStateFlow(false)
    val notifyFlag: StateFlow<Boolean> = _notifyFlag.asStateFlow()
    fun getLanguage() {
        viewModelScope.launch {
            repo.getLanguage().collect {
                _language.value = it
            }
        }
    }

    fun getTempUnit() {
        viewModelScope.launch {
            repo.getTempUnit().collect {
                _tempUnit.value = it
            }
        }
    }

    fun getWindSpeedUnit() {
        viewModelScope.launch {
            repo.getWindSpeedUnit().collect {
                _windSpeedUnit.value = it
            }
        }
    }

    fun getNotificationFlag() {
        viewModelScope.launch {
            repo.getNotificationFlag().collect {
                _notifyFlag.value = it
            }
        }
    }


    /*private val _selectedForecast = MutableLiveData<ForecastModel?>()
    val selectedForecast: LiveData<ForecastModel?> = _selectedForecast*/

    private val _selectedForecast = MutableStateFlow<UiState<ForecastModel?>>(UiState.Loading)
    val selectedForecast: StateFlow<UiState<ForecastModel?>> = _selectedForecast.asStateFlow()
    fun setForecast(forecast: ForecastModel?) {
        viewModelScope.launch {
            _selectedForecast.emit(UiState.Success(forecast))
        }
    }
}