package com.giraffe.weatherforecasapplication.features.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.giraffe.weatherforecasapplication.model.forecast.ForecastModel
import com.giraffe.weatherforecasapplication.model.repo.RepoInterface
import com.giraffe.weatherforecasapplication.utils.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HomeVM(private val repo: RepoInterface) : ViewModel() {
    private val _forecast =
        MutableStateFlow<UiState<ForecastModel?>>(UiState.Loading)
    val forecast: StateFlow<UiState<ForecastModel?>> = _forecast.asStateFlow()
    fun getForecast(lat: Double, lon: Double, isCurrent: Boolean) {
        _forecast.value = UiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            repo.getForecast(lat, lon, isCurrent)
                .catch {
                    _forecast.emit(UiState.Fail(it.message ?: "unknown error"))
                }
                .collect {
                    _forecast.emit(it)
                }
        }
    }
    fun getCurrentForecast() {
        _forecast.value = UiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            repo.getCurrent()
                .catch {
                    _forecast.emit(UiState.Fail(it.message ?: "unknown error"))
                }
                .collect {
                    _forecast.emit(it)
                }
        }
    }




    private val _favorites = MutableStateFlow<UiState<List<ForecastModel>>>(UiState.Loading)
    val favorites: StateFlow<UiState<List<ForecastModel>>> = _favorites
    /*val coldFavorites: SharedFlow<UiState<List<ForecastModel>>> = _favorites
        .shareIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
        )*/



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
}

