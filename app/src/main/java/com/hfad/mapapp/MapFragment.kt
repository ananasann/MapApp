package com.hfad.mapapp

import android.annotation.SuppressLint
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
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
    private val TARGET_LOCATION = Point(55.751574, 37.573856)
    private var mapObject: MapObject? = null
    private lateinit var confirmView: ConstraintLayout
    private lateinit var addressTxtV: TextView
    private lateinit var geocoder: Geocoder
    private lateinit var addresses: List<Address>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*arguments?.let {
            param1 = it.getString(ARG_PARAM1)
        }*/
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_map, container, false)
        mapView = root.findViewById(R.id.mapView)
        confirmView = root.findViewById(R.id.constraint_confirm)
        addressTxtV = root.findViewById(R.id.address)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView.map.move(
            CameraPosition(
                TARGET_LOCATION,
                11.0f, 0.0f, 0.0f
            ),
            Animation(Animation.Type.SMOOTH, 0F),
            null
        )
        getPoint()
    }

    private val listener = object : InputListener {
        @SuppressLint("UseCompatLoadingForDrawables")
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun onMapTap(map: Map, point: Point) {
            val view = View(requireContext()).apply {
                background = requireContext().getDrawable(R.drawable.ic_place_24px)
            }
            val newPoint = Point(point.latitude, point.longitude)
            Log.i("anya", "onMapTap: ${newPoint.latitude}")
            mapObject?.let {
                mapView.map.mapObjects.remove(it)
            }
            mapObject = addMarker(
                newPoint.latitude,
                newPoint.longitude,
                view
            )
            toggleLoginUI(true)
            addressTxtV.text = getAddresses(newPoint.latitude, newPoint.longitude)

        }

        override fun onMapLongTap(p0: Map, p1: Point) {
        }
    }

    fun getAddresses(lat: Double, lon: Double): String {
        geocoder = Geocoder(requireContext(), Locale.getDefault())
        addresses = geocoder.getFromLocation(lat, lon, 1)
        /* val city = addresses[0].locality
        val state = addresses[0].adminArea
        val country = addresses[0].countryName
        val postalCode = addresses[0].postalCode
        val knownName = addresses[0].featureName*/
        return addresses[0].getAddressLine(0) //address
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

    fun getPoint() {
        mapView.map.addInputListener(listener)
    }

    private fun toggleLoginUI(show: Boolean) {
        if (show) {
            confirmView.visibility = View.VISIBLE
        } else {
            confirmView.visibility = View.GONE
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
    }


    /* companion object {
         fun newInstance(param1: String, param2: String) =
             MapFragment().apply {
                 arguments = Bundle().apply {
                     //putString(ARG_PARAM1, param1)
                 }
             }
     }*/
}