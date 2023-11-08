package com.giraffe.weatherforecasapplication.features.favorites.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.giraffe.weatherforecasapplication.database.ConcreteLocalSource
import com.giraffe.weatherforecasapplication.databinding.FragmentFavoritesBinding
import com.giraffe.weatherforecasapplication.features.favorites.view.adapters.FavoritesAdapter
import com.giraffe.weatherforecasapplication.features.favorites.viewmodel.FavoritesVM
import com.giraffe.weatherforecasapplication.features.home.view.HomeFragment
import com.giraffe.weatherforecasapplication.model.ForecastModel
import com.giraffe.weatherforecasapplication.model.repo.Repo
import com.giraffe.weatherforecasapplication.network.ApiClient
import com.giraffe.weatherforecasapplication.utils.UiState
import com.giraffe.weatherforecasapplication.utils.ViewModelFactory
import kotlinx.coroutines.launch

class FavoritesFragment : Fragment(),FavoritesAdapter.OnDeleteClick {
    companion object {
        const val TAG = "FavoritesFragment"
    }

    private lateinit var binding: FragmentFavoritesBinding
    private lateinit var viewModel: FavoritesVM
    private lateinit var factory: ViewModelFactory

    private lateinit var adapter: FavoritesAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        factory = ViewModelFactory(Repo.getInstance(ApiClient, ConcreteLocalSource(requireContext())))
        viewModel = ViewModelProvider(this, factory)[FavoritesVM::class.java]
        adapter = FavoritesAdapter(mutableListOf(),this)
        viewModel.getFavorites()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvFavorites.adapter = adapter
        lifecycleScope.launch {
            viewModel.favorites.collect {
                when (it) {
                    is UiState.Fail -> {
                        Log.e(HomeFragment.TAG, "fail: ${it.error}")
                    }

                    UiState.Loading -> {
                        Log.d(HomeFragment.TAG, "loading: ")
                    }

                    is UiState.Success -> {
                        adapter.updateList(it.data)
                    }
                }
            }
        }
        lifecycleScope.launch {
            viewModel.deletion.collect {
                when (it) {
                    is UiState.Fail -> {
                        Log.e(HomeFragment.TAG, "fail: ${it.error}")
                    }

                    UiState.Loading -> {
                        Log.d(HomeFragment.TAG, "loading: ")
                    }

                    is UiState.Success -> {
                        Log.d(HomeFragment.TAG, "deletion success: ")
                        viewModel.getFavorites()
                    }
                }
            }
        }

    }

    override fun onclick(forecast: ForecastModel) {
        viewModel.deleteFavorite(forecast)
    }
}