package com.giraffe.weatherforecasapplication.model.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.giraffe.weatherforecasapplication.SharedVM
import com.giraffe.weatherforecasapplication.model.alert.AlertItem
import com.giraffe.weatherforecasapplication.model.forecast.ForecastModel
import com.giraffe.weatherforecasapplication.model.repo.FakeLocalSource
import com.giraffe.weatherforecasapplication.model.repo.FakeRemoteSource
import com.giraffe.weatherforecasapplication.utils.Constants
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import java.time.LocalDateTime


@RunWith(AndroidJUnit4::class)
class SharedVMTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private var forecastModel: ForecastModel =
        ForecastModel(1.0, 2.0, "egypt", 0.0, null, listOf(), listOf(), true)
    private var forecastList: MutableList<ForecastModel> = mutableListOf()
    private var alertsList: MutableList<AlertItem> = mutableListOf()
    private val startLocalDateTime = LocalDateTime.now()
    private val endLocalDateTime = LocalDateTime.now().plusDays(1)

    init {
        for (i in 1..15) {
            forecastList.add(
                ForecastModel(
                    i * 1.0,
                    i * 2.0,
                    "palestine",
                    0.0,
                    null,
                    listOf(),
                    listOf()
                )
            )
            alertsList.add(
                AlertItem(
                    startLocalDateTime,
                    "alarm",
                    "egypt",
                    15.0,
                    15.0,
                    endLocalDateTime
                )
            )
        }
    }


    private lateinit var remoteSource: FakeRemoteSource
    private lateinit var localSource: FakeLocalSource
    private lateinit var repo: FakeRepo
    private lateinit var sharedVM: SharedVM


    @Before
    fun setUp() {
        remoteSource = FakeRemoteSource(forecastModel)
        localSource = FakeLocalSource(
            forecastList, alertsList, forecastModel,
            Constants.Languages.ARABIC,
            Constants.TempUnits.KELVIN,
            Constants.WindSpeedUnits.MILES,
            true,
        )
        repo = FakeRepo(remoteSource, localSource)
        sharedVM = SharedVM(repo)
    }
}