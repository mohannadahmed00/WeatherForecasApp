package com.giraffe.weatherforecasapplication.features.favorites.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.giraffe.weatherforecasapplication.databinding.FragmentFavoritesBinding
import com.giraffe.weatherforecasapplication.features.alerts.viewmodel.AlertsVM
import com.giraffe.weatherforecasapplication.features.favorites.viewmodel.FavoritesVM

class FavoritesFragment : Fragment() {
    companion object {
        const val TAG = "FavoritesFragment"
    }

    private lateinit var binding: FragmentFavoritesBinding
    private lateinit var viewModel: FavoritesVM
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[FavoritesVM::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }
}