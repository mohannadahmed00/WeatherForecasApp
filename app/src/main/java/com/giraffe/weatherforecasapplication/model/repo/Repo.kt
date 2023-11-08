package com.giraffe.weatherforecasapplication.model.repo

import android.util.Log
import com.giraffe.weatherforecasapplication.database.ConcreteLocalSource
import com.giraffe.weatherforecasapplication.model.ForecastModel
import com.giraffe.weatherforecasapplication.network.RemoteSource
import com.giraffe.weatherforecasapplication.utils.UiState
import kotlinx.coroutines.coroutineScope
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.toList
import retrofit2.Response

class Repo private constructor(
    private val remoteSource: RemoteSource,
    private val localSource: ConcreteLocalSource
) : RepoInterface {
    companion object {
        @Volatile
        private var INSTANCE: Repo? = null
        fun getInstance(remoteSource: RemoteSource, localSource: ConcreteLocalSource): Repo {
            return INSTANCE ?: synchronized(this) {
                val instance = Repo(remoteSource, localSource)
                INSTANCE = instance
                instance
            }
        }
    }

    override suspend fun getForecast(lat: Double, lon: Double): Flow<UiState<ForecastModel?>> {
        return flow {
            try {
                val response = remoteSource.getForecast(lat, lon)
                if (response.isSuccessful) {
                    emit(UiState.Success(response.body()))
                } else {
                    emit(UiState.Fail(response.message()))
                }
            } catch (e: Exception) {
                emit(UiState.Fail(e.message ?: "unknown error"))
            }
            //remoteSource.getForecast(lat, lon)
        }.onStart { UiState.Loading }
    }

    override suspend fun getAllFavorites() :Flow<UiState<List<ForecastModel>>>{
        return flow {
            try {
                val response = localSource.getAllFavorites()
                if (response.isNotEmpty()) {
                    emit(UiState.Success(response))
                } else {
                    emit(UiState.Fail("empty list"))
                }
            } catch (e: Exception) {
                emit(UiState.Fail(e.message ?: "unknown error"))
            }
        }.onStart { UiState.Loading }.flowOn(Dispatchers.IO)
    }

    override suspend fun insertForecast(forecast: ForecastModel) =
        localSource.insertForecast(forecast)

    override suspend fun deleteForecast(forecast: ForecastModel):Flow<UiState<Int>> {
        return flow {
            try {
                val response = localSource.deleteForecast(forecast)
                if (response>0) {
                    emit(UiState.Success(response))
                } else {
                    emit(UiState.Fail("deletion failed"))
                }
            } catch (e: Exception) {
                emit(UiState.Fail(e.message ?: "unknown error"))
            }
        }.onStart { UiState.Loading }
    }

    override suspend fun deleteAllForecasts() = localSource.deleteAllForecasts()

}