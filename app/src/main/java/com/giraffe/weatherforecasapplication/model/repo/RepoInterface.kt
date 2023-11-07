package com.giraffe.weatherforecasapplication.model.repo

import com.giraffe.weatherforecasapplication.model.ForecastModel
import retrofit2.Response

interface RepoInterface {


    suspend fun getForecast(lat:Double,lon:Double): Response<ForecastModel>
    suspend fun insertForecast(forecast:ForecastModel):Long
    suspend fun deleteAllForecasts()
}