package com.giraffe.weatherforecasapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.giraffe.weatherforecasapplication.model.forecast.ForecastModel
import com.giraffe.weatherforecasapplication.model.repo.RepoInterface
import com.giraffe.weatherforecasapplication.utils.Constants
import com.giraffe.weatherforecasapplication.utils.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class SharedVM(val repo: RepoInterface) : ViewModel() {

    private val _selectedLocation = MutableStateFlow<Pair<Double,Double>?>(null)
    val selectedLocation: StateFlow<Pair<Double,Double>?> = _selectedLocation.asStateFlow()

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

    private val _selectedForecast = MutableStateFlow<ForecastModel?>(null)
    val selectedForecast: StateFlow<ForecastModel?> = _selectedForecast.asStateFlow()
    fun selectForecast(forecast: ForecastModel?) {
        viewModelScope.launch {
            _selectedForecast.emit(forecast)
        }
    }


    fun selectLocation(lat:Double,lon:Double){
        viewModelScope.launch {
            repo.getForecast(lat,lon,false)
                .catch {  }
                .collect{
                    when(it){
                        is UiState.Fail -> {

                        }
                        UiState.Loading -> {

                        }
                        is UiState.Success -> {
                            _selectedForecast.emit(it.data)
                        }
                    }
                }
            //_selectedLocation.emit(Pair(lat,lon))
        }
    }
}