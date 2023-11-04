package com.giraffe.weatherforecasapplication.model

data class Current(
    val clouds: Double,
    val dew_point: Double,
    val dt: Double,
    val feels_like: Double,
    val humidity: Double,
    val pressure: Double,
    val sunrise: Double,
    val sunset: Double,
    val temp: Double,
    val uvi: Double,
    val visibility: Double,
    val weather: List<Weather>,
    val wind_deg: Double,
    val wind_gust: Double,
    val wind_speed: Double
)