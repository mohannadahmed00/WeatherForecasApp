package com.giraffe.weatherforecasapplication.features.alerts.bottomsheet.view

import android.Manifest
import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.TimePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.giraffe.weatherforecasapplication.R
import com.giraffe.weatherforecasapplication.SharedVM
import com.giraffe.weatherforecasapplication.database.ConcreteLocalSource
import com.giraffe.weatherforecasapplication.databinding.BottomSheetLayoutBinding
import com.giraffe.weatherforecasapplication.features.alerts.AlarmScheduler
import com.giraffe.weatherforecasapplication.features.alerts.bottomsheet.view.adapters.LocationsAdapter
import com.giraffe.weatherforecasapplication.features.alerts.bottomsheet.view.adapters.TypesAdapter
import com.giraffe.weatherforecasapplication.features.alerts.bottomsheet.viewmodel.BottomSheetVM
import com.giraffe.weatherforecasapplication.model.alert.AlertItem
import com.giraffe.weatherforecasapplication.model.forecast.ForecastModel
import com.giraffe.weatherforecasapplication.model.repo.Repo
import com.giraffe.weatherforecasapplication.network.ApiClient
import com.giraffe.weatherforecasapplication.utils.Constants
import com.giraffe.weatherforecasapplication.utils.ViewModelFactory
import com.giraffe.weatherforecasapplication.utils.getAddress
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

