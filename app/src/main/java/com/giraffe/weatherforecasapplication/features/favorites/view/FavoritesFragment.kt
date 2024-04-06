package com.giraffe.weatherforecasapplication.features.favorites.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.giraffe.weatherforecasapplication.OnDrawerClick
import com.giraffe.weatherforecasapplication.R
import com.giraffe.weatherforecasapplication.SharedVM
import com.giraffe.weatherforecasapplication.database.ConcreteLocalSource
import com.giraffe.weatherforecasapplication.databinding.FragmentFavoritesBinding
import com.giraffe.weatherforecasapplication.features.favorites.view.adapters.FavoritesAdapter
import com.giraffe.weatherforecasapplication.model.forecast.ForecastModel
import com.giraffe.weatherforecasapplication.model.repo.Repo
import com.giraffe.weatherforecasapplication.network.ApiClient
import com.giraffe.weatherforecasapplication.utils.ViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class FavoritesFragment : Fragment(), FavoritesAdapter.OnSelectClick {
    companion object {
        const val TAG = "FavoritesFragment"
    }

    private lateinit var binding: FragmentFavoritesBinding
    private lateinit var factory: ViewModelFactory
    private lateinit var onDrawerClick: OnDrawerClick
    private lateinit var adapter: FavoritesAdapter
    private lateinit var sharedVM: SharedVM
    private var tempForecast: ForecastModel? = null
    private lateinit var itemTouchHelper: ItemTouchHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleInit()
        adapter = FavoritesAdapter(mutableListOf(), this)
        sharedVM.getFavorites()

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
                tempForecast = adapter.list[position]
                sharedVM.justDeleteFavorite(forecast)
                showRetrySnackbar(requireView(),"${forecast.timezone} has been deleted"){
                    tempForecast?.let {
                        sharedVM.justInsertFavorite(it)
                    }
                }
                adapter.removeItem(position)
            }
        }
        itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)

    }

    fun showRetrySnackbar(view: View, message: String, onRetryClick: () -> Unit) {
        val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
        snackbar.setAction("Undo") {
            onRetryClick()
        }
        snackbar.show()
    }

    private fun handleInit() {
        factory =
            ViewModelFactory(Repo.getInstance(ApiClient, ConcreteLocalSource(requireContext())))
        sharedVM = ViewModelProvider(requireActivity(), factory)[SharedVM::class.java]
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
            sharedVM.favorites.collect {
                hideLoading()
                binding.rvFavorites.visibility = View.VISIBLE
                adapter.updateList(it)
            }
        }
    }

    override fun onSelectClick(forecast: ForecastModel) {
        sharedVM.setSelectedForecast(forecast)
        findNavController().navigate(R.id.homeFragment)
    }

    private fun showLoading() {
        binding.itemsShimmer.startShimmer()
        binding.itemsShimmer.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        binding.itemsShimmer.hideShimmer()
        binding.itemsShimmer.visibility = View.INVISIBLE
    }
}