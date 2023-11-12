package com.giraffe.weatherforecasapplication.features.alerts

import com.giraffe.weatherforecasapplication.model.alert.AlertItem

interface IAlarmScheduler {
    fun schedule(item: AlertItem)
    fun cancel(item: AlertItem)
}