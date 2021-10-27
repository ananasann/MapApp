package com.hfad.mapapp

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.*
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.ui_view.ViewProvider

class MapFragment : Fragment() {

    private lateinit var mapView: MapView
    private val TARGET_LOCATION = Point(55.751574, 37.573856)
    private var mapObject: MapObject? = null


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
        }
        override fun onMapLongTap(p0: Map, p1: Point) {
        }
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