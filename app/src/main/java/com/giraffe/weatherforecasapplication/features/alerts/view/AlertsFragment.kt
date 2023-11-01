package com.giraffe.weatherforecasapplication.features.alerts.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.giraffe.weatherforecasapplication.databinding.FragmentAlertsBinding
import com.giraffe.weatherforecasapplication.features.alerts.viewmodel.AlertsVM
import com.giraffe.weatherforecasapplication.features.home.viewmodel.HomeVM

class AlertsFragment : Fragment() {
    companion object {
        const val TAG = "AlertsFragment"
    }

    private lateinit var binding: FragmentAlertsBinding
    private lateinit var viewModel: AlertsVM
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[AlertsVM::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAlertsBinding.inflate(inflater, container, false)
        return binding.root
    }
}