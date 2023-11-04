package com.giraffe.weatherforecasapplication.model.repo

import com.giraffe.weatherforecasapplication.model.ForecastModel
import retrofit2.Response

interface RepoInterface {


    suspend fun getForecast(lat:Int,lon:Int): Response<ForecastModel>
    suspend fun insertForecast(forecast:ForecastModel):Long
    suspend fun deleteAllForecasts()
}