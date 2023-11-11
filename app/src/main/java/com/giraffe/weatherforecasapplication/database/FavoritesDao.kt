package com.giraffe.weatherforecasapplication.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.giraffe.weatherforecasapplication.model.ForecastModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

@Dao
interface FavoritesDao {
    @Query("SELECT * FROM forecast_table")
    fun getFavForecasts(): List<ForecastModel>

    @Query("SELECT * FROM forecast_table WHERE isCurrent = 1")
    fun getCurrent(): ForecastModel?
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend  fun insertForecast(forecast: ForecastModel):Long
    @Delete
    suspend fun deleteForecast(forecast: ForecastModel):Int
    @Query("DELETE FROM forecast_table")
    suspend fun deleteAllFavForecasts()

    @Query("DELETE FROM forecast_table Where isCurrent = 1")
    suspend fun deleteCurrent()
    @Update
    suspend fun updateForecast(forecast: ForecastModel)
}