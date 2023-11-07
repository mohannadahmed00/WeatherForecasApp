package com.giraffe.weatherforecasapplication.features.map.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.giraffe.weatherforecasapplication.R
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
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng

class MapFragment : Fragment(), OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
    GoogleMap.OnCameraIdleListener {
    companion object {
        const val TAG = "MapFragment"
    }
    private lateinit var binding: FragmentMapBinding
    private lateinit var viewModel: MapVM
    private lateinit var factory: ViewModelFactory

    //==============================================================
    private var mGoogleApiClient: GoogleApiClient? = null
    private lateinit var mLocationRequest: LocationRequest
    private lateinit var mLocationCallback: LocationCallback
    private var gMap: GoogleMap? = null
    //==============================================================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        factory = ViewModelFactory(Repo.getInstance(ApiClient, ConcreteLocalSource(requireContext())))
        viewModel = ViewModelProvider(this,factory)[MapVM::class.java]
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
        mGoogleApiClient?.apply{ connect() }
    }

    override fun onConnected(bundle: Bundle?) {
        val lat = SharedHelper.getInstance(requireContext()).read(Constants.LocationKeys.CURRENT_LAT)?.toDouble()?:0.0
        val lon = SharedHelper.getInstance(requireContext()).read(Constants.LocationKeys.CURRENT_LON)?.toDouble()?:0.0
        //gMap?.uiSettings?.isMyLocationButtonEnabled = false
        moveCamera(LatLng(lat,lon))
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
        val lat = gMap?.cameraPosition?.target?.latitude?:0.0
        val lon = gMap?.cameraPosition?.target?.longitude?:0.0
        SharedHelper.getInstance(requireContext()).store(Constants.LocationKeys.CURRENT_LAT,lat.toString())
        SharedHelper.getInstance(requireContext()).store(Constants.LocationKeys.CURRENT_LON,lon.toString())
        Log.i(TAG, "onCameraIdle: (lat:$lat , lon:$lon)")
    }
}