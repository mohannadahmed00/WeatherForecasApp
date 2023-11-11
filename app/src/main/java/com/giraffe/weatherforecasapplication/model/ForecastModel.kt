package com.giraffe.weatherforecasapplication.model

import androidx.room.Entity

@Entity(tableName = "forecast_table", primaryKeys = ["lat","lon"])
data class ForecastModel(
    val lat: Double = 0.0,
    val lon: Double = 0.0,
    var timezone: String = "",
    val timezone_offset: Double = 0.0,
    val current: Current? = null,
    val daily: List<Daily> = listOf(),
    val hourly: List<Hourly> = listOf(),
    var isCurrent:Boolean= false,
    var isFavorite:Boolean = false,
    //val minutely: List<Minutely>,

)