package com.giraffe.weatherforecasapplication.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.giraffe.weatherforecasapplication.model.ForecastModel

@Dao
interface FavoritesDao {
    @Query("SELECT * FROM forecast_table")
    suspend fun getFavForecasts(): List<ForecastModel>
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend  fun insertForecast(forecast: ForecastModel):Long
    @Delete
    suspend fun deleteForecast(forecast: ForecastModel):Int
    @Query("DELETE FROM forecast_table")
    suspend fun deleteAllFavForecasts()
    @Update
    suspend fun updateForecast(forecast: ForecastModel)
}