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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.work.WorkManager
import com.giraffe.weatherforecasapplication.OnDrawerClick
import com.giraffe.weatherforecasapplication.R
import com.giraffe.weatherforecasapplication.SharedVM
import com.giraffe.weatherforecasapplication.database.ConcreteLocalSource
import com.giraffe.weatherforecasapplication.databinding.FragmentFavoritesBinding
import com.giraffe.weatherforecasapplication.features.favorites.view.adapters.FavoritesAdapter
import com.giraffe.weatherforecasapplication.features.favorites.viewmodel.FavoritesVM
import com.giraffe.weatherforecasapplication.model.forecast.ForecastModel
import com.giraffe.weatherforecasapplication.model.repo.Repo
import com.giraffe.weatherforecasapplication.network.ApiClient
import com.giraffe.weatherforecasapplication.utils.UiState
import com.giraffe.weatherforecasapplication.utils.ViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class FavoritesFragment : Fragment(),FavoritesAdapter.OnSelectClick {
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
    private lateinit var itemTouchHelper:ItemTouchHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        factory =
            ViewModelFactory(Repo.getInstance(ApiClient, ConcreteLocalSource(requireContext())))
        viewModel = ViewModelProvider(this, factory)[FavoritesVM::class.java]
        sharedVM = ViewModelProvider(requireActivity(), factory)[SharedVM::class.java]
        adapter = FavoritesAdapter(mutableListOf(), this)
        viewModel.getFavorites()

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
                val forecast = adapter.list[position]
                tempForecast= adapter.list[position]
                viewModel.deleteFavorite(forecast)
                adapter.removeItem(position)
                observeOnDeletion()
            }
        }
        itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)

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
        showLoading()
        binding.rvFavorites.visibility = View.INVISIBLE
        onDrawerClick = activity as OnDrawerClick
        binding.ivMore.setOnClickListener {
            onDrawerClick.onClick()
        }




        binding.rvFavorites.adapter = adapter
        itemTouchHelper.attachToRecyclerView(binding.rvFavorites)
        lifecycleScope.launch {
            viewModel.favorites.collect {
                when (it) {
                    is UiState.Fail -> {
                        hideLoading()
                        binding.rvFavorites.visibility = View.INVISIBLE
                        Log.e(TAG, "fail: ${it.error}")
                    }
                    UiState.Loading -> {
                        Log.d(TAG, "loading: ")
                    }

                    is UiState.Success -> {
                        hideLoading()
                        binding.rvFavorites.visibility = View.VISIBLE
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


    }

    private fun observeOnDeletion(){
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

    override fun onSelectClick(forecast: ForecastModel) {
        sharedVM.selectForecast(forecast)
        findNavController().navigate(R.id.homeFragment)
    }

    private fun showLoading(){
        binding.itemsShimmer.startShimmer()
        binding.itemsShimmer.visibility = View.VISIBLE
    }

    private fun hideLoading(){
        binding.itemsShimmer.hideShimmer()
        binding.itemsShimmer.visibility = View.INVISIBLE
    }
}