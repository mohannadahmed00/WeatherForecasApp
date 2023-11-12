package com.giraffe.weatherforecasapplication.features.alerts

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.giraffe.weatherforecasapplication.model.alert.AlertItem
import com.giraffe.weatherforecasapplication.utils.Constants
import java.time.ZoneId

class AlarmScheduler(private val context: Context) : IAlarmScheduler {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)
    override fun schedule(item: AlertItem) {
        val intent = Intent(context,AlarmReceiver::class.java).apply {
            putExtra(Constants.CLICKED_ALERT_ID,item.id)
            putExtra(Constants.EXTRA_LAT,item.lat)
            putExtra(Constants.EXTRA_LON,item.lon)
        }
        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            item.startDateTime.atZone(ZoneId.systemDefault()).toEpochSecond()*1000,
            PendingIntent.getBroadcast(
                context,
                item.id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

    override fun cancel(item: AlertItem) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                item.id,
                Intent(context,AlarmReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }


}