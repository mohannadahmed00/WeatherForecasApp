package com.giraffe.weatherforecasapplication.model.forecast

import android.util.Log
import com.giraffe.weatherforecasapplication.R

data class Weather(
    val description: String,
    val icon: String,
    val id: Double,
    val main: String
)/* {
    val iconRes = when (icon) {
        "01n" -> R.drawable.ic_01n
        "01d" -> R.drawable.ic_01d
        "02n" -> R.drawable.ic_02n
        "02d" -> R.drawable.ic_02d
        "03n", "03d" -> R.drawable.ic_03
        "04n" -> R.drawable.ic_04n
        "04d" -> R.drawable.ic_04d
        "09n", "09d" -> R.drawable.ic_09
        "10n" -> R.drawable.ic_10n
        "10d" -> R.drawable.ic_10d
        "11n", "11d" -> R.drawable.ic_11
        "13n" -> R.drawable.ic_13n
        "13d" -> R.drawable.ic_13d
        "50n", "50d" -> R.drawable.ic_50
        else -> R.drawable.ic_04d
    }
}*/