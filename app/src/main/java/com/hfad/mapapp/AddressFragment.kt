package com.hfad.mapapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class AddressFragment : Fragment() {

    private lateinit var addressOfSlctdPlc: TextView
    private lateinit var latOfSlctdPlc: TextView
    private lateinit var lonOfSlctdPlc: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_address, container, false)
        addressOfSlctdPlc = root.findViewById(R.id.address_of_slctd_plc)
        latOfSlctdPlc = root.findViewById(R.id.lat_of_slctd_plc)
        lonOfSlctdPlc = root.findViewById(R.id.lon_of_slctd_plc)
        return root
    }

    override fun onResume() {
        super.onResume()
        val arg = this.arguments
        if (arg != null) {
            val addressValue = arg.getString("keyForAddress", "")
            val latValue = arg.getString("keyForLat", "")
            val lonValue = arg.getString("keyForLon", "")

            addressOfSlctdPlc.text = addressValue
            latOfSlctdPlc.text = getString(R.string.latitude, latValue)
            lonOfSlctdPlc.text = getString(R.string.longitude, lonValue)
        }
    }
}