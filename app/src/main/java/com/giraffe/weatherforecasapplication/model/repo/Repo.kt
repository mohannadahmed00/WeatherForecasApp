package com.giraffe.weatherforecasapplication.model.repo

import android.util.Log
import com.giraffe.weatherforecasapplication.database.ConcreteLocalSource
import com.giraffe.weatherforecasapplication.model.ForecastModel
import com.giraffe.weatherforecasapplication.network.RemoteSource
import com.giraffe.weatherforecasapplication.utils.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import retrofit2.Response

class Repo private constructor(private val remoteSource: RemoteSource,private val localSource: ConcreteLocalSource):RepoInterface {
    companion object {
        @Volatile
        private var INSTANCE: Repo? = null
        fun getInstance(remoteSource: RemoteSource, localSource: ConcreteLocalSource): Repo {
            return INSTANCE ?: synchronized(this) {
                val instance = Repo(remoteSource,localSource)
                INSTANCE = instance
                instance
            }
        }
    }

    override suspend fun getForecast(lat: Double, lon: Double): Flow<UiState<ForecastModel?>> {
        return flow{
            try {
                val response = remoteSource.getForecast(lat, lon)
                if (response.isSuccessful) {
                    emit(UiState.Success(response.body()))
                } else {
                    emit(UiState.Fail(response.message()))
                }
            } catch (e: Exception) {
                emit(UiState.Fail(e.message?:"unknown error"))
            }
            remoteSource.getForecast(lat, lon)
        }.onStart { UiState.Loading }
    }
        /*return try{
            var state:UiState<ForecastModel?> = UiState.Loading
            remoteSource.getForecast(lat, lon)
                .catch { state = UiState.Fail(it.message ?: "unknown error") }
                .collect {
                    state = if (it.isSuccessful) {
                        UiState.Success(it.body())
                    } else {
                        UiState.Fail(it.errorBody().toString())
                    }
                }
            Log.i("hey", "getForecast: ${state}")
            state
        }catch (e:Exception){
            UiState.Fail(e.message?:"unknown error")
        }*/

    override suspend fun getAllFavorites() = localSource.getAllFavorites()

    override suspend fun insertForecast(forecast: ForecastModel) = localSource.insertForecast(forecast)
    override suspend fun deleteForecast(forecast: ForecastModel) = localSource.deleteForecast(forecast)

    override suspend fun deleteAllForecasts() = localSource.deleteAllForecasts()

}