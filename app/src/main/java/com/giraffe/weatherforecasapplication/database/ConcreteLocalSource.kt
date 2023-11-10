package com.giraffe.weatherforecasapplication.database

import android.content.Context
import com.giraffe.weatherforecasapplication.model.ForecastModel
import com.giraffe.weatherforecasapplication.utils.Constants

class ConcreteLocalSource(context: Context) : LocalSource {
    private val dao: FavoritesDao = AppDataBase.getInstance(context).getFavoritesDao()
    private val shared: SharedHelper = SharedHelper.getInstance(context)
    override suspend fun getAllFavorites() = dao.getFavForecasts()
    override suspend fun insertForecast(forecast: ForecastModel) = dao.insertForecast(forecast)
    override suspend fun deleteAllForecasts() = dao.deleteAllFavForecasts()
    override suspend fun deleteForecast(forecast: ForecastModel) = dao.deleteForecast(forecast)
    override suspend fun getLanguage(): String {
        return shared.read(Constants.LANGUAGE) ?: Constants.Languages.ENGLISH.apply {
            shared.store(Constants.LANGUAGE, this)
        }
    }

    override suspend fun getTempUnit(): String {
        return shared.read(Constants.TEMP_UNIT) ?: Constants.TempUnits.CELSIUS.apply {
            shared.store(Constants.TEMP_UNIT, Constants.TempUnits.CELSIUS)
        }
    }

    override suspend fun getWindSpeedUnit(): String {
        return shared.read(Constants.WIND_SPEED_UNIT) ?: Constants.WindSpeedUnits.METRE.apply {
            shared.store(Constants.WIND_SPEED_UNIT, Constants.WindSpeedUnits.METRE)
        }
    }

    override suspend fun getNotificationFlag(): Boolean {
        return shared.read(Constants.NOTIFY_FLAG)?.equals("true") ?: false.apply {
            shared.store(Constants.NOTIFY_FLAG, "false")
        }
    }

    override suspend fun setLanguage(lang: String) {
        shared.store(Constants.LANGUAGE, lang)
    }

    override suspend fun setTempUnit(unit: String) {
        shared.store(Constants.TEMP_UNIT, unit)
    }

    override suspend fun setWindSpeedUnit(unit: String) {
        shared.store(Constants.WIND_SPEED_UNIT, unit)
    }

    override suspend fun setNotificationFlag(notifyFlag: Boolean) {
        if (notifyFlag) {
            shared.store(Constants.NOTIFY_FLAG, "true")
        } else {
            shared.store(Constants.NOTIFY_FLAG, "false")
        }
    }

}