package com.giraffe.weatherforecasapplication.model.viewmodels

import com.giraffe.weatherforecasapplication.model.alert.AlertItem
import com.giraffe.weatherforecasapplication.model.forecast.ForecastModel
import com.giraffe.weatherforecasapplication.model.repo.FakeLocalSource
import com.giraffe.weatherforecasapplication.model.repo.FakeRemoteSource
import com.giraffe.weatherforecasapplication.model.repo.RepoInterface
import com.giraffe.weatherforecasapplication.utils.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeRepo(
    private val fakeRemoteSource: FakeRemoteSource,
    private val fakeLocalSource: FakeLocalSource,
) : RepoInterface {
    override suspend fun getForecast(
        lat: Double,
        lon: Double,
        isCurrent: Boolean
    ): Flow<UiState<ForecastModel?>> {
        return flow { UiState.Success(fakeRemoteSource.getForecast(lat,lon,fakeLocalSource.getLanguage()).body()) }
    }

    override suspend fun getFavorites(): Flow<UiState<List<ForecastModel>>> {
        return flow { UiState.Success(fakeLocalSource.getFavorites()) }
    }

    override suspend fun insertFavorite(forecast: ForecastModel): Flow<UiState<Long>> {
        return flow { UiState.Success(fakeLocalSource.insertFavorite(forecast)) }
    }

    override suspend fun deleteFavorite(forecast: ForecastModel): Flow<UiState<Int>> {
        return flow { UiState.Success(fakeLocalSource.deleteFavorite(forecast)) }
    }

    override suspend fun deleteFavorites() {
        fakeLocalSource.deleteFavorites()
    }

    override suspend fun deleteCurrent() {
        fakeLocalSource.deleteCurrent()
    }

    override suspend fun getCurrent(): Flow<UiState<ForecastModel?>> {
        return flow { UiState.Success(fakeLocalSource.getCurrent()) }
    }

    override suspend fun getAlerts(): Flow<UiState<List<AlertItem>>> {
        return flow { UiState.Success(fakeLocalSource.getAlerts()) }
    }

    override suspend fun insertAlert(alertItem: AlertItem): Flow<UiState<Long>> {
        return flow { UiState.Success(fakeLocalSource.insertAlert(alertItem)) }
    }

    override suspend fun deleteAlert(alertId: Int) {
        fakeLocalSource.deleteAlert(alertId)
    }

    override suspend fun getLanguage(): Flow<String> {
        return flow { fakeLocalSource.getLanguage() }
    }

    override suspend fun getTempUnit(): Flow<String> {
        return flow { fakeLocalSource.getTempUnit() }
    }

    override suspend fun getWindSpeedUnit(): Flow<String> {
        return flow { fakeLocalSource.getWindSpeedUnit() }
    }

    override suspend fun getNotificationFlag(): Flow<Boolean> {
        return flow { fakeLocalSource.getNotificationFlag() }
    }

    override suspend fun setLanguage(lang: String) {
        fakeLocalSource.setLanguage(lang)
    }

    override suspend fun setTempUnit(unit: String) {
        fakeLocalSource.setTempUnit(unit)
    }

    override suspend fun setWindSpeedUnit(unit: String) {
        fakeLocalSource.setWindSpeedUnit(unit)
    }

    override suspend fun setNotificationFlag(notifyFlag: Boolean) {
        fakeLocalSource.setNotificationFlag(notifyFlag)
    }
}