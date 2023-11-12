package com.giraffe.weatherforecasapplication.model.repo

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.giraffe.weatherforecasapplication.model.forecast.ForecastModel
import com.giraffe.weatherforecasapplication.utils.UiState
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RepoTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()//to run line by line

    private var  forecastModel: ForecastModel
    private var list:MutableList<ForecastModel>
    init {
        forecastModel = ForecastModel(1.0,2.0,"egypt",0.0,null, listOf(), listOf())
        list = mutableListOf()
        for (i in 1..15){
            list.add(ForecastModel(i*1.0,i*2.0,"palestine",0.0,null, listOf(), listOf()))
        }
    }


    private lateinit var remoteSource: FakeRemoteSource
    private lateinit var localSource: FakeLocalSource
    private lateinit var repo: Repo



    @Before
    fun setUp() {
        remoteSource = FakeRemoteSource(forecastModel)
        localSource = FakeLocalSource(list,forecastModel)
        repo = Repo.getInstance(remoteSource,localSource)
    }

    @Test
    //sub_input_output
    fun getForecast_withFalse_forecast() = runBlocking{
        /*repo.insertForecast(forecastModel).collect{

        }
        repo.getAllFavorites().collect{
            it as UiState.Success
            println("hello: ${it.data}")
            assertThat(it.data).contains(forecastModel)
        }*/

        val oldSize = list.size
        //When: call getForecast with true
        repo.getForecast(0.0,0.0,true).collect{

        }
        /*repo.insertForecast(forecastModel).collect{

        }*/
        repo.getAllFavorites().collect{
            it as UiState.Success
            assertThat(it.data.size).isGreaterThan(oldSize)
        }

    }

    @Test
    fun getAllFavorites() {
    }

    @Test
    fun insertForecast() {
    }

    @Test
    fun deleteCurrent() {
    }

    @Test
    fun getCurrent() {
    }

    @Test
    fun deleteForecast() {
    }

    @Test
    fun deleteAllForecasts() {
    }

    @Test
    fun getLanguage() {
    }

    @Test
    fun getTempUnit() {
    }

    @Test
    fun getWindSpeedUnit() {
    }

    @Test
    fun getNotificationFlag() {
    }

    @Test
    fun setLanguage() {
    }

    @Test
    fun setTempUnit() {
    }

    @Test
    fun setWindSpeedUnit() {
    }

    @Test
    fun setNotificationFlag() {
    }
}