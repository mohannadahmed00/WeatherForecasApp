package com.giraffe.weatherforecasapplication.utils


sealed class UiState<out T>{
    class Success<out T>(val data: T):UiState<T>()
    class Fail(val error: Throwable):UiState<Nothing>()
    object Loading : UiState<Nothing>()
}
