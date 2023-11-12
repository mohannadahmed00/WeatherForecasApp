package com.giraffe.weatherforecasapplication.features.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.giraffe.weatherforecasapplication.model.repo.RepoInterface
import com.giraffe.weatherforecasapplication.utils.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsVM(private val repo: RepoInterface) : ViewModel() {
    private val _language = MutableStateFlow(Constants.Languages.ENGLISH)
    val language: StateFlow<String> = _language.asStateFlow()

    private val _tempUnit = MutableStateFlow(Constants.TempUnits.CELSIUS)
    val tempUnit: StateFlow<String> = _tempUnit.asStateFlow()

    private val _windSpeedUnit = MutableStateFlow(Constants.WindSpeedUnits.METRE)
    val windSpeedUnit: StateFlow<String> = _windSpeedUnit.asStateFlow()

    private val _notifyFlag = MutableStateFlow(false)
    val notifyFlag: StateFlow<Boolean> = _notifyFlag.asStateFlow()
    fun getLanguage() {
        viewModelScope.launch {
            repo.getLanguage().collect {
                _language.value = it
            }
        }
    }

    fun getTempUnit() {
        viewModelScope.launch {
            repo.getTempUnit().collect {
                _tempUnit.value = it
            }
        }
    }

    fun getWindSpeedUnit() {
        viewModelScope.launch {
            repo.getWindSpeedUnit().collect {
                _windSpeedUnit.value = it
            }
        }
    }

    fun getNotificationFlag() {
        viewModelScope.launch {
            repo.getNotificationFlag().collect {
                _notifyFlag.value = it
            }
        }
    }

    fun setLanguage(lang: String) {
        viewModelScope.launch {
            repo.setLanguage(lang)
        }
    }

    fun setTempUnit(unit: String) {
        viewModelScope.launch {
            repo.setTempUnit(unit)
        }
    }

    fun setWindSpeedUnit(unit: String) {
        viewModelScope.launch {
            repo.setWindSpeedUnit(unit)
        }
    }

    fun setNotificationFlag(notifyFlag: Boolean) {
        viewModelScope.launch {
            repo.setNotificationFlag(notifyFlag)
        }
    }

}