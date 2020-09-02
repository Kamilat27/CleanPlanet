package com.example.ecojem.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.ecojem.R

class SplashFragment : Fragment() {

    interface Callbacks {

        fun onSignInSelected()

        fun onSignUpSelected()

    }
    private var callbacks: Callbacks? = null

    private lateinit var signUpButton: Button
    private lateinit var signInButton: Button

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.splash_screen_fragment, container, false)

        signUpButton = view.findViewById(R.id.sign_up_button) as Button
        signInButton = view.findViewById(R.id.sign_in_button) as Button
        (activity as AppCompatActivity)!!.supportActionBar!!.hide()

        signUpButton.setOnClickListener {
            callbacks?.onSignUpSelected()
        }

        signInButton.setOnClickListener{
            callbacks?.onSignInSelected()
        }

        return view
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }


}