class BottomSheet(private val onBottomSheetDismiss: OnBottomSheetDismiss) :
    BottomSheetDialogFragment(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener, LocationsAdapter.OnLocationClick, TypesAdapter.OnTypeClick {
    companion object {
        const val TAG = "BottomSheetFragment"
        const val REQUEST_CODE = 1001
    }


    private lateinit var binding: BottomSheetLayoutBinding
    private lateinit var viewModel: BottomSheetVM
    private lateinit var factory: ViewModelFactory
    private lateinit var locationsAdapter: LocationsAdapter
    private lateinit var typesAdapter: TypesAdapter
    private lateinit var sharedVM: SharedVM


    //=================alarm work area=================
    private lateinit var alarmScheduler: AlarmScheduler
    private var alertItem: AlertItem? = null


    //=================date & time work area=================
    private var day = 0
    private var month = 0
    private var year = 0
    private var hour = 0
    private var minute = 0

    private var startDay = -1
    private var startMonth = -1
    private var startYear = -1
    private var startHour = -1
    private var startMinute = -1

    private var endDay = -1
    private var endMonth = -1
    private var endYear = -1
    private var endHour = -1
    private var endMinute = -1


    private var startFlag: Boolean = false
    private var endFlag: Boolean = false

    //private var type:String? = null
    private var forecast: ForecastModel? = null
    private var alertType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        factory =
            ViewModelFactory(Repo.getInstance(ApiClient, ConcreteLocalSource(requireContext())))
        viewModel = ViewModelProvider(this, factory)[BottomSheetVM::class.java]
        locationsAdapter = LocationsAdapter(mutableListOf(), this)
        typesAdapter = TypesAdapter(
            mutableListOf(
                Constants.AlertTypes.NOTIFICATION,
                Constants.AlertTypes.ALARM
            ), this
        )
        sharedVM = ViewModelProvider(requireActivity(), factory)[SharedVM::class.java]


        //=================alarm work area=================
        alarmScheduler = AlarmScheduler(requireContext())

        //=================notification work area=================
        if (checkPermissions()) {
            createNotificationChannel()
            createAlertsChannel()
        } else {
            requestPermissions()
        }

        sharedVM.getFavorites()

    }

    private fun observeFavorites() {
        lifecycleScope.launch {
            sharedVM.favorites.collect {
                val list = it.map { f ->
                    LocationItem(f, false)
                }
                locationsAdapter.updateList(list)
            }

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvLocations.adapter = locationsAdapter
        binding.rvTypes.adapter = typesAdapter
        observeFavorites()
        /*binding.cgType.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId== R.id.chip_alarm){
                type = Constants.AlertTypes.ALARM
            }else if (checkedId == R.id.chip_notification){
                type = Constants.AlertTypes.NOTIFICATION
            }
        }*/

        binding.tvStartDateTime.setOnClickListener {
            startFlag = true
            endFlag = false
            getDateTimeCalender()
            DatePickerDialog(requireContext(), this, year, month, day).show()
        }

        binding.tvEndDateTime.setOnClickListener {
            startFlag = false
            endFlag = true
            getDateTimeCalender()
            DatePickerDialog(requireContext(), this, year, month, day).show()
        }

        binding.btnConfirm.setOnClickListener {
            if (validate()) {
                val lat = forecast?.lat ?: 0.0
                val lon = forecast?.lon ?: 0.0
                val locationName =
                    getAddress(requireContext(), lat, lon, forecast?.timezone ?: "null,")
                val startLocalDate =
                    LocalDateTime.of(startYear, startMonth + 1, startDay, startHour, startMinute, 1)
                if (endMinute != -1 && endHour != -1 && endDay != -1 && endMonth != -1 && endYear != -1) {
                    val endLocalDate =
                        LocalDateTime.of(endYear, endMonth + 1, endDay, endHour, endMinute, 1)
                    alertItem = AlertItem(
                        startLocalDate,
                        alertType ?: Constants.AlertTypes.ALARM,
                        locationName,
                        lat,
                        lon,
                        endLocalDate
                    )
                } else {
                    alertItem = AlertItem(
                        startLocalDate,
                        alertType ?: Constants.AlertTypes.ALARM,
                        locationName,
                        lat,
                        lon
                    )
                }
                alertItem?.apply {
                    alarmScheduler.schedule(this)
                    viewModel.storeAlarm(this)
                    onBottomSheetDismiss.onBottomSheetDismiss(this)
                }
                dismiss()
            }
        }
    }

    private fun getDateTimeCalender() {
        val cal = Calendar.getInstance()
        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONTH)
        year = cal.get(Calendar.YEAR)
        hour = cal.get(Calendar.HOUR)
        minute = cal.get(Calendar.MINUTE)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        if (startFlag) {
            startDay = dayOfMonth
            startMonth = month
            startYear = year
        } else if (endFlag) {
            endDay = dayOfMonth
            endMonth = month
            endYear = year
        }
        getDateTimeCalender()
        TimePickerDialog(requireContext(), this, hour, minute, true).show()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        if (startFlag) {
            startHour = hourOfDay
            startMinute = minute
            binding.tvStartDateTime.text = formatLocalDateTime(
                LocalDateTime.of(
                    startYear,
                    startMonth + 1,
                    startDay,
                    startHour,
                    startMinute,
                    10
                )
            )
        } else if (endFlag) {
            endHour = hourOfDay
            endMinute = minute
            binding.tvEndDateTime.text = formatLocalDateTime(
                LocalDateTime.of(
                    endYear,
                    endMonth + 1,
                    endDay,
                    endHour,
                    endMinute,
                    10
                )
            )
        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            Constants.NOTIFICATION_CHANNEL_ID,
            Constants.NOTIFICATION_CHANNEL_ID,
            NotificationManager.IMPORTANCE_HIGH
        )
        val notificationManager =
            requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun createAlertsChannel() {
        val channel = NotificationChannel(
            Constants.ALERT_CHANNEL_ID,
            Constants.ALERT_CHANNEL_ID,
            NotificationManager.IMPORTANCE_HIGH
        )
        val notificationManager =
            requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun checkPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        requestPermissions(
            arrayOf(
                Manifest.permission.POST_NOTIFICATIONS,
            ),
            REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                createNotificationChannel()
                createAlertsChannel()
            }
        }
    }

    data class LocationItem(val forecast: ForecastModel, var isSelected: Boolean)

    override fun onLocationClick(forecast: ForecastModel) {
        this.forecast = forecast
    }

    private fun formatLocalDateTime(localDateTime: LocalDateTime): String {
        val formatter = DateTimeFormatter.ofPattern("HH:mm EEEE, MMM d, yyyy", Locale.US)
        return localDateTime.format(formatter)
    }


    private fun validate(): Boolean {
        if (startMinute == -1 || startHour == -1 || startDay == -1 || startMonth == -1 || startYear == -1) return false
        if (forecast == null) return false
        if (alertType == null) return false

        return true
    }

    interface OnBottomSheetDismiss {
        fun onBottomSheetDismiss(alertItem: AlertItem)
    }

    override fun onTypeClick(alertType: String) {
        Log.d(TAG, "onTypeClick: $alertType")
        this.alertType = alertType
    }
}