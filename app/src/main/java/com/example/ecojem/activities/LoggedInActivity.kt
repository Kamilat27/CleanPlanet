package com.example.ecojem.activities

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.ecojem.Event
import com.example.ecojem.R
import com.example.ecojem.fragments.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.android.synthetic.main.logged_in_activity.*
import java.util.*


class LoggedInActivity : AppCompatActivity(), FeedFragment.Callbacks, ComposeEventFragment.Callbacks, ProfilePageFragment.Callbacks {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.logged_in_activity)


        val currentFragment = supportFragmentManager.findFragmentById(R.id.logged_in_fragment_container)

        if (currentFragment == null) {
            val fragment = FeedFragment()
            supportFragmentManager
                .beginTransaction()
                .add(R.id.logged_in_fragment_container, fragment)
                .commit()
        }


        val feedFragment = FeedFragment()
        val mapsActivity = MapsActivity()
        val myProfileFragment = ProfilePageFragment()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)


        bottomNavigationView.setOnNavigationItemSelectedListener { item: MenuItem ->
            when (item.getItemId()) {
                R.id.action_feed -> { supportFragmentManager.beginTransaction().replace(R.id.logged_in_fragment_container, feedFragment)
                    .commit()}
                R.id.action_maps -> {val intent = Intent(this, mapsActivity::class.java)
                startActivity(intent)}
                R.id.action_my_profile -> { supportFragmentManager.beginTransaction().replace(R.id.logged_in_fragment_container, myProfileFragment)
                    .commit()}


                else -> feedFragment
            }



            return@setOnNavigationItemSelectedListener true
        }


    }

    override fun onLogOutSelected() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }


    override fun onEventSelected(clickedEvent: Event) {
        val fragment = EventDetailFragment.newInstance(clickedEvent)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.logged_in_fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onFloatingButtonSelected() {
        val fragment = ComposeEventFragment()
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.logged_in_fragment_container, fragment)
                .addToBackStack(null)
                .commit()

    }

    override fun onSaveButtonSelected() {
        val fragment = FeedFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.logged_in_fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

}