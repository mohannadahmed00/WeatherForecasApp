package com.giraffe.weatherforecasapplication.database

import android.content.Context
import com.giraffe.weatherforecasapplication.model.ForecastModel

class ConcreteLocalSource (context: Context) : LocalSource {
    private val dao: FavoritesDao = AppDataBase.getInstance(context).getFavoritesDao()
    override suspend fun getAllFavorites() = dao.getFavForecasts()
    override suspend fun insertForecast(forecast: ForecastModel) = dao.insertForecast(forecast)

    override suspend fun deleteAllForecasts() = dao.deleteAllFavForecasts()
    override suspend fun deleteForecast(forecast: ForecastModel) = dao.deleteForecast(forecast)

}