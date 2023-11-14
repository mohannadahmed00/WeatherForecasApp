package com.giraffe.weatherforecasapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.giraffe.weatherforecasapplication.model.forecast.ForecastModel
import com.giraffe.weatherforecasapplication.model.repo.RepoInterface
import com.giraffe.weatherforecasapplication.utils.Constants
import com.giraffe.weatherforecasapplication.utils.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class SharedVM(val repo: RepoInterface) : ViewModel() {

     private val _language = MutableStateFlow(Constants.Languages.ENGLISH)
    val language: StateFlow<String> = _language

    private val _tempUnit = MutableStateFlow(Constants.TempUnits.CELSIUS)
    val tempUnit: StateFlow<String> = _tempUnit.asStateFlow()

    private val _windSpeedUnit = MutableStateFlow(Constants.WindSpeedUnits.METRE)
    val windSpeedUnit: StateFlow<String> = _windSpeedUnit.asStateFlow()
    fun getLanguage() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getLanguage().collect {
                _language.value = it
            }
        }
    }

    fun getTempUnit() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getTempUnit().collect {
                _tempUnit.value = it
            }
        }
    }

    fun getWindSpeedUnit() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getWindSpeedUnit().collect {
                _windSpeedUnit.emit(it)
            }
        }
    }

    private val _selectedForecast = MutableStateFlow<ForecastModel?>(null)
    val selectedForecast: StateFlow<ForecastModel?> = _selectedForecast.asStateFlow()
    fun selectForecast(forecast: ForecastModel?) {
        viewModelScope.launch(Dispatchers.IO) {
            _selectedForecast.emit(forecast)
        }
    }


    fun selectLocation(lat:Double,lon:Double){
        viewModelScope.launch(Dispatchers.IO) {
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