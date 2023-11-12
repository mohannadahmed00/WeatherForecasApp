package com.giraffe.weatherforecasapplication.model.repo

import com.giraffe.weatherforecasapplication.model.forecast.ForecastModel
import com.giraffe.weatherforecasapplication.network.RemoteSource
import retrofit2.Response

class FakeRemoteSource(private val forecast: ForecastModel) : RemoteSource {
    override suspend fun getForecast(lat: Double, lon: Double): Response<ForecastModel> {
        return Response.success(forecast)
    }
}
