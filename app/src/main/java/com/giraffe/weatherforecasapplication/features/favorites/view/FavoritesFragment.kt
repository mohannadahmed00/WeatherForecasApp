package com.giraffe.weatherforecasapplication.features.favorites.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.giraffe.weatherforecasapplication.OnDrawerClick
import com.giraffe.weatherforecasapplication.R
import com.giraffe.weatherforecasapplication.SharedVM
import com.giraffe.weatherforecasapplication.database.ConcreteLocalSource
import com.giraffe.weatherforecasapplication.databinding.FragmentFavoritesBinding
import com.giraffe.weatherforecasapplication.features.favorites.view.adapters.FavoritesAdapter
import com.giraffe.weatherforecasapplication.features.favorites.viewmodel.FavoritesVM
import com.giraffe.weatherforecasapplication.model.ForecastModel
import com.giraffe.weatherforecasapplication.model.repo.Repo
import com.giraffe.weatherforecasapplication.network.ApiClient
import com.giraffe.weatherforecasapplication.utils.UiState
import com.giraffe.weatherforecasapplication.utils.ViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class FavoritesFragment : Fragment(), FavoritesAdapter.OnDeleteClick,
    FavoritesAdapter.OnSelectClick {
    companion object {
        const val TAG = "FavoritesFragment"
    }

    private lateinit var binding: FragmentFavoritesBinding
    private lateinit var viewModel: FavoritesVM
    private lateinit var factory: ViewModelFactory
    private lateinit var onDrawerClick: OnDrawerClick
    private lateinit var adapter: FavoritesAdapter
    private lateinit var sharedVM: SharedVM
    private var tempForecast: ForecastModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        factory =
            ViewModelFactory(Repo.getInstance(ApiClient, ConcreteLocalSource(requireContext())))
        viewModel = ViewModelProvider(this, factory)[FavoritesVM::class.java]
        sharedVM = ViewModelProvider(requireActivity(), factory)[SharedVM::class.java]
        adapter = FavoritesAdapter(mutableListOf(), this, this)
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
        onDrawerClick = activity as OnDrawerClick
        binding.ivMore.setOnClickListener {
            onDrawerClick.onClick()
        }
        binding.rvFavorites.adapter = adapter
        lifecycleScope.launch {
            viewModel.favorites.collect {
                when (it) {
                    is UiState.Fail -> {
                        Log.e(TAG, "fail: ${it.error}")
                    }

                    UiState.Loading -> {
                        Log.d(TAG, "loading: ")
                    }

                    is UiState.Success -> {
                        adapter.updateList(it.data)
                    }
                }
            }
        }
        lifecycleScope.launch {
            viewModel.insert.collect{
                when(it){
                    is UiState.Fail -> {

                    }
                    UiState.Loading -> {

                    }
                    is UiState.Success -> {
                        viewModel.getFavorites()
                    }
                }
            }
        }
        lifecycleScope.launch {
            viewModel.deletion.collect {
                when (it) {
                    is UiState.Fail -> {
                        Log.e(TAG, "fail: ${it.error}")
                    }

                    UiState.Loading -> {
                        Log.d(TAG, "loading: ")
                    }

                    is UiState.Success -> {
                        Log.d(TAG, "deletion success: ")
                        viewModel.getFavorites()
                        Snackbar.make(
                            requireView(),
                            "the location has been deleted",
                            Snackbar.LENGTH_LONG
                        )
                            .setAction("Undo") {
                                tempForecast?.let { temp ->
                                    viewModel.insertForecast(temp)
                                }
                            }
                            .show()

                    }
                }
            }
        }

    }

    override fun onDeleteClick(forecast: ForecastModel) {
        tempForecast = forecast
        viewModel.deleteFavorite(forecast)
    }

    override fun onSelectClick(forecast: ForecastModel) {
        sharedVM.selectForecast(forecast)
        findNavController().navigate(R.id.homeFragment)
    }
}