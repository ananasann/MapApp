package com.hfad.mapapp

import android.Manifest
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class StartFragment : Fragment() {

    lateinit var showOnMap: Button
    private lateinit var mapFragment: MapFragment
    private lateinit var locationManager: LocationManager
    private val permissArray: Array<String> = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    val argsCurrent = Bundle()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

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
        ) {
            argsCurrent.putDouble("keyForCurLat", it.latitude)
            argsCurrent.putDouble("keyForCurLon", it.longitude)
        }
        locationManager.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            500,
            1F
        ) { }
    }
}