package com.giraffe.weatherforecasapplication.features.favorites.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.giraffe.weatherforecasapplication.model.ForecastModel
import com.giraffe.weatherforecasapplication.model.repo.RepoInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class FavoritesVM(private val repo: RepoInterface): ViewModel() {
    private val _favorites = MutableLiveData<List<ForecastModel>>()
    val favorites: LiveData<List<ForecastModel>> = _favorites

    private val _deletion = MutableLiveData<Int>()
    val deletion: LiveData<Int> = _deletion
    fun getFavorites(){
        viewModelScope.launch {
            _favorites.postValue(repo.getAllFavorites())
        }
    }

    fun deleteFavorite(forecastModel: ForecastModel){
        viewModelScope.launch(Dispatchers.IO) {
            _deletion.postValue(repo.deleteForecast(forecastModel))
        }
    }
}