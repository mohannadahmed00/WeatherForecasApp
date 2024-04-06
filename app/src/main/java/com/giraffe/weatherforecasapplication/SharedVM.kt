package com.giraffe.weatherforecasapplication

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.giraffe.weatherforecasapplication.features.home.view.HomeFragment
import com.giraffe.weatherforecasapplication.model.forecast.ForecastModel
import com.giraffe.weatherforecasapplication.model.repo.RepoInterface
import com.giraffe.weatherforecasapplication.utils.Constants
import com.giraffe.weatherforecasapplication.utils.UiState
import com.giraffe.weatherforecasapplication.utils.getAddress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class SharedVM(val repo: RepoInterface) : ViewModel() {

    //============================states============================
    private val _language = MutableStateFlow(Constants.Languages.ENGLISH)
    val language: StateFlow<String> = _language.asStateFlow()
    private val _tempUnit = MutableStateFlow(Constants.TempUnits.CELSIUS)
    val tempUnit: StateFlow<String> = _tempUnit.asStateFlow()
    private val _windSpeedUnit = MutableStateFlow(Constants.WindSpeedUnits.METRE)
    val windSpeedUnit: StateFlow<String> = _windSpeedUnit.asStateFlow()
    private val _favorites = MutableStateFlow<List<ForecastModel>>(emptyList())
    val favorites: StateFlow<List<ForecastModel>> = _favorites
    private val _selectedForecast = MutableStateFlow<UiState<ForecastModel?>>(UiState.Loading)
    val selectedForecast: StateFlow<UiState<ForecastModel?>> = _selectedForecast.asStateFlow()

    //============================events============================
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

    fun getFavorites() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getFavorites()
                .collect {
                    Log.d(
                        HomeFragment.TAG,
                        "getFavorites (shared viewmodel): (size = ${it.size}) ${it.map { it.timezone }}"
                    )
                    _favorites.emit(it.sortedBy { item -> item.timezone })
                }
        }
    }

    fun insertFavorite(forecast: ForecastModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.insertFavorite(forecast)
            getFavorites()
            setSelectedForecast(forecast)

        }
    }

    fun justInsertFavorite(forecast: ForecastModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.insertFavorite(forecast)
            getFavorites()
        }
    }

    fun deleteFavorite(forecast: ForecastModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteFavorite(forecast.timezone)
            getFavorites()
            setSelectedForecast(forecast)
        }
    }

    fun justDeleteFavorite(forecast: ForecastModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteFavorite(forecast.timezone)
            getFavorites()
            //setSelectedForecast(forecast)
        }
    }

    fun getSelectedForecast() {

        viewModelScope.launch(Dispatchers.IO) {
            //_selectedForecast.emit(UiState.Loading)
            repo.getSelectedForecast()
                .catch {
                    //Log.e(HomeFragment.TAG, "SharedVM: getSelectedForecast: ${it.message}")
                    _selectedForecast.emit(UiState.Fail(it.message ?: "unknown error"))
                }
                .collect {

                    if (it != null) {
                        Log.d(
                            HomeFragment.TAG,
                            "getSelectedForecast (shared viewmodel): ${it.isFavorite} (it)=>${it.timezone}"
                        )
                        val s = it
                        s.isFavorite = isExistInFavorites(it)
                        Log.d(
                            HomeFragment.TAG,
                            "getSelectedForecast (shared viewmodel): ${s.isFavorite} (s)=>${s.timezone}"
                        )
                        _selectedForecast.emit(UiState.Success(s))
                    } else {
                        _selectedForecast.emit(UiState.Fail("not location has been selected"))
                    }
                }
        }
    }

    private fun isExistInFavorites(forecast: ForecastModel) = favorites.value.any {
        it.timezone == forecast.timezone
    }

    fun setSelectedForecast(forecast: ForecastModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.setSelectedForecast(forecast)
            getSelectedForecast()
        }
    }

    fun del() {
        viewModelScope.launch {
            repo.deleteFavorites()
        }
    }
    //============================fix area============================


    fun getForecast(lat: Double, lon: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            _selectedForecast.emit(UiState.Loading)
            repo.getForecast(lat, lon)
                .catch {
                    //Log.e(HomeFragment.TAG, "getForecast: ${it.message}")
                }
                .collect {
                    it?.let { forecast ->

                        setSelectedForecast(forecast)
                    }
                }
        }
    }


    //===============================================================old===================================

    fun selectLocation(lat: Double, lon: Double) {

        /*viewModelScope.launch(Dispatchers.IO) {
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
        }*/
    }
}