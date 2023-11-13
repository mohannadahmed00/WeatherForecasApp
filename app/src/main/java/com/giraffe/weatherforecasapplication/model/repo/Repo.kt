package com.giraffe.weatherforecasapplication.model.repo

import android.util.Log
import com.giraffe.weatherforecasapplication.database.LocalSource
import com.giraffe.weatherforecasapplication.model.alert.AlertItem
import com.giraffe.weatherforecasapplication.model.forecast.ForecastModel
import com.giraffe.weatherforecasapplication.network.RemoteSource
import com.giraffe.weatherforecasapplication.utils.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart

class Repo private constructor(
    private val remoteSource: RemoteSource,
    private val localSource: LocalSource
) : RepoInterface {
    companion object {
        @Volatile
        private var INSTANCE: Repo? = null
        fun getInstance(remoteSource: RemoteSource, localSource: LocalSource): Repo {
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
                Log.i("messi", "getForecast: ")
                if (response.isSuccessful) {
                    Log.i("messi", "getForecast: success")
                    emit(UiState.Success(response.body()))
                } else {
                    Log.i("messi", "getForecast: fail ${response.message()}")
                    emit(UiState.Fail(response.message()))
                }
            } catch (e: Exception) {
                Log.i("messi", "getForecast: fail ${e.message}")

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
        Log.i("messi", "insertForecast: ")

        return flow {
            try {
                val response = localSource.insertForecast(forecast)
                if (response>0) {
                    Log.i("messi", "insertForecast: success")
                    emit(UiState.Success(response))
                } else {
                    Log.i("messi", "insertForecast: fail")

                    emit(UiState.Fail("fail insertion"))
                }
            } catch (e: Exception) {
                Log.i("messi", "insertForecast: fail ${e.message}")

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
    }

    override suspend fun getAllAlerts(): Flow<UiState<List<AlertItem>>> {
        return flow {
            try {
                val response = localSource.getAllAlerts()
                emit(UiState.Success(response))
            } catch (e: Exception) {
                emit(UiState.Fail(e.message ?: "unknown error"))
            }
        }.onStart { UiState.Loading }.flowOn(Dispatchers.IO)
    }

    override suspend fun insertAlert(alertItem: AlertItem): Flow<UiState<Long>> {
        return flow {
            try {
                val response = localSource.insertAlert(alertItem)
                emit(UiState.Success(response))
            } catch (e: Exception) {
                emit(UiState.Fail(e.message ?: "unknown error"))
            }
        }.onStart { UiState.Loading }.flowOn(Dispatchers.IO)
    }

    override suspend fun deleteAlert(alertId: Int) {
        localSource.deleteAlert(alertId)
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