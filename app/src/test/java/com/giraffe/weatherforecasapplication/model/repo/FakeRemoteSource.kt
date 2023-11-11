package com.giraffe.weatherforecasapplication.model.repo

import com.giraffe.weatherforecasapplication.database.LocalSource
import com.giraffe.weatherforecasapplication.model.ForecastModel
import com.giraffe.weatherforecasapplication.network.RemoteSource
import retrofit2.Response

class FakeRemoteSource:RemoteSource {
    override suspend fun getForecast(lat: Double, lon: Double): Response<ForecastModel> {
        return Response.success(ForecastModel(timezone = "egypt"))
    }

}