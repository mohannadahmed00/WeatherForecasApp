package com.giraffe.weatherforecasapplication.features.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.giraffe.weatherforecasapplication.database.SharedHelper
import com.giraffe.weatherforecasapplication.model.ForecastModel
import com.giraffe.weatherforecasapplication.model.repo.RepoInterface
import com.giraffe.weatherforecasapplication.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response

class HomeVM(private val repo: RepoInterface):ViewModel() {
    private val _forecast = MutableLiveData<Response<ForecastModel>>()
    val forecast:LiveData<Response<ForecastModel>> = _forecast

    fun getForecast(lat:Double,lon:Double){
        viewModelScope.launch {

           _forecast.postValue(repo.getForecast(lat,lon))
        }
    }

    fun insertForecast(forecast: ForecastModel){
        viewModelScope.launch(Dispatchers.IO) {
            repo.insertForecast(forecast)
        }
    }
}

