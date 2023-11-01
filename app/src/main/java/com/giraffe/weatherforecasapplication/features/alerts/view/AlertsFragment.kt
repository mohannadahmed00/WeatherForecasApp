package com.giraffe.weatherforecasapplication.features.alerts.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.giraffe.weatherforecasapplication.databinding.FragmentAlertsBinding

class AlertsFragment : Fragment() {
    companion object {
        const val TAG = "AlertsFragment"
    }

    private lateinit var binding: FragmentAlertsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAlertsBinding.inflate(inflater, container, false)
        return binding.root
    }
}