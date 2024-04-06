package com.giraffe.weatherforecasapplication.model.repo

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.giraffe.weatherforecasapplication.model.alert.AlertItem
import com.giraffe.weatherforecasapplication.model.forecast.ForecastModel
import com.giraffe.weatherforecasapplication.utils.Constants
import com.giraffe.weatherforecasapplication.utils.UiState
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime


class RepoTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()//to run line by line

    private var  forecastModel: ForecastModel = ForecastModel(1.0,2.0,"egypt",0.0,null, listOf(), listOf(),true)
    private var forecastList:MutableList<ForecastModel> = mutableListOf()
    private var alertsList:MutableList<AlertItem> = mutableListOf()
    private val startLocalDateTime = LocalDateTime.now()
    private val endLocalDateTime = LocalDateTime.now().plusDays(1)

    init {
        for (i in 1..15){
            forecastList.add(ForecastModel(i*1.0,i*2.0,"palestine",0.0,null, listOf(), listOf()))
            alertsList.add(AlertItem(startLocalDateTime,"alarm","egypt",15.0,15.0,endLocalDateTime))
        }
    }


    private lateinit var remoteSource: FakeRemoteSource
    private lateinit var localSource: FakeLocalSource
    private lateinit var repo: Repo



    @Before
    fun setUp() {
        remoteSource = FakeRemoteSource(forecastModel)
        localSource = FakeLocalSource(forecastList,alertsList,forecastModel,
            Constants.Languages.ARABIC,
            Constants.TempUnits.CELSIUS,
            Constants.WindSpeedUnits.METRE,
            true,)
        repo = Repo.getInstance(remoteSource,localSource)
    }

    @Test
    //sub_input_output
    fun getForecast_forecast() = runBlocking {
        //When: call getForecast with true
        repo.getForecast(0.0,0.0,true).collect{
            //Then: get the forecast of egypt
            it as UiState.Success
            assertThat(it.data!!.timezone).isEqualTo("egypt")
        }
    }

    @Test
    fun getAllFavorites_15Forecasts()  = runBlocking {
        //When: call getAllFavorites
        repo.getFavorites().collect{
            //Then: get the 15 forecasts from local
            it as UiState.Success
            assertThat(it.data.size).isEqualTo(16)
        }
    }

    @Test
    fun insertForecast_newForecast_16Forecasts() = runBlocking{
        //When: call insertForecast with the new forecast
        repo.insertFavorite(forecastModel).collect{}
        repo.getFavorites().collect{
            //Then: get the 16 forecasts from local
            it as UiState.Success
            assertThat(it.data.size).isEqualTo(16)
        }
    }

    @Test
    fun deleteCurrent_forecastsWithoutCurrentEgypt() = runBlocking{
        //When: call deleteCurrent
        repo.deleteCurrent()
        repo.getFavorites().collect{
            //Then: there is no forecast to egypt
            it as UiState.Success
            assertThat(it.data).doesNotContain(forecastModel)
        }
    }

    @Test
    fun getCurrent_egyptForecast() = runBlocking {
        //When: call getCurrent
        repo.getCurrent().collect{
            //Then: get the forecast of egypt
            it as UiState.Success
            assertThat(it.data).isEqualTo(forecastModel)
        }
    }

    @Test
    fun deleteForecast_egypt_getAllForecastsWithoutEgypt() = runBlocking {
        //When: call deleteForecast with egypt
        repo.deleteFavorite(forecastModel).collect{}
        repo.getFavorites().collect{
            //Then: there is no forecast to egypt
            it as UiState.Success
            assertThat(it.data).doesNotContain(forecastModel)
        }
    }

    @Test
    fun deleteAllForecasts_getNoForecasts() = runBlocking {
        //When: call deleteAllForecasts
        repo.deleteFavorites()
        repo.getFavorites().collect{
            //Then: there is no forecasts
            it as UiState.Fail
            assertThat(it.error).isEqualTo("empty list")
        }
    }

    @Test
    fun getLanguage_arabic() = runBlocking {
        //When: call getLanguage
        repo.getLanguage().collect{
            //Then: get arabic language
            assertThat(it).isEqualTo(Constants.Languages.ARABIC)
        }
    }

    @Test
    fun getTempUnit_celsiusUnit() = runBlocking {
        //When: call getTempUnit
        repo.getTempUnit().collect{
            //Then: get celsius unit
            assertThat(it).isEqualTo(Constants.TempUnits.KELVIN)
        }
    }

    @Test
    fun getWindSpeedUnit_metreUnit() = runBlocking {
        //When: call getWindSpeedUnit
        repo.getWindSpeedUnit().collect{
            //Then: get metre unit
            assertThat(it).isEqualTo(Constants.WindSpeedUnits.METRE)
        }
    }

    @Test
    fun getNotificationFlag_true() = runBlocking {
        //When: call getNotificationFlag
        repo.getNotificationFlag().collect{
            //Then: get true flag
            assertThat(it).isEqualTo(true)
        }
    }

    @Test
    fun setLanguage_english() = runBlocking {
        //When: call setLanguage with english
        repo.setLanguage(Constants.Languages.ENGLISH)
        repo.getLanguage().collect{
            //Then: get english language
            assertThat(it).isEqualTo(Constants.Languages.ENGLISH)
        }
    }

    @Test
    fun setTempUnit_kelvin() = runBlocking {
        //When: call setTempUnit with kelvin
        repo.setTempUnit(Constants.TempUnits.KELVIN)
        repo.getTempUnit().collect{
            //Then: get kelvin unit
            assertThat(it).isEqualTo(Constants.TempUnits.KELVIN)
        }
    }

    @Test
    fun setWindSpeedUnit_miles() = runBlocking {
        //When: call setWindSpeedUnit with miles
        repo.setWindSpeedUnit(Constants.WindSpeedUnits.MILES)
        repo.getWindSpeedUnit().collect{
            //Then: get miles unit
            assertThat(it).isEqualTo(Constants.WindSpeedUnits.MILES)
        }
    }

    @Test
    fun setNotificationFlag_false() = runBlocking {
        //When: call setNotificationFlag with false
        repo.setNotificationFlag(false)
        repo.getNotificationFlag().collect{
            //Then: get false flag
            assertThat(it).isEqualTo(false)
        }
    }
}