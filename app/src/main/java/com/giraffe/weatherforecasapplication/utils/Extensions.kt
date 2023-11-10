package com.giraffe.weatherforecasapplication.utils

fun Double.toKelvin():Double{
    return this + 273.15
}
fun Double.toFahrenheit():Double{
    return (this * (9/5)) + 32
}

fun Double.toMilesPerHours():Double{
    return this *  2.2369
}