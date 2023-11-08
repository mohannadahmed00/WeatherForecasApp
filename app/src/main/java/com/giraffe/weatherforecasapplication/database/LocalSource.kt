package com.giraffe.weatherforecasapplication.database

import com.giraffe.weatherforecasapplication.model.ForecastModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow


interface LocalSource {
    suspend fun getAllFavorites():List<ForecastModel>
    suspend fun insertForecast(forecast: ForecastModel):Long
    suspend fun deleteAllForecasts()
    suspend fun deleteForecast(forecast: ForecastModel):Int
}