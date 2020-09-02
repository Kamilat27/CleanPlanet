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
import com.google.android.material.textfield.TextInputEditText
import com.parse.ParseUser


const val TAG1 = "SignUpFragment"
class SignUpFragment : Fragment() {

    private var callbacks: SplashFragment.Callbacks? = null

    private lateinit var userFirstName: EditText
    private lateinit var userEmail: EditText
    private lateinit var userLastName: EditText
    private lateinit var userPassword: TextInputEditText
    private lateinit var userPhone: TextInputEditText
    private lateinit var username: EditText
    private lateinit var signUpButton: Button

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as SplashFragment.Callbacks?

    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.sign_up_screen_fragment, container, false)

        userFirstName = view.findViewById(R.id.user_first_name) as EditText
        userLastName = view.findViewById(R.id.user_last_name) as EditText
        userEmail = view.findViewById(R.id.user_email) as EditText
        userPhone = view.findViewById(R.id.user_phone) as TextInputEditText
        username = view.findViewById(R.id.user_name) as EditText
        userPassword = view.findViewById(R.id.user_password) as TextInputEditText
        signUpButton = view.findViewById(R.id.sign_up_button) as Button
        (activity as AppCompatActivity)!!.supportActionBar!!.hide()

        signUpButton.setOnClickListener{
            val firstName = userFirstName.text.toString()
            val lastName = userLastName.text.toString()
            val userEmail = userEmail.text.toString()
            val phone = Integer.parseInt(userPhone.text.toString())
            val userName = username.text.toString()
            val password = userPassword.text.toString()

            val user = ParseUser()

            with(user) {

                setPassword(password)
                email = userEmail
                username = userName
                put("phone", phone)
                put("firstName", firstName)
                put("lastName", lastName)
                signUpInBackground{e ->
                    if (e == null) {
                        Log.i(TAG1, "You are signed up!")
                        Toast.makeText(context, "вы успешно зарегистрировались!", Toast.LENGTH_SHORT).show()
                        callbacks?.onSignInSelected()
                    } else {
                        Log.i(TAG1, "Sign up failed!")
                        Toast.makeText(context, "Что-то пошло не так. Ошибка регистрации", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        return view
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

}