package com.giraffe.weatherforecasapplication.database

import android.content.Context
import com.giraffe.weatherforecasapplication.model.alert.AlertItem
import com.giraffe.weatherforecasapplication.model.forecast.ForecastModel
import com.giraffe.weatherforecasapplication.utils.Constants
import com.giraffe.weatherforecasapplication.utils.getAddress
import com.google.gson.Gson
import kotlinx.coroutines.flow.flow

class ConcreteLocalSource(val context: Context) : LocalSource {
    private val favoritesDao: FavoritesDao = AppDataBase.getInstance(context).getFavoritesDao()
    private val alertsDao: AlertsDao = AppDataBase.getInstance(context).getAlertsDao()
    private val shared: SharedHelper = SharedHelper.getInstance(context)
    override suspend fun getFavorites() = flow { emit(favoritesDao.getFavorites()) }
    override suspend fun insertFavorite(forecast: ForecastModel) =
        favoritesDao.insertFavorite(
            forecast.copy(
                timezone = getAddress(
                    context,
                    forecast.lat,
                    forecast.lon,
                    forecast.timezone
                )
            )
        )

    override suspend fun deleteFavorites() = favoritesDao.deleteFavorites()
    override suspend fun deleteFavorite(timezone:String) =
        favoritesDao.deleteFavorite(timezone)

    override suspend fun getAlerts() = flow { emit(alertsDao.getAlerts()) }

    override suspend fun insertAlert(alertItem: AlertItem) = alertsDao.insertAlert(alertItem)

    override suspend fun deleteAlert(alertId: Int) = alertsDao.deleteAlert(alertId)

    override suspend fun getLanguage() = flow {
        emit((shared.read(Constants.LANGUAGE) ?: Constants.Languages.ENGLISH).apply {
            shared.store(Constants.LANGUAGE, this)
        })
    }

    override suspend fun getTempUnit() = flow {
        emit(shared.read(Constants.TEMP_UNIT) ?: Constants.TempUnits.CELSIUS.apply {
            shared.store(Constants.TEMP_UNIT, Constants.TempUnits.CELSIUS)
        })
    }

    override suspend fun getWindSpeedUnit() = flow {
        emit((shared.read(Constants.WIND_SPEED_UNIT) ?: Constants.WindSpeedUnits.METRE).apply {
            shared.store(Constants.WIND_SPEED_UNIT, Constants.WindSpeedUnits.METRE)
        })
    }

    override suspend fun getNotificationFlag() = flow {
        emit((shared.read(Constants.NOTIFY_FLAG)?.equals("true") ?: false).apply {
            shared.store(Constants.NOTIFY_FLAG, "true")
        })
    }

    override suspend fun setLanguage(lang: String) = shared.store(Constants.LANGUAGE, lang)

    override suspend fun setTempUnit(unit: String) = shared.store(Constants.TEMP_UNIT, unit)

    override suspend fun setWindSpeedUnit(unit: String) =
        shared.store(Constants.WIND_SPEED_UNIT, unit)

    override suspend fun setNotificationFlag(notifyFlag: Boolean) = if (notifyFlag) {
        shared.store(Constants.NOTIFY_FLAG, "true")
    } else {
        shared.store(Constants.NOTIFY_FLAG, "false")
    }

    override suspend fun setSelectedForecast(forecast: ForecastModel) = shared.store(
        Constants.SELECTED_FORECAST,
        Gson().toJson(
            forecast.copy(
                timezone = getAddress(
                    context,
                    forecast.lat,
                    forecast.lon,
                    forecast.timezone
                )
            )
        )
    )

    override suspend fun getSelectedForecast() = flow {
        emit(
            shared.read(Constants.SELECTED_FORECAST)?.let {
                Gson().fromJson(
                    it,
                    ForecastModel::class.java
                )
            }

        )
    }
}