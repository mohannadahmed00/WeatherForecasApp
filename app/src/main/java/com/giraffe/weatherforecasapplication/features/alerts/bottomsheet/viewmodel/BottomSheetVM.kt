package com.giraffe.weatherforecasapplication.features.alerts.bottomsheet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.giraffe.weatherforecasapplication.model.alert.AlertItem
import com.giraffe.weatherforecasapplication.model.forecast.ForecastModel
import com.giraffe.weatherforecasapplication.model.repo.RepoInterface
import com.giraffe.weatherforecasapplication.utils.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class BottomSheetVM(val repo: RepoInterface):ViewModel() {
    private val _alert =
        MutableStateFlow<UiState<Long>>(UiState.Loading)
    val alert: StateFlow<UiState<Long>> = _alert.asStateFlow()
    fun storeAlarm(alertItem: AlertItem) {
        viewModelScope.launch {
            repo.insertAlert(alertItem)
                .catch {
                    _alert.emit(UiState.Fail(it.message ?: "unknown error"))
                }
                .collect {
                    _alert.emit(it)
                }
        }
    }

    private val _favorites =
        MutableStateFlow<UiState<List<ForecastModel>>>(UiState.Loading)
    val favorites: StateFlow<UiState<List<ForecastModel>>> = _favorites.asStateFlow()
    fun getFavorites() {
        viewModelScope.launch {
            repo.getAllFavorites()
                .catch {
                    _favorites.emit(UiState.Fail(it.message ?: "unknown error"))
                }
                .collect {
                    _favorites.emit(it)
                }
        }
    }
}