package com.giraffe.weatherforecasapplication.model.alert

import androidx.room.Entity
import java.time.LocalDateTime
import java.time.ZoneId

@Entity(tableName = "alert_table", primaryKeys = ["id"])
data class AlertItem(
    val startDateTime:LocalDateTime,
    val type:String,
    val locationName:String,
    val lat:Double,
    val lon:Double,
    val endDateTime:LocalDateTime? = null,
    val id:Int = lat.toInt()+lon.toInt()+(startDateTime.atZone(ZoneId.systemDefault()).toEpochSecond()*1000).toInt()
)
