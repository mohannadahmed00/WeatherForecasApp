package com.giraffe.weatherforecasapplication.features.settings.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.giraffe.weatherforecasapplication.databinding.FragmentSettingsBinding
import com.giraffe.weatherforecasapplication.features.favorites.viewmodel.FavoritesVM
import com.giraffe.weatherforecasapplication.features.settings.viewmodel.SettingsVM

class SettingsFragment : Fragment() {
    companion object {
        const val TAG = "SettingsFragment"
    }

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var viewModel: SettingsVM
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[SettingsVM::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }
}