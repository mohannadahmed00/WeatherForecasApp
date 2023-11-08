package com.giraffe.weatherforecasapplication.features.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.giraffe.weatherforecasapplication.database.SharedHelper
import com.giraffe.weatherforecasapplication.model.ForecastModel
import com.giraffe.weatherforecasapplication.model.repo.RepoInterface
import com.giraffe.weatherforecasapplication.utils.Constants
import com.giraffe.weatherforecasapplication.utils.UiState
import com.google.android.gms.common.api.Api
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response

class HomeVM(private val repo: RepoInterface) : ViewModel() {
    private val _forecast =
        MutableStateFlow<UiState<ForecastModel?>>(UiState.Loading)//<Response<ForecastModel>>()
    val forecast: StateFlow<UiState<ForecastModel?>> = _forecast.asStateFlow()


    fun getForecast(lat: Double, lon: Double) {
        _forecast.value = UiState.Loading

        viewModelScope.launch {
            repo.getForecast(lat, lon)
                .catch {
                    _forecast.emit(UiState.Fail(it.message ?: "unknown error"))
                }
                .collect {
                    _forecast.emit(it)
                }
            /*if (response.isSuccessful) {
                _forecast.emit(UiState.Success(response.body()))
            } else {
                _forecast.emit(UiState.Fail(response.message() ?: "unknown error"))
            }*/
            /*repo.getForecast(lat,lon)
                .catch { _forecast.emit(UiState.Fail(it.message?:"unknown error")) }
                .collect{
                    if (it.isSuccessful){
                        _forecast.emit(UiState.Success(it.body()))
                    }else{
                        _forecast.emit(UiState.Fail(it.message()?:"unknown error"))
                    }
                }*/
            //_forecast.emit(repo.getForecast(lat,lon))
        }


    }

    fun insertForecast(forecast: ForecastModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.insertForecast(forecast)
        }
    }
}

