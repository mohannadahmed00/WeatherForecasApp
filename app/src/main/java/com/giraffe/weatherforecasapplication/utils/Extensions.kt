package com.giraffe.weatherforecasapplication.utils

import android.content.Context
import android.location.Geocoder

fun Double.toKelvin():Double{
    return this + 273.15
}
fun Double.toFahrenheit():Double{
    return (this * (9/5)) + 32
}

fun Double.toMilesPerHours():Double{
    return this *  2.2369
}

fun getAddress(context: Context, latitude: Double, longitude: Double): String {
    val geoCoder = Geocoder(context)
    val address = geoCoder.getFromLocation(latitude, longitude, 1)
    return if (!address.isNullOrEmpty()) {
        "${address[0].adminArea}, ${address[0].countryName}"
    } else {
        Constants.UNKNOWN_AREA
    }
}