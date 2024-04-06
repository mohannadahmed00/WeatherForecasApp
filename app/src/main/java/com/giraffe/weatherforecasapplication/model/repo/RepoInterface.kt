package com.giraffe.weatherforecasapplication.model.repo

import com.giraffe.weatherforecasapplication.model.alert.AlertItem
import com.giraffe.weatherforecasapplication.model.forecast.ForecastModel
import kotlinx.coroutines.flow.Flow

interface RepoInterface {
    suspend fun getForecast(lat: Double, lon: Double): Flow<ForecastModel?>
    suspend fun getFavorites(): Flow<List<ForecastModel>>
    suspend fun insertFavorite(forecast: ForecastModel)
    suspend fun deleteFavorite(timezone:String)
    suspend fun deleteFavorites()
    suspend fun getAlerts(): Flow<List<AlertItem>>
    suspend fun insertAlert(alertItem: AlertItem)
    suspend fun deleteAlert(alertId: Int)
    suspend fun getLanguage(): Flow<String>
    suspend fun getTempUnit(): Flow<String>
    suspend fun getWindSpeedUnit(): Flow<String>
    suspend fun getNotificationFlag(): Flow<Boolean>
    suspend fun setLanguage(lang: String)
    suspend fun setTempUnit(unit: String)
    suspend fun setWindSpeedUnit(unit: String)
    suspend fun setNotificationFlag(notifyFlag: Boolean)
    suspend fun setSelectedForecast(forecast: ForecastModel)
    suspend fun getSelectedForecast():Flow<ForecastModel?>
}