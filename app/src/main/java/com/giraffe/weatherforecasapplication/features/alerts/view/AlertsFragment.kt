package com.giraffe.weatherforecasapplication.features.alerts.view

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.giraffe.weatherforecasapplication.database.ConcreteLocalSource
import com.giraffe.weatherforecasapplication.databinding.FragmentAlertsBinding
import com.giraffe.weatherforecasapplication.features.alerts.AlarmReceiver
import com.giraffe.weatherforecasapplication.features.alerts.bottomsheet.view.BottomSheet
import com.giraffe.weatherforecasapplication.features.alerts.view.adapters.AlertsAdapter
import com.giraffe.weatherforecasapplication.features.alerts.viewmodel.AlertsVM
import com.giraffe.weatherforecasapplication.model.alert.AlertItem
import com.giraffe.weatherforecasapplication.model.repo.Repo
import com.giraffe.weatherforecasapplication.network.ApiClient
import com.giraffe.weatherforecasapplication.utils.UiState
import com.giraffe.weatherforecasapplication.utils.ViewModelFactory
import kotlinx.coroutines.launch

class AlertsFragment : Fragment(),BottomSheet.OnBottomSheetDismiss {
    companion object {
        const val TAG = "AlertsFragment"
    }

    private lateinit var binding: FragmentAlertsBinding
    private lateinit var viewModel: AlertsVM
    private lateinit var factory: ViewModelFactory
    private lateinit var adapter: AlertsAdapter
    private lateinit var itemTouchHelper:ItemTouchHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "onCreate: ")
        super.onCreate(savedInstanceState)
        factory = ViewModelFactory(Repo.getInstance(ApiClient, ConcreteLocalSource(requireContext())))
        viewModel = ViewModelProvider(this,factory)[AlertsVM::class.java]
        adapter = AlertsAdapter(mutableListOf())

        val simpleItemTouchCallback: ItemTouchHelper.SimpleCallback = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                val position = viewHolder.adapterPosition
                cancelAlarm(adapter.list[position].id)
                viewModel.deleteAlert(adapter.list[position].id)
                adapter.removeItem(position)

            }
        }
        itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
    }

    

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.i(TAG, "onCreateView: ")
        binding = FragmentAlertsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.i(TAG, "onViewCreated: ")
        super.onViewCreated(view, savedInstanceState)
        binding.rvAlerts.adapter = adapter
        itemTouchHelper.attachToRecyclerView(binding.rvAlerts)
        observeAlerts()
        viewModel.getAlerts()
        binding.btnAddAlert.setOnClickListener {
            val bottomSheet = BottomSheet(this)
            bottomSheet.show(childFragmentManager,BottomSheet.TAG)
        }
    }

    override fun onStart() {
        super.onStart()
        Log.i(TAG, "onStart: ")
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "onResume: ")
    }

    override fun onPause() {
        super.onPause()
        Log.i(TAG, "onPause: ")
    }

    override fun onStop() {
        super.onStop()
        Log.i(TAG, "onStop: ")
    }

    private fun observeAlerts() {
        lifecycleScope.launch {
            viewModel.alerts.collect{
                when(it){
                    is UiState.Fail -> {
                        Log.e(TAG, "observeAlerts: ${it.error}")
                    }
                    UiState.Loading -> {
                        Log.d(TAG, "observeAlerts: loading...")
                    }
                    is UiState.Success -> {
                        Log.i(TAG, "observeAlerts: ${it.data.size}")
                        adapter.updateList(it.data)
                    }
                }
            }
        }
    }

    override fun onBottomSheetDismiss(alertItem: AlertItem) {
        val list = mutableListOf<AlertItem>()
        list.addAll(adapter.list)
        list.add(alertItem)
        adapter.updateList(list)
    }


    private fun cancelAlarm(alertId:Int){
        val alarmManager = requireContext().getSystemService(AlarmManager::class.java)
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                alertId,
                Intent(context, AlarmReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }


}