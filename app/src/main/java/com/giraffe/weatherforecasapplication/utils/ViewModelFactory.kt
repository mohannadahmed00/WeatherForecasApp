package com.giraffe.weatherforecasapplication.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.giraffe.weatherforecasapplication.SharedVM
import com.giraffe.weatherforecasapplication.features.alerts.bottomsheet.viewmodel.BottomSheetVM
import com.giraffe.weatherforecasapplication.features.alerts.viewmodel.AlertsVM
import com.giraffe.weatherforecasapplication.features.favorites.viewmodel.FavoritesVM
import com.giraffe.weatherforecasapplication.features.home.viewmodel.HomeVM
import com.giraffe.weatherforecasapplication.features.map.viewmodel.MapVM
import com.giraffe.weatherforecasapplication.features.settings.viewmodel.SettingsVM
import com.giraffe.weatherforecasapplication.model.repo.RepoInterface


class ViewModelFactory(private val repo: RepoInterface) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(HomeVM::class.java)) {
            HomeVM(repo) as T
        } else if (modelClass.isAssignableFrom(FavoritesVM::class.java)) {
            FavoritesVM(repo) as T
        } else if (modelClass.isAssignableFrom(AlertsVM::class.java)) {
            AlertsVM(repo) as T
        } else if (modelClass.isAssignableFrom(SettingsVM::class.java)) {
            SettingsVM(repo) as T
        } else if (modelClass.isAssignableFrom(MapVM::class.java)) {
            MapVM(repo) as T
        }else if (modelClass.isAssignableFrom(SharedVM::class.java)) {
            SharedVM(repo) as T
        }else if (modelClass.isAssignableFrom(BottomSheetVM::class.java)) {
            BottomSheetVM(repo) as T
        }
        else {
            throw IllegalArgumentException("can't create ${modelClass.simpleName}")
        }
    }
}