package com.giraffe.weatherforecasapplication.utils

object Constants {
    const val URL = "https://api.openweathermap.org/"
    const val API_KEY = "375d11598481406538e244d548560243"
    const val UNITS = "metric"
    const val TEMP_UNIT = "TEMP_UNIT"
    const val WIND_SPEED_UNIT = "WIND_SPEED_UNIT"
    const val LANGUAGE = "LANGUAGE"
    const val LANG = "en"
    object LocationKeys{
        const val CURRENT_LON = "CURRENT_LON"
        const val CURRENT_LAT = "CURRENT_LAT"
    }

    object Languages{
        const val ARABIC = "CURRENT_LON"
        const val ENGLISH = "CURRENT_LAT"
    }

    object TempUnits{
        const val CELSIUS = "CELSIUS"
        const val FAHRENHEIT = "FAHRENHEIT"
        const val KELVIN = "KELVIN"
    }

    object WindSpeedUnits{
        const val METRE = "METRE"
        const val MILES = "MILES"
    }

}