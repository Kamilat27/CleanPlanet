package com.example.ecojem.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ecojem.R
import com.example.ecojem.fragments.*

class MainActivity : AppCompatActivity(), SplashFragment.Callbacks, SignInFragment.Callbacks{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)

        if (currentFragment == null) {
            val fragment = SplashFragment()
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit()
        }
    }

    override fun onSignInButtonSelected() {
        val intent = Intent(this, LoggedInActivity::class.java)
        startActivity(intent)
    }

    override fun onSignInSelected(){
        val fragment = SignInFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
    override fun onSignUpSelected(){
        val fragment = SignUpFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }


}