package com.giraffe.weatherforecasapplication.database

import com.giraffe.weatherforecasapplication.model.ForecastModel


interface LocalSource {
    suspend fun insertForecast(forecast: ForecastModel):Long
    suspend fun deleteAllForecasts()
}