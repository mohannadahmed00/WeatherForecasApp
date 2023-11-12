package com.giraffe.weatherforecasapplication.model.repo

import com.giraffe.weatherforecasapplication.database.LocalSource
import com.giraffe.weatherforecasapplication.model.forecast.ForecastModel

class FakeLocalSource(
    private val list: MutableList<ForecastModel>,
    private val forecast: ForecastModel?
) : LocalSource {
    override suspend fun getAllFavorites(): List<ForecastModel> {
        return list
    }

    override suspend fun getCurrent(): ForecastModel? {
        return forecast
    }

    override suspend fun insertForecast(forecast: ForecastModel): Long {
        list.add(forecast)
        return 1
    }

    override suspend fun deleteAllForecasts() {
        list.remove(forecast)
    }

    override suspend fun deleteForecast(forecast: ForecastModel): Int {
        list.clear()
        return 1
    }

    override suspend fun deleteCurrent() {
        list.removeIf { it.isCurrent }
    }

    override suspend fun getLanguage(): String {
        return "arabic"
    }

    override suspend fun getTempUnit(): String {
        return "celsius"
    }

    override suspend fun getWindSpeedUnit(): String {
        return "metre"
    }

    override suspend fun getNotificationFlag(): Boolean {
        return true
    }

    override suspend fun setLanguage(lang: String) {}

    override suspend fun setTempUnit(unit: String) {}

    override suspend fun setWindSpeedUnit(unit: String) {}

    override suspend fun setNotificationFlag(notifyFlag: Boolean) {}
}