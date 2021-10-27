package com.hfad.mapapp

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.camera2.CameraManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.runtime.image.ImageProvider

class StartFragment : Fragment() {
    //private var param1: String? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var showOnMap: Button
    private lateinit var mapFragment: MapFragment
    private lateinit var locationManager: LocationManager
    private lateinit var cameraManager: CameraManager
    private val permissArray: Array<String> = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
    }
        /*arguments?.let {
            param1 = it.getString(ARG_PARAM1)
        }*/


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_start, container, false)
        showOnMap = root.findViewById(R.id.show_on_mp)
        return root
    }

    override fun onResume() {
        super.onResume()
        mapFragment = MapFragment()
        openFragment(mapFragment)
        getLocation()
    }

    private fun openFragment(fragment: Fragment) {
        showOnMap.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.activity_main, fragment, "startFragTag")
                //?.add(R.id.start_layout, fragment, "startFragTag")
                ?.addToBackStack(null)
                ?.commit()
        }
    }


    private fun getLocation() {
        locationManager = ContextCompat.getSystemService(
            requireContext(),
            LocationManager::class.java
        ) as LocationManager

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                permissArray,
                109
            )
        }
        locationManager.requestLocationUpdates(
            LocationManager.FUSED_PROVIDER,
            500,
            1f
        ) { location -> Log.i("anya2", "onLocationChanged: ${location.latitude}") }
        locationManager.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            500,
            1F
        ) { location -> Log.i("anya", "onLocationChanged: ${location.latitude}") }

        fusedLocationClient.lastLocation.addOnSuccessListener { location : Location? ->
            Log.i("anya", "onResume + lastLocation: $location")
            //запрос обновлений местоположения и получения последнего известного местоположения
        }

    }

   /* fun addMarker(latitude: Double,
                  longitude: Double,
                  @DrawableRes imageRes: Int,
                  userData: Any? = null): PlacemarkMapObject {
        val marker = MapObjectCollection.addPlacemark(
            Point(latitude, longitude),
            ImageProvider.fromResource(context, imageRes)
        )
        marker.userData = userData
        markerTapListener?.let { marker.addTapListener(it) }
        return marker
    }*/

    /*companion object {
        fun newInstance(param1: String, param2: String) =
            StartFragment().apply {
                arguments = Bundle().apply {
                    //putString(ARG_PARAM1, param1)
                }
            }
    }*/
}