package com.hfad.mapapp

import android.annotation.SuppressLint
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.*
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.ui_view.ViewProvider
import java.util.*

class MapFragment : Fragment() {

    private lateinit var mapView: MapView
    private var mapObject: MapObject? = null
    private lateinit var confirmView: ConstraintLayout
    private lateinit var addressTxtV: TextView
    private lateinit var geocoder: Geocoder
    private lateinit var addresses: List<Address>
    private lateinit var confirmBtn: Button
    private val addressFragment: AddressFragment = AddressFragment()
    val args = Bundle()
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_map, container, false)
        mapView = root.findViewById(R.id.mapView)
        confirmView = root.findViewById(R.id.constraint_confirm)
        addressTxtV = root.findViewById(R.id.address)
        confirmBtn = root.findViewById(R.id.confirm_button)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        val argsCur = this.arguments
        if (argsCur != null) {
            val curLat = argsCur.getDouble("keyForCurLat", 55.751574)
            val curLon = argsCur.getDouble("keyForCurLon", 37.573856)
            setCameraToPosition(curLat, curLon)
        } else {
            getLastLocationFromFused()
        }
        getPoint()
    }

    @SuppressLint("MissingPermission")
    // на этом экране permission уже получен
    private fun getLastLocationFromFused() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                setCameraToPosition(
                    it.latitude,
                    it.longitude
                )
            }
        }
    }

    private fun setCameraToPosition(curLat: Double, curLon: Double) {
        val TARGET_LOCATION = Point(curLat, curLon)
        mapView.map.move(
            CameraPosition(
                TARGET_LOCATION,
                16.0f, 0.0f, 0.0f
            ),
            Animation(Animation.Type.SMOOTH, 0F),
            null
        )
    }


    private val listener = object : InputListener {
        @SuppressLint("UseCompatLoadingForDrawables")
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun onMapTap(map: Map, point: Point) {
            val view = View(requireContext()).apply {
                background = requireContext().getDrawable(R.drawable.ic_place_24px)
            }
            val newPoint = Point(point.latitude, point.longitude)
            mapView.map.mapObjects.clear()
            mapObject = addMarker(
                newPoint.latitude,
                newPoint.longitude,
                view
            )
            toggleLoginUI(true)
            val geoAddress = getAddresses(newPoint.latitude, newPoint.longitude)
            addressTxtV.text = geoAddress

            args.putString("keyForAddress", geoAddress)
            args.putString("keyForLat", newPoint.latitude.toString())
            args.putString("keyForLon", newPoint.longitude.toString())
        }

        override fun onMapLongTap(p0: Map, p1: Point) {
        }
    }

    fun getAddresses(lat: Double, lon: Double): String {
        geocoder = Geocoder(requireContext(), Locale.getDefault())
        addresses = geocoder.getFromLocation(lat, lon, 1)
        return addresses[0].getAddressLine(0)
    }

    fun addMarker(
        latitude: Double,
        longitude: Double,
        view: View
    ): PlacemarkMapObject {
        return mapView.map.mapObjects.addPlacemark(
            Point(latitude, longitude),
            ViewProvider(view)
        )
    }

    private fun getPoint() {
        mapView.map.addInputListener(listener)
    }

    private fun toggleLoginUI(show: Boolean) {
        if (show) {
            confirmView.visibility = View.VISIBLE
        } else {
            confirmView.visibility = View.GONE
        }
    }

    private fun openFragment(fragment: Fragment) {
        fragment.arguments = args
        confirmBtn.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.activity_main, fragment, "mapFragTag")
                ?.addToBackStack(null)
                ?.commit()
        }
    }

    override fun onStop() {
        mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView.onStart()
        openFragment(addressFragment)
    }
}