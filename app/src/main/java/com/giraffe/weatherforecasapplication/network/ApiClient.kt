package com.giraffe.weatherforecasapplication.network

import com.giraffe.weatherforecasapplication.utils.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object ApiClient: RemoteSource {
    private val apiServices = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(Constants.URL)
        .build().create(ApiServices::class.java)
}