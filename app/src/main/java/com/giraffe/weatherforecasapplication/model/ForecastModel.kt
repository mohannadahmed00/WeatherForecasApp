package com.giraffe.weatherforecasapplication.model

import androidx.room.Entity

@Entity(tableName = "forecast_table", primaryKeys = ["lat","lon"])
data class ForecastModel(
    val current: Current,
    val daily: List<Daily>,
    val hourly: List<Hourly>,
    val lat: Int,
    val lon: Int,
    val minutely: List<Minutely>,
    val timezone: String,
    val timezone_offset: Int
)