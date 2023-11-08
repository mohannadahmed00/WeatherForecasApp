package com.giraffe.weatherforecasapplication.model.repo

import com.giraffe.weatherforecasapplication.model.ForecastModel
import com.giraffe.weatherforecasapplication.utils.UiState
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface RepoInterface {
    suspend fun getForecast(lat:Double,lon:Double): Flow<UiState<ForecastModel?>>
    suspend fun getAllFavorites(): List<ForecastModel>
    suspend fun insertForecast(forecast:ForecastModel):Long
    suspend fun deleteForecast(forecast:ForecastModel):Int
    suspend fun deleteAllForecasts()
}