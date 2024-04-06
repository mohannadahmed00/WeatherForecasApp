package com.giraffe.weatherforecasapplication.features.alerts

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.content.Intent
import android.graphics.PixelFormat
import android.media.MediaPlayer
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.giraffe.weatherforecasapplication.MainActivity
import com.giraffe.weatherforecasapplication.R
import com.giraffe.weatherforecasapplication.database.ConcreteLocalSource
import com.giraffe.weatherforecasapplication.features.alerts.worker.AlertWorker
import com.giraffe.weatherforecasapplication.model.repo.Repo
import com.giraffe.weatherforecasapplication.network.ApiClient
import com.giraffe.weatherforecasapplication.utils.Constants
import com.giraffe.weatherforecasapplication.utils.getAddress
import com.giraffe.weatherforecasapplication.utils.toFahrenheit
import com.giraffe.weatherforecasapplication.utils.toKelvin
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit


class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val alertId = intent?.getIntExtra(Constants.CLICKED_ALERT_ID, 0) ?: return
        val lat = intent.getDoubleExtra(Constants.EXTRA_LAT, 0.0)
        val lon = intent.getDoubleExtra(Constants.EXTRA_LON, 0.0)
        val alertType = intent.getStringExtra(Constants.ALERT_TYPE)?:Constants.AlertTypes.ALARM


        if (intent.hasExtra(Constants.WORKER_FLAG)) {
            val flag = intent.getStringExtra(Constants.WORKER_FLAG)
            if (flag == Constants.WORKER_ON) {
                Log.i("Alarm", "worker : on")
                val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
                val requestBuilder = PeriodicWorkRequestBuilder<AlertWorker>(
                    15, TimeUnit.MINUTES,
                    5, TimeUnit.MINUTES)
                    .setInputData(
                        workDataOf(
                            Constants.CLICKED_ALERT_ID to alertId,
                            Constants.EXTRA_LAT to lat,
                            Constants.EXTRA_LON to lon,
                            Constants.ALERT_TYPE to alertType
                        )
                    )
                    .setBackoffCriteria(BackoffPolicy.LINEAR,10L,TimeUnit.MINUTES)
                    .setConstraints(constraints)
                    .addTag(alertId.toString())

                if (context != null) {
                    val request = requestBuilder.build()
                    WorkManager.getInstance(context).enqueueUniquePeriodicWork(alertId.toString(),ExistingPeriodicWorkPolicy.KEEP,request)
                }

            } else {
                Log.i("Alarm", "worker : off")
                if (context != null) {
                    val repo = Repo.getInstance(ApiClient, ConcreteLocalSource(context))
                    runBlocking {
                        repo.deleteAlert(alertId)
                        WorkManager.getInstance(context).cancelUniqueWork(alertId.toString())
                    }
                }
            }
        }
        else {
            if (context != null) {
                val repo = Repo.getInstance(ApiClient, ConcreteLocalSource(context))
                runBlocking {
                    repo.deleteAlert(alertId)
                    repo.getForecast(lat, lon).collect {
                        if (it!=null){
                            var temp = ""
                            repo.getTempUnit().collect { tempUnit ->
                                temp = when (tempUnit) {
                                    Constants.TempUnits.FAHRENHEIT -> {
                                        "${
                                            it.current?.temp?.toFahrenheit()?.toInt() ?: 0
                                        }°F"
                                    }

                                    Constants.TempUnits.KELVIN -> {
                                        "${it.current?.temp?.toKelvin()?.toInt() ?: 0}K"
                                    }

                                    else -> {
                                        "${it.current?.temp?.toInt() ?: 0}°C"
                                    }
                                }
                            }
                            val address = getAddress(context, lat, lon, it.timezone)

                            repo.getNotificationFlag().collect{flag->
                                Log.d("Alarm", "onReceive: $flag")
                                Log.d("Alarm", "onReceive: ${Constants.AlertTypes.ALARM}")
                                if (flag){
                                    if (alertType==Constants.AlertTypes.ALARM){
                                        showDialog(context, "$address: $temp")
                                    }else{
                                        showNotification(context, address, temp, alertId)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showNotification(
        context: Context,
        locationTitle: String,
        temp: String,
        alertId: Int
    ) {
        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra(Constants.CLICKED_ALERT_ID, alertId)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_MUTABLE)
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
            .setContentText(context.getString(R.string.the_temperature_now_is, temp))
            .setContentTitle(locationTitle)
            .setSmallIcon(R.drawable.ic_04d)
            .setContentIntent(pendingIntent)
            .build()
        notificationManager.notify(7, notification)
    }
    private fun showDialog(context: Context,message:String){
        val mediaPlayer = MediaPlayer.create(context, R.raw.alarm)
        val mView: View
        val mParams: WindowManager.LayoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        mParams.gravity = Gravity.TOP
        val mWindowManager: WindowManager = context.getSystemService(Context.WINDOW_SERVICE) as (WindowManager)
        val layoutInflater: LayoutInflater = context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mediaPlayer.start()
        mediaPlayer.isLooping = true
        mView = layoutInflater.inflate(R.layout.alarm_dialog, null)
        mView.findViewById<TextView>(R.id.descriptionAlarm).text = message
        mView.findViewById<Button>(R.id.btnDismissAlarm).setOnClickListener {
            closeAlarmDialog(mView,mWindowManager, mediaPlayer)
        }
        openAlarmDialog(mView, mWindowManager, mParams)
    }

    private fun openAlarmDialog(mView:View,mWindowManager: WindowManager,mParams: WindowManager.LayoutParams) {
        try {
            if(mView.windowToken ==null) {
                if(mView.parent ==null) {
                    mWindowManager.addView(mView, mParams)
                }
            }
        } catch (e:Exception) {
            Log.d("Open Error",e.toString())
        }
    }

    private fun closeAlarmDialog(mView:View,mWindowManager: WindowManager,mediaPlayer: MediaPlayer) {
        try {
            mediaPlayer.release()
            mWindowManager.removeView(mView)
            mView.invalidate()
            (mView.parent as ViewGroup).removeAllViews()
        } catch (e:Exception) {
            Log.d("Close Error",e.toString())
        }
    }
}