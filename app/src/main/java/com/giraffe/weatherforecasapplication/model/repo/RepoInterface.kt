package com.giraffe.weatherforecasapplication.model.repo

import com.giraffe.weatherforecasapplication.model.Current
import com.giraffe.weatherforecasapplication.model.ForecastModel
import com.giraffe.weatherforecasapplication.utils.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import retrofit2.Response

interface RepoInterface {
    suspend fun getForecast(lat:Double,lon:Double,isCurrent: Boolean): Flow<UiState<ForecastModel?>>
    suspend fun getAllFavorites(): Flow<UiState<List<ForecastModel>>>
    suspend fun insertForecast(forecast:ForecastModel):Long
    suspend fun deleteForecast(forecast:ForecastModel):Flow<UiState<Int>>
    suspend fun deleteAllForecasts()
    suspend fun deleteCurrent()
    suspend fun getCurrent():Flow<UiState<ForecastModel?>>




    suspend fun getLanguage():Flow<String>
    suspend fun getTempUnit():Flow<String>
    suspend fun getWindSpeedUnit():Flow<String>
    suspend fun getNotificationFlag():Flow<Boolean>
    suspend fun setLanguage(lang:String)
    suspend fun setTempUnit(unit:String)
    suspend fun setWindSpeedUnit(unit:String)
    suspend fun setNotificationFlag(notifyFlag:Boolean)
}