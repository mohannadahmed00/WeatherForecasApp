package com.giraffe.weatherforecasapplication.features.alerts.worker

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
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
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.giraffe.weatherforecasapplication.MainActivity
import com.giraffe.weatherforecasapplication.R
import com.giraffe.weatherforecasapplication.database.ConcreteLocalSource
import com.giraffe.weatherforecasapplication.model.forecast.Alert
import com.giraffe.weatherforecasapplication.model.repo.Repo
import com.giraffe.weatherforecasapplication.network.ApiClient
import com.giraffe.weatherforecasapplication.utils.Constants
import com.giraffe.weatherforecasapplication.utils.getAddress
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

class AlertWorker(val context: Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {
    override fun doWork(): Result {
        val alertId = inputData.getInt(Constants.CLICKED_ALERT_ID, 0)
        val lat = inputData.getDouble(Constants.EXTRA_LAT, 0.0)
        val lon = inputData.getDouble(Constants.EXTRA_LON, 0.0)
        val alertType = inputData.getString(Constants.ALERT_TYPE)
        val repo = Repo.getInstance(ApiClient, ConcreteLocalSource(context))
        var result = Result.failure()
        runBlocking {
            repo.getNotificationFlag().collect { flag ->
                if (flag) {
                    repo.getForecast(lat, lon).collect {
                        result = if (it != null) {
                            val alert = it.alerts
                            val address = getAddress(context, lat, lon, it.timezone)
                            if (alertType == Constants.AlertTypes.ALARM) {
                                showDialog(context, address, alert)
                            } else {
                                showNotification(context, address, alert, alertId)
                            }
                            Result.success(workDataOf(Constants.WORKER_SUCCESS to (alert != null)))
                        } else {
                            if (runAttemptCount == 2) {
                                Result.failure(workDataOf(Constants.WORKER_ERROR to "unknown error"))
                            } else {
                                Result.retry()
                            }
                        }
                    }
                } else {
                    result =
                        Result.failure(workDataOf(Constants.WORKER_ERROR to "notifications is turned off"))
                }
            }

        }
        return result
    }

    private fun showNotification(
        context: Context,
        locationTitle: String,
        alerts: List<Alert>?,
        alertId: Int
    ) {
        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra(Constants.CLICKED_ALERT_ID, alertId)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_MUTABLE)
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(context, Constants.ALERT_CHANNEL_ID)
            .setContentTitle(locationTitle)
            .setSmallIcon(R.drawable.ic_04d)
            .setContentIntent(pendingIntent)
        if (alerts == null) {
            notification.setContentText(context.getString(R.string.everything_is_fine_in_that_area))
        } else {
            notification.setContentText(context.getString(R.string.be_careful_of_the_weather_in_that_area_as_there_are_warnings))
        }
        notificationManager.notify(Random.nextInt(1, 1000), notification.build())
    }

    private fun showDialog(context: Context, location: String, alerts: List<Alert>?) {
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
        val mWindowManager: WindowManager =
            context.getSystemService(Context.WINDOW_SERVICE) as (WindowManager)
        val layoutInflater: LayoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mediaPlayer.start()
        mediaPlayer.isLooping = true
        mView = layoutInflater.inflate(R.layout.alarm_dialog, null)
        if (alerts == null) {
            mView.findViewById<TextView>(R.id.descriptionAlarm).text =
                location.plus(": ${context.getString(R.string.everything_is_fine_in_that_area)}")
        } else {
            mView.findViewById<TextView>(R.id.descriptionAlarm).text =
                location.plus(": ${context.getString(R.string.be_careful_of_the_weather_in_that_area_as_there_are_warnings)}")
        }
        mView.findViewById<TextView>(R.id.descriptionAlarm).text = location
        mView.findViewById<Button>(R.id.btnDismissAlarm).setOnClickListener {
            closeAlarmDialog(mView, mWindowManager, mediaPlayer)
        }
        openAlarmDialog(mView, mWindowManager, mParams)
    }

    private fun openAlarmDialog(
        mView: View,
        mWindowManager: WindowManager,
        mParams: WindowManager.LayoutParams
    ) {
        try {
            if (mView.windowToken == null) {
                if (mView.parent == null) {
                    mWindowManager.addView(mView, mParams)
                }
            }
        } catch (e: Exception) {
            Log.d("Open Error", e.toString())
        }
    }

    private fun closeAlarmDialog(
        mView: View,
        mWindowManager: WindowManager,
        mediaPlayer: MediaPlayer
    ) {
        try {
            mediaPlayer.release()
            mWindowManager.removeView(mView)
            mView.invalidate()
            (mView.parent as ViewGroup).removeAllViews()
        } catch (e: Exception) {
            Log.d("Close Error", e.toString())
        }
    }
}