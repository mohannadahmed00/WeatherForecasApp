package com.giraffe.weatherforecasapplication.features.alerts.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class AlertWorker(context: Context,workerParameters: WorkerParameters): Worker(context,workerParameters) {
    override fun doWork(): Result {
        TODO("Not yet implemented")
    }
}