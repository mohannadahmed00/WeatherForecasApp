package com.giraffe.weatherforecasapplication.features.alerts

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.giraffe.weatherforecasapplication.MainActivity
import com.giraffe.weatherforecasapplication.R
import com.giraffe.weatherforecasapplication.database.ConcreteLocalSource
import com.giraffe.weatherforecasapplication.model.repo.Repo
import com.giraffe.weatherforecasapplication.model.repo.RepoInterface
import com.giraffe.weatherforecasapplication.network.ApiClient
import com.giraffe.weatherforecasapplication.utils.Constants
import com.giraffe.weatherforecasapplication.utils.UiState
import com.giraffe.weatherforecasapplication.utils.getAddress
import com.giraffe.weatherforecasapplication.utils.toFahrenheit
import com.giraffe.weatherforecasapplication.utils.toKelvin
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.suspendCoroutine


class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val alertId = intent?.getIntExtra(Constants.CLICKED_ALERT_ID,0)?:return
        val lat = intent.getDoubleExtra(Constants.EXTRA_LAT,0.0)
        val lon = intent.getDoubleExtra(Constants.EXTRA_LON,0.0)






        //====================================================

        if (context!=null){
            val repo = Repo.getInstance(ApiClient, ConcreteLocalSource(context))
            runBlocking {
                repo.deleteAlert(alertId)
                repo.getForecast(lat, lon, false).collect {
                    when (it) {
                        is UiState.Fail -> {

                        }

                        UiState.Loading -> {

                        }

                        is UiState.Success -> {

                            var temp = ""
                            repo.getTempUnit().collect{ tempUnit->
                                temp = when (tempUnit) {
                                    Constants.TempUnits.FAHRENHEIT -> {
                                        "${it.data?.current?.temp?.toFahrenheit()?.toInt() ?: 0}°F"
                                    }
                                    Constants.TempUnits.KELVIN -> {
                                        "${it.data?.current?.temp?.toKelvin()?.toInt() ?: 0}K"
                                    }
                                    else -> {
                                        "${it.data?.current?.temp?.toInt() ?: 0}°C"
                                    }
                                }
                            }
                            var address = getAddress(context,lat,lon)
                            if (address == Constants.UNKNOWN_AREA){
                                it.data?.timezone?.let {zone->
                                    address = zone
                                }
                            }
                            showNotification(context,address,temp,alertId)
                        }
                    }
                }
            }
        }

    }

    private fun showNotification(context: Context,locationTitle:String,temp:String,alertId:Int){
        val intent = Intent(context,MainActivity::class.java)
        intent.putExtra(Constants.CLICKED_ALERT_ID,alertId)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_MUTABLE)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(context, "channel id")
            .setContentText("the temperature now is $temp")
            .setContentTitle(locationTitle)
            .setSmallIcon(R.drawable.sun)
            .setContentIntent(pendingIntent)
            .build()
        notificationManager.notify(7,notification)
    }
}