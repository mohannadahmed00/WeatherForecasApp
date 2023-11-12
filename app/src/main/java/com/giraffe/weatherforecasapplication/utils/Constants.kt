package com.giraffe.weatherforecasapplication.utils

object Constants {
    const val URL = "https://api.openweathermap.org/"
    const val API_KEY = "375d11598481406538e244d548560243"
    const val UNITS = "metric"
    const val TEMP_UNIT = "TEMP_UNIT"
    const val WIND_SPEED_UNIT = "WIND_SPEED_UNIT"
    const val LANGUAGE = "LANGUAGE"
    const val NOTIFY_FLAG = "NOTIFY_FLAG"
    const val EXTRA_MESSAGE = "EXTRA_MESSAGE"
    const val EXTRA_LAT = "EXTRA_LAT"
    const val EXTRA_LON = "EXTRA_LON"
    const val UNKNOWN_AREA = "UNKNOWN_AREA"
    const val CLICKED_NOTIFICATION = "CLICKED_NOTIFICATION"
    const val CLICKED_LAT = "CLICKED_LAT"
    const val CLICKED_LON = "CLICKED_LON"
    const val CLICKED_DATE_TIME = "CLICKED_DATE_TIME"
    const val CLICKED_ALERT_ID = "CLICKED_ALERT_ID"



    object LocationKeys{
        const val CURRENT_LON = "CURRENT_LON"
        const val CURRENT_LAT = "CURRENT_LAT"
    }

    object Languages{
        const val ARABIC = "ARABIC"
        const val ENGLISH = "ENGLISH"
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

    object AlertType{
        const val ALARM = "ALARM"
        const val NOTIFICATION = "NOTIFICATION"
    }

}