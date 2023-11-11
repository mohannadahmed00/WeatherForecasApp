package com.giraffe.weatherforecasapplication.model.repo

import com.giraffe.weatherforecasapplication.database.LocalSource
import com.giraffe.weatherforecasapplication.model.ForecastModel

class FakeLocalSource:LocalSource {
    override suspend fun getAllFavorites(): List<ForecastModel> {
        TODO("Not yet implemented")
    }

    override suspend fun getCurrent(): ForecastModel? {
        TODO("Not yet implemented")
    }

    override suspend fun insertForecast(forecast: ForecastModel): Long {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllForecasts() {
        TODO("Not yet implemented")
    }

    override suspend fun deleteForecast(forecast: ForecastModel): Int {
        TODO("Not yet implemented")
    }

    override suspend fun deleteCurrent() {
        TODO("Not yet implemented")
    }

    override suspend fun getLanguage(): String {
        TODO("Not yet implemented")
    }

    override suspend fun getTempUnit(): String {
        TODO("Not yet implemented")
    }

    override suspend fun getWindSpeedUnit(): String {
        TODO("Not yet implemented")
    }

    override suspend fun getNotificationFlag(): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun setLanguage(lang: String) {
        TODO("Not yet implemented")
    }

    override suspend fun setTempUnit(unit: String) {
        TODO("Not yet implemented")
    }

    override suspend fun setWindSpeedUnit(unit: String) {
        TODO("Not yet implemented")
    }

    override suspend fun setNotificationFlag(notifyFlag: Boolean) {
        TODO("Not yet implemented")
    }
}