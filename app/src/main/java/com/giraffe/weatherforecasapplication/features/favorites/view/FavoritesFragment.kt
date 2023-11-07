package com.giraffe.weatherforecasapplication.features.favorites.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.giraffe.weatherforecasapplication.database.ConcreteLocalSource
import com.giraffe.weatherforecasapplication.databinding.FragmentFavoritesBinding
import com.giraffe.weatherforecasapplication.features.favorites.view.adapters.FavoritesAdapter
import com.giraffe.weatherforecasapplication.features.favorites.viewmodel.FavoritesVM
import com.giraffe.weatherforecasapplication.model.ForecastModel
import com.giraffe.weatherforecasapplication.model.repo.Repo
import com.giraffe.weatherforecasapplication.network.ApiClient
import com.giraffe.weatherforecasapplication.utils.ViewModelFactory

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
        viewModel.favorites.observe(viewLifecycleOwner){
            adapter.updateList(it)
        }

        viewModel.deletion.observe(viewLifecycleOwner){
            if (it>0){
                viewModel.getFavorites()
            }
        }

    }

    override fun onclick(forecast: ForecastModel) {
        viewModel.deleteFavorite(forecast)
    }
}