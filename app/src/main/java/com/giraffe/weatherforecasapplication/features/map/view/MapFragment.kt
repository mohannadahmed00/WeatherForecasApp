package com.giraffe.weatherforecasapplication.features.map.view

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.giraffe.weatherforecasapplication.R
import com.giraffe.weatherforecasapplication.SharedVM
import com.giraffe.weatherforecasapplication.database.ConcreteLocalSource
import com.giraffe.weatherforecasapplication.database.SharedHelper
import com.giraffe.weatherforecasapplication.databinding.FragmentMapBinding
import com.giraffe.weatherforecasapplication.features.map.viewmodel.MapVM
import com.giraffe.weatherforecasapplication.model.repo.Repo
import com.giraffe.weatherforecasapplication.network.ApiClient
import com.giraffe.weatherforecasapplication.utils.Constants
import com.giraffe.weatherforecasapplication.utils.ViewModelFactory
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar

class MapFragment : Fragment(), OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,
    GoogleMap.OnCameraIdleListener {
    companion object {
        const val TAG = "MapFragment"
    }

    private val args: MapFragmentArgs by navArgs()

    private lateinit var binding: FragmentMapBinding
    private lateinit var viewModel: MapVM
    private lateinit var sharedVM: SharedVM
    private lateinit var factory: ViewModelFactory
    private var mGoogleApiClient: GoogleApiClient? = null
    private var gMap: GoogleMap? = null
    private var lat: Double = 0.0
    private var lon: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        factory =
            ViewModelFactory(Repo.getInstance(ApiClient, ConcreteLocalSource(requireContext())))
        viewModel = ViewModelProvider(this, factory)[MapVM::class.java]
        sharedVM = ViewModelProvider(requireActivity(), factory)[SharedVM::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        binding.btnConfirm.setOnClickListener {
            if(isConnected()) {
                sharedVM.getForecast(lat,lon)
                findNavController().navigateUp()
            }else{
                Snackbar.make(view, getString(R.string.make_sure_you_are_connected_to_the_internet),Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(gMap: GoogleMap) {
        Log.i(TAG, "onMapReady: ")
        this.gMap = gMap
        buildGoogleApiClient()
        gMap.isMyLocationEnabled = true
    }

    private fun buildGoogleApiClient() {
        Log.i(TAG, "buildGoogleApiClient: ")
        mGoogleApiClient = GoogleApiClient.Builder(requireContext())
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API).build()
        mGoogleApiClient?.apply { connect() }
    }

    override fun onConnected(bundle: Bundle?) {
        val lat =
            SharedHelper.getInstance(requireContext()).read(Constants.LocationKeys.CURRENT_LAT)
                ?.toDouble() ?: 0.0
        val lon =
            SharedHelper.getInstance(requireContext()).read(Constants.LocationKeys.CURRENT_LON)
                ?.toDouble() ?: 0.0
        //gMap?.uiSettings?.isMyLocationButtonEnabled = false
        moveCamera(LatLng(args.lat.toDouble(), args.lon.toDouble()))
        gMap?.setOnCameraIdleListener(this)
    }

    private fun moveCamera(latLng: LatLng) {
        gMap?.animateCamera(CameraUpdateFactory.newLatLng(latLng))
        gMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
    }

    override fun onConnectionSuspended(p0: Int) {
        TODO("Not yet implemented")
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("Not yet implemented")
    }

    override fun onCameraIdle() {
        lat = gMap?.cameraPosition?.target?.latitude ?: 0.0
        lon = gMap?.cameraPosition?.target?.longitude ?: 0.0
    }

    private fun isConnected(): Boolean {
        val connectivityManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork) != null
    }
}