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

    override suspend fun getForecast(
        lat: Double,
        lon: Double,
        isCurrent: Boolean
    ): Flow<UiState<ForecastModel?>> {
        return flow {
            try {
                val response = remoteSource.getForecast(lat, lon)
                if (response.isSuccessful) {
                    val f = response.body()
                    f?.apply {
                        this.isCurrent = isCurrent
                        if (isCurrent) {
                            deleteCurrent()
                            insertForecast(f)
                        }
                    }
                    emit(UiState.Success(f))
                } else {
                    emit(UiState.Fail(response.message()))
                }
            } catch (e: Exception) {
                emit(UiState.Fail(e.message ?: "unknown error"))
            }
            //remoteSource.getForecast(lat, lon)
        }.onStart { UiState.Loading }
    }

    override suspend fun getAllFavorites(): Flow<UiState<List<ForecastModel>>> {
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

    override suspend fun insertForecast(forecast: ForecastModel): Flow<UiState<Long>> {
        return flow {
            try {
                val response = localSource.insertForecast(forecast)
                if (response>0) {
                    emit(UiState.Success(response))
                } else {
                    emit(UiState.Fail("fail insertion"))
                }
            } catch (e: Exception) {
                emit(UiState.Fail(e.message ?: "unknown error"))
            }
        }.onStart { UiState.Loading }.flowOn(Dispatchers.IO)

    }

    override suspend fun deleteCurrent() = localSource.deleteCurrent()
    override suspend fun getCurrent(): Flow<UiState<ForecastModel?>> {
        return flow {
            try {
                val response = localSource.getCurrent()
                if (response != null) {
                    emit(UiState.Success(response))
                } else {
                    emit(UiState.Fail("no stored current"))
                }
            } catch (e: Exception) {
                emit(UiState.Fail(e.message ?: "unknown error"))
            }
        }.onStart { UiState.Loading }.flowOn(Dispatchers.IO)
        //localSource.getCurrent()
    }

    override suspend fun deleteForecast(forecast: ForecastModel): Flow<UiState<Int>> {
        return flow {
            try {
                val response = localSource.deleteForecast(forecast)
                if (response > 0) {
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
    override suspend fun getLanguage() = flow { emit(localSource.getLanguage()) }


    override suspend fun getTempUnit() = flow { emit(localSource.getTempUnit()) }

    override suspend fun getWindSpeedUnit() = flow { emit(localSource.getWindSpeedUnit()) }

    override suspend fun getNotificationFlag() = flow { emit(localSource.getNotificationFlag()) }

    override suspend fun setLanguage(lang: String) = localSource.setLanguage(lang)

    override suspend fun setTempUnit(unit: String) = localSource.setTempUnit(unit)

    override suspend fun setWindSpeedUnit(unit: String) = localSource.setWindSpeedUnit(unit)

    override suspend fun setNotificationFlag(notifyFlag: Boolean) =
        localSource.setNotificationFlag(notifyFlag)

}