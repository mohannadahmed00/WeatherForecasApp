package com.giraffe.weatherforecasapplication.network

import com.giraffe.weatherforecasapplication.model.ForecastModel
import retrofit2.Response

interface RemoteSource {
    suspend fun getForecast(lat:Int,lon:Int): Response<ForecastModel>
}