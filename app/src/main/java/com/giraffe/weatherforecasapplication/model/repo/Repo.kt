package com.giraffe.weatherforecasapplication.model.repo

import com.giraffe.weatherforecasapplication.database.LocalSource
import com.giraffe.weatherforecasapplication.model.alert.AlertItem
import com.giraffe.weatherforecasapplication.model.forecast.ForecastModel
import com.giraffe.weatherforecasapplication.network.RemoteSource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class Repo private constructor(
    private val remoteSource: RemoteSource,
    private val localSource: LocalSource
) : RepoInterface {
    companion object {
        @Volatile
        private var INSTANCE: Repo? = null
        fun getInstance(remoteSource: RemoteSource, localSource: LocalSource): Repo {
            return INSTANCE ?: synchronized(this) {
                val instance = Repo(remoteSource, localSource)
                INSTANCE = instance
                instance
            }
        }
    }

    override suspend fun getForecast(
        lat: Double,
        lon: Double
    ) = remoteSource.getForecast(lat, lon, localSource.getLanguage().first()).map { it.body() }


    override suspend fun getFavorites() = localSource.getFavorites()

    override suspend fun insertFavorite(forecast: ForecastModel) =
        localSource.insertFavorite(forecast)

    override suspend fun getAlerts() = localSource.getAlerts()

    override suspend fun insertAlert(alertItem: AlertItem) = localSource.insertAlert(alertItem)

    override suspend fun deleteAlert(alertId: Int) = localSource.deleteAlert(alertId)


    override suspend fun deleteFavorite(timezone:String) =
        localSource.deleteFavorite(timezone)

    override suspend fun deleteFavorites() = localSource.deleteFavorites()
    override suspend fun getLanguage() = localSource.getLanguage()
    override suspend fun getTempUnit() = localSource.getTempUnit()

    override suspend fun getWindSpeedUnit() = localSource.getWindSpeedUnit()

    override suspend fun getNotificationFlag() = localSource.getNotificationFlag()

    override suspend fun setLanguage(lang: String) = localSource.setLanguage(lang)

    override suspend fun setTempUnit(unit: String) = localSource.setTempUnit(unit)

    override suspend fun setWindSpeedUnit(unit: String) = localSource.setWindSpeedUnit(unit)

    override suspend fun setNotificationFlag(notifyFlag: Boolean) =
        localSource.setNotificationFlag(notifyFlag)

    override suspend fun setSelectedForecast(forecast: ForecastModel) =
        localSource.setSelectedForecast(forecast)

    override suspend fun getSelectedForecast() = localSource.getSelectedForecast()

}