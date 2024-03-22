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
    val language: StateFlow<String> = _language.asStateFlow()

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
    fun setLanguage(lang: String) {
        viewModelScope.launch {
            repo.setLanguage(lang)
            _language.emit(lang)
        }
    }
    fun setTempUnit(unit: String) {
        viewModelScope.launch {
            repo.setTempUnit(unit)
            _tempUnit.emit(unit)
        }
    }
    fun setWindSpeedUnit(unit: String) {
        viewModelScope.launch {
            repo.setWindSpeedUnit(unit)
            _windSpeedUnit.emit(unit)
        }
    }

    private val _selectedForecast = MutableStateFlow<UiState<ForecastModel?>>(UiState.Loading)
    val selectedForecast: StateFlow<UiState<ForecastModel?>> = _selectedForecast.asStateFlow()
    fun getForecast(lat: Double, lon: Double, isCurrent: Boolean) {
        _selectedForecast.value = UiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            repo.getForecast(lat, lon, isCurrent)
                .catch {
                    _selectedForecast.emit(UiState.Fail(it.message ?: "unknown error"))
                }
                .collect {
                    _selectedForecast.emit(it)
                }
        }
    }

    private val _favorites = MutableStateFlow<UiState<List<ForecastModel>>>(UiState.Loading)
    val favorites: StateFlow<UiState<List<ForecastModel>>> = _favorites
    fun getFavorites(){
        _favorites.value = UiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            repo.getAllFavorites()
                .catch { _favorites.emit(UiState.Fail(it.message ?: "unknown error")) }
                .collect {
                    _favorites.emit(it)
                }
        }
    }



    private val _insert = MutableStateFlow<UiState<Long>>(UiState.Loading)
    val insert: StateFlow<UiState<Long>> = _insert.asStateFlow()
    fun insertForecast(forecast: ForecastModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.insertForecast(forecast)
                .catch { emit(UiState.Fail(it.message ?: "unknown error")) }
                .collect {
                    _insert.emit(it)
                }
        }
    }

    private val _delete = MutableStateFlow<UiState<Int>>(UiState.Loading)
    val delete: StateFlow<UiState<Int>> = _delete.asStateFlow()
    fun deleteForecast(forecast: ForecastModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteForecast(forecast)
                .catch { emit(UiState.Fail(it.message ?: "unknown error")) }
                .collect {
                    _delete.emit(it)
                }
        }
    }


    //===============================================================old===================================
    fun selectForecast(forecast: ForecastModel?) {
        viewModelScope.launch(Dispatchers.IO) {
            _selectedForecast.emit(UiState.Success(forecast))
        }
    }
    fun selectLocation(lat: Double, lon: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getForecast(lat, lon, false)
                .catch { }
                .collect {
                    when (it) {
                        is UiState.Fail -> {

                        }

                        UiState.Loading -> {

                        }

                        is UiState.Success -> {
                            _selectedForecast.emit(UiState.Success(it.data))
                        }
                    }
                }
            //_selectedLocation.emit(Pair(lat,lon))
        }
    }
}