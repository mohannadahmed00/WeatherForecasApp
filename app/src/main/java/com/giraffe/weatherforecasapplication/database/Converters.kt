package com.giraffe.weatherforecasapplication.database

import androidx.room.TypeConverter
import com.giraffe.weatherforecasapplication.model.Current
import com.giraffe.weatherforecasapplication.model.Daily
import com.giraffe.weatherforecasapplication.model.Hourly
import com.giraffe.weatherforecasapplication.model.Minutely
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromCurrent(current: Current): String {
        return Gson().toJson(current)
    }
    @TypeConverter
    fun toCurrent(json: String): Current {
        return Gson().fromJson(json,Current::class.java)
    }

    @TypeConverter
    fun fromDaily(daily: List<Daily>): String {
        return Gson().toJson(daily)
    }
    @TypeConverter
    fun toDaily(json: String): List<Daily> {
        return try {
            Gson().fromJson<List<Daily>>(json)
        } catch (e: Exception) {
            arrayListOf()
        }
    }

    @TypeConverter
    fun fromHourly(hourly: List<Hourly>): String {
        return Gson().toJson(hourly)
    }
    @TypeConverter
    fun toHourly(json: String): List<Hourly> {
        return try {
            Gson().fromJson<List<Hourly>>(json)
        } catch (e: Exception) {
            arrayListOf()
        }
    }

    inline fun <reified T> Gson.fromJson(json: String) = fromJson<T>(json, object : TypeToken<T>() {}.type)
}
