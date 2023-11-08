package com.giraffe.weatherforecasapplication.features.favorites.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.giraffe.weatherforecasapplication.model.ForecastModel
import com.giraffe.weatherforecasapplication.model.repo.RepoInterface
import com.giraffe.weatherforecasapplication.utils.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import retrofit2.Response

class FavoritesVM(private val repo: RepoInterface) : ViewModel() {
    /*private val _favorites = MutableLiveData<List<ForecastModel>>()
    val favorites: LiveData<List<ForecastModel>> = _favorites*/

    private val _favorites = MutableStateFlow<UiState<List<ForecastModel>>>(UiState.Loading)
    val favorites: StateFlow<UiState<List<ForecastModel>>> = _favorites

    private val _deletion = MutableStateFlow<UiState<Int>>(UiState.Loading)
    val deletion: StateFlow<UiState<Int>> = _deletion
    fun getFavorites() {
        _favorites.value = UiState.Loading
        viewModelScope.launch {
            repo.getAllFavorites()
                .catch { _favorites.emit(UiState.Fail(it.message ?: "unknown error")) }
                .collect {
                    _favorites.emit(it)
                }
        }
    }

    fun deleteFavorite(forecastModel: ForecastModel) {
        _deletion.value = UiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteForecast(forecastModel)
                .catch { _deletion.emit(UiState.Fail(it.message?:"unknown error")) }
                .collect{
                    _deletion.emit(it)
                }
        }
    }
}