package com.giraffe.weatherforecasapplication.features.alerts.bottomsheet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.giraffe.weatherforecasapplication.model.alert.AlertItem
import com.giraffe.weatherforecasapplication.model.repo.RepoInterface
import com.giraffe.weatherforecasapplication.utils.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BottomSheetVM(val repo: RepoInterface):ViewModel() {
    fun storeAlarm(alertItem: AlertItem) {
        viewModelScope.launch {
            repo.insertAlert(alertItem)
        }
    }
}