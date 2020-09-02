package com.example.ecojem.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.ecojem.R
import com.parse.ParseUser

const val TAG = "SignInFragment"
class SignInFragment : Fragment() {

    interface Callbacks{
        fun onSignInButtonSelected()
    }

    private var callbacks: Callbacks? = null


    private lateinit var userPassword: EditText
    private lateinit var signInButton: Button
    private lateinit var userEmail: EditText

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.sign_in_screen_fragment, container, false)

        userPassword = view.findViewById(R.id.user_password) as EditText
        signInButton = view.findViewById(R.id.sign_in_button) as Button
        userEmail = view.findViewById(R.id.user_email) as EditText
        (activity as AppCompatActivity)!!.supportActionBar!!.hide()

        signInButton.setOnClickListener {
            val email = userEmail.text.toString()
            val password = userPassword.text.toString()


            ParseUser.logInInBackground(email, password, ({ user, e ->
                if (user != null) {
                    Log.i(TAG, "You are logged in")
                    //Toast.makeText(context, "You are signed in!", Toast.LENGTH_SHORT).show()
                    callbacks?.onSignInButtonSelected()

                } else {
                    Log.e(TAG, "Sign in failed!", e)
                    Toast.makeText(context, "Sign in failed!", Toast.LENGTH_SHORT).show()
                                    }})
            )
        }



        return view
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }



}
