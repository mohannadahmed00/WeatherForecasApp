package com.giraffe.weatherforecasapplication.model.repo

import com.giraffe.weatherforecasapplication.database.LocalSource
import com.giraffe.weatherforecasapplication.model.alert.AlertItem
import com.giraffe.weatherforecasapplication.model.forecast.Alert
import com.giraffe.weatherforecasapplication.model.forecast.ForecastModel

class FakeLocalSource(
    private val forecastList: MutableList<ForecastModel>,
    private val alertsList: MutableList<AlertItem>,
    private val forecast: ForecastModel?,
    private var language:String,
    private var tempUnit:String,
    private var windSpeedUnit:String,
    private var notificationFlag:Boolean,
) : LocalSource {
    override suspend fun getFavorites(): List<ForecastModel> {
        return forecastList
    }

    override suspend fun getCurrent(): ForecastModel? {
        return forecast
    }

    override suspend fun insertFavorite(forecast: ForecastModel): Long {
        forecastList.add(forecast)
        return 1L
    }

    override suspend fun deleteFavorites() {
        forecastList.clear()
    }

    override suspend fun deleteFavorite(forecast: ForecastModel): Int {
        forecastList.remove(forecast)
        return 1
    }

    override suspend fun deleteCurrent() {
        forecastList.removeIf { it.isCurrent }
    }

    override suspend fun getAlerts(): List<AlertItem> {
        return alertsList
    }

    override suspend fun insertAlert(alertItem: AlertItem): Long {
        alertsList.add(alertItem)
        return 1L
    }

    override suspend fun deleteAlert(alertId: Int) {
        alertsList.removeIf { it.id == alertId }
    }

    override suspend fun getLanguage(): String {
        return language
    }

    override suspend fun getTempUnit(): String {
        return tempUnit
    }

    override suspend fun getWindSpeedUnit(): String {
        return windSpeedUnit
    }

    override suspend fun getNotificationFlag(): Boolean {
        return notificationFlag
    }

    override suspend fun setLanguage(lang: String) {
        language = lang
    }

    override suspend fun setTempUnit(unit: String) {
        tempUnit = unit
    }

    override suspend fun setWindSpeedUnit(unit: String) {
        windSpeedUnit = unit
    }

    override suspend fun setNotificationFlag(notifyFlag: Boolean) {
        notificationFlag = notifyFlag
    }
}