package com.hfad.mapapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.yandex.mapkit.MapKitFactory

class MainActivity : AppCompatActivity() {

    private lateinit var startFragment: StartFragment
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
        transaction.commit()
    }
}

