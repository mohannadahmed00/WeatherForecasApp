package com.giraffe.weatherforecasapplication.utils

import android.content.Context
import android.location.Geocoder
import com.giraffe.weatherforecasapplication.R
import java.io.IOException


fun getIconRes(icon: String) = when (icon) {
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

fun Double.toKelvin(): Double {
    return this + 273.15
}

fun Double.toFahrenheit(): Double {
    return (this * (9 / 5)) + 32
}

fun Double.toMilesPerHours(): Double {
    return this * 2.2369
}

fun getAddress(context: Context, latitude: Double, longitude: Double, zone: String): String {
    return try {
        val geoCoder = Geocoder(context)
        val address = geoCoder.getFromLocation(latitude, longitude, 1)
        if (!address.isNullOrEmpty()) {
            "${
                address[0]?.adminArea?.replace(" Governorate", "")?.replace(" Province", "")
            }, ${address[0]?.countryName}"
        } else {
            if (zone.contains("null,")) {
                Constants.UNKNOWN_AREA
            } else {
                zone
            }
        }
    } catch (e: IOException) {
        e.message ?: "unknown error"
    }

}