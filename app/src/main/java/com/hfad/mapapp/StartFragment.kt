package com.hfad.mapapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class StartFragment : Fragment() {

    lateinit var showOnMap: Button
    private lateinit var mapFragment: MapFragment
    private val locationPermissionCode = 109
    private lateinit var locationManager: LocationManager
    private val permissArray: Array<String> = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    private val argsCurrent = Bundle()

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
        locationManager = ContextCompat.getSystemService(
            requireContext(),
            LocationManager::class.java
        ) as LocationManager

        showOnMap.setOnClickListener {
            checkPermission()
        }

        if (permissionDeniedJustNow) {
            runNextFragment()
            permissionDeniedJustNow = false
        }
    }

    private fun runNextFragment() {
        mapFragment = MapFragment()
        mapFragment.arguments = argsCurrent
        activity?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(R.id.activity_main, mapFragment, "startFragTag")
            ?.addToBackStack(null)
            ?.commit()
    }

    private fun checkPermission() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                permissArray,
                locationPermissionCode
            )
        } else {
            getLocation()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkGps()
            } else {
                permissionDeniedJustNow = true
            }
        }
    }

    // permissionResult меняет состояние фрагмента и не позволяет сразу запустить след фрагмент
    // соу используем флаг, который учитывается в онРезюм
    private var permissionDeniedJustNow = false

    private val locationListener: LocationListener = LocationListener { p0 ->
        argsCurrent.putDouble("keyForCurLat", p0.latitude)
        argsCurrent.putDouble("keyForCurLon", p0.longitude)
        unregisterMe()
        runNextFragment()
    }

    //чтобы считал значение только 1 раз
    private fun unregisterMe() {
        locationManager.removeUpdates(locationListener)
    }

    @SuppressLint("MissingPermission")
    // реализуется только если получен permission
    private fun getLocation() {
        locationManager.requestLocationUpdates(
            LocationManager.FUSED_PROVIDER,
            500,
            1f,
            locationListener
        )
        locationManager.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            500,
            1F
        ) { }
    }

    private fun checkGps() {
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            getLocation()
        } else {
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            //buildAlertMessageNoGps()
        }
    }

    //диалоговое окно работает исправно, при желании можно с ним
    private fun buildAlertMessageNoGps() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
            .setCancelable(false)
            .setPositiveButton(
                android.R.string.ok,
                DialogInterface.OnClickListener() { dialog: DialogInterface?, i: Int ->
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                })
            .setNegativeButton(
                android.R.string.cancel,
                DialogInterface.OnClickListener() { dialog: DialogInterface?, i: Int ->
                    runNextFragment()
                    dialog?.cancel()
                })
            .show()
    }
}
