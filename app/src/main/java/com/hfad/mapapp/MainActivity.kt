package com.hfad.mapapp

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.yandex.mapkit.MapKitFactory

class MainActivity : AppCompatActivity() {

    private lateinit var startFragment: StartFragment
    private val locationPermissionCode = 2
    private lateinit var transaction: FragmentTransaction
    private val MAPKIT_API_KEY: String = "fe35bed6-6bef-401c-a925-5d12055f65a5"

    override fun onCreate(savedInstanceState: Bundle?) {
        MapKitFactory.setApiKey(MAPKIT_API_KEY)
        MapKitFactory.initialize(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
    }


    override fun onResume() {
        super.onResume()
        startFragment = StartFragment()
        openFragment(startFragment)
    }

    private fun openFragment(fragment: Fragment) {
        transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.activity_main, fragment)
        transaction.replace(R.id.activity_main, fragment)
        transaction.commit()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                //Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

