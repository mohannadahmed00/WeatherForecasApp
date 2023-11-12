package com.giraffe.weatherforecasapplication.database

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.giraffe.weatherforecasapplication.model.alert.AlertItem
import com.giraffe.weatherforecasapplication.model.forecast.ForecastModel


interface LocalSource {
    suspend fun getAllFavorites():List<ForecastModel>
    suspend fun getCurrent(): ForecastModel?
    suspend fun insertForecast(forecast: ForecastModel):Long
    suspend fun deleteAllForecasts()
    suspend fun deleteForecast(forecast: ForecastModel):Int
    suspend fun deleteCurrent()

    suspend fun getAllAlerts(): List<AlertItem>

    suspend  fun insertAlert(alertItem: AlertItem):Long

    suspend fun deleteAlert(alertId: Int)



    suspend fun getLanguage():String
    suspend fun getTempUnit():String
    suspend fun getWindSpeedUnit():String
    suspend fun getNotificationFlag():Boolean
    suspend fun setLanguage(lang:String)
    suspend fun setTempUnit(unit:String)
    suspend fun setWindSpeedUnit(unit:String)
    suspend fun setNotificationFlag(notifyFlag:Boolean)
}