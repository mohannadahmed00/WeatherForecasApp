package com.giraffe.weatherforecasapplication.features.alerts.worker

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
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
import com.giraffe.weatherforecasapplication.utils.UiState
import com.giraffe.weatherforecasapplication.utils.getAddress
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

class AlertWorker(val context: Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {
    override fun doWork(): Result {
        val alertId = inputData.getInt(Constants.CLICKED_ALERT_ID, 0)
        val lat = inputData.getDouble(Constants.EXTRA_LAT, 0.0)
        val lon = inputData.getDouble(Constants.EXTRA_LON, 0.0)
        val repo = Repo.getInstance(ApiClient, ConcreteLocalSource(context))
        var result = Result.failure()
        runBlocking {
            repo.getForecast(lat, lon, false).collect {
                result = when (it) {
                    is UiState.Fail -> {
                        Result.failure(workDataOf(Constants.WORKER_ERROR to it.error))
                    }

                    UiState.Loading -> {
                        if (runAttemptCount == 2) {
                            Result.failure()
                        } else {
                            Result.retry()
                        }
                    }

                    is UiState.Success -> {
                        val alert = it.data?.alerts
                        val address = getAddress(context, lat, lon, it.data?.timezone ?: "null,")
                        showNotification(context, address, alert, alertId)
                        Result.success(workDataOf(Constants.WORKER_SUCCESS to (alert != null)))
                    }
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
            .setSmallIcon(R.drawable.sun)
            .setContentIntent(pendingIntent)
        if (alerts == null) {
            notification.setContentText(context.getString(R.string.everything_is_fine_in_that_area))
        } else {
            notification.setContentText(context.getString(R.string.be_careful_of_the_weather_in_that_area_as_there_are_warnings))
        }
        notificationManager.notify(Random.nextInt(1, 1000), notification.build())
    }
}