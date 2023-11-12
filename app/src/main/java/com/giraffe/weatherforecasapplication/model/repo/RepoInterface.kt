package com.giraffe.weatherforecasapplication.model.repo

import com.giraffe.weatherforecasapplication.model.alert.AlertItem
import com.giraffe.weatherforecasapplication.model.forecast.ForecastModel
import com.giraffe.weatherforecasapplication.utils.UiState
import kotlinx.coroutines.flow.Flow

interface RepoInterface {
    suspend fun getForecast(lat:Double,lon:Double,isCurrent: Boolean): Flow<UiState<ForecastModel?>>
    suspend fun getAllFavorites(): Flow<UiState<List<ForecastModel>>>
    suspend fun insertForecast(forecast: ForecastModel):Flow<UiState<Long>>
    suspend fun deleteForecast(forecast: ForecastModel):Flow<UiState<Int>>
    suspend fun deleteAllForecasts()
    suspend fun deleteCurrent()
    suspend fun getCurrent():Flow<UiState<ForecastModel?>>

    suspend fun getAllAlerts(): Flow<UiState<List<AlertItem>>>

    suspend  fun insertAlert(alertItem: AlertItem):Flow<UiState<Long>>

    suspend fun deleteAlert(alertId: Int)



    suspend fun getLanguage():Flow<String>
    suspend fun getTempUnit():Flow<String>
    suspend fun getWindSpeedUnit():Flow<String>
    suspend fun getNotificationFlag():Flow<Boolean>
    suspend fun setLanguage(lang:String)
    suspend fun setTempUnit(unit:String)
    suspend fun setWindSpeedUnit(unit:String)
    suspend fun setNotificationFlag(notifyFlag:Boolean)
}