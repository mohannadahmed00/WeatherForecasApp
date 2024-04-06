package com.giraffe.weatherforecasapplication.network

import com.giraffe.weatherforecasapplication.model.forecast.ForecastModel
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface RemoteSource {
    suspend fun getForecast(lat:Double,lon:Double,lang:String): Flow<Response<ForecastModel>>
}