package com.example.ecojem.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.ecojem.Event
import com.example.ecojem.R
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.material.textfield.TextInputEditText
import com.parse.ParseFile
import com.parse.ParseGeoPoint
import com.parse.ParseUser
import org.w3c.dom.Text
import java.io.ByteArrayOutputStream
import java.io.IOException

const val PICK_PHOTO_CODE2 = 1046
class ProfilePageFragment : Fragment() {
    interface Callbacks {
        fun onLogOutSelected()

    }

    private var callbacks: Callbacks? = null
    private lateinit var profileImage: ImageView
    private lateinit var userFirstName: TextInputEditText
    private lateinit var userLastName: TextInputEditText
    private lateinit var userEmail: TextInputEditText
    private lateinit var userUsername: TextInputEditText
    private lateinit var userPhone: TextInputEditText
    private lateinit var logOutButton: Button
    private lateinit var savePhotoButton: Button
    private var photoFile: ParseFile? = null


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
        val view = inflater.inflate(R.layout.profile_fragment, container, false)

        profileImage = view.findViewById(R.id.profile_image) as ImageView
        userFirstName = view.findViewById(R.id.user_firstName) as TextInputEditText
        userLastName = view.findViewById(R.id.user_lastName) as TextInputEditText
        userEmail = view.findViewById(R.id.user_email) as TextInputEditText
        userUsername = view.findViewById(R.id.user_username) as TextInputEditText
        userPhone = view.findViewById(R.id.user_phone) as TextInputEditText
        logOutButton = view.findViewById(R.id.log_out_button) as Button
        savePhotoButton = view.findViewById(R.id.save_photo_button) as Button

        profileImage.setOnClickListener{
            onPickPhoto(it)
        }


        savePhotoButton.setOnClickListener{
            val user = ParseUser.getCurrentUser()
            with(user) {
                photoFile?.let { it1 -> put("userPhoto", it1) }
                saveInBackground() { e ->
                    if (e == null) {
                        Log.i(TAG, "Photo has been saved!!")
                        Toast.makeText(context, "Фотография сохранена на сервере!!!", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Log.i(TAG, "Photo has not been saved!!")
                        Toast.makeText(context, "Photo has not been saved!", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }


        }


        logOutButton.setOnClickListener{
            ParseUser.logOut()
            Toast.makeText(context, "You are logged out!", Toast.LENGTH_SHORT)
                .show()
            callbacks?.onLogOutSelected()

        }



        val currentLoggedInUser = ParseUser.getCurrentUser().fetchIfNeeded()
        val photo = currentLoggedInUser.getParseFile("userPhoto")

        val firstName = currentLoggedInUser.getString("firstName")
        val lastName = currentLoggedInUser.getString("lastName")
        val email = currentLoggedInUser.email
        val username = currentLoggedInUser.username
        val phone = currentLoggedInUser.getInt("phone").toString()

        userFirstName.setText(firstName, TextView.BufferType.EDITABLE)
        userLastName.setText(lastName, TextView.BufferType.EDITABLE)
        userEmail.setText(email, TextView.BufferType.EDITABLE)
        userUsername.setText(username, TextView.BufferType.EDITABLE)
        userPhone.setText(phone, TextView.BufferType.EDITABLE)


        if (currentLoggedInUser.getParseFile("userPhoto") != null) {
            Glide.with(context!!).load(currentLoggedInUser?.getParseFile("userPhoto")!!.url).into(profileImage)
        }else {
            profileImage.setImageDrawable(context!!.getDrawable(R.drawable.profile_pic))
        }


        return view
    }

    //Trigger gallery selection for photo
    private fun onPickPhoto(view: View?) {
        // Create intent for picking a photo from the gallery
        val intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )

        //If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        //So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(context!!.packageManager) != null) {
            //bring up gallery to select a photo
            startActivityForResult(intent, PICK_PHOTO_CODE2)
        }
    }

    fun loadFromIri(photoUri: Uri?) : Bitmap?{
        var image: Bitmap? = null
        try {
            // check version of Android on device
            image = if (Build.VERSION.SDK_INT > 27) {
                // on newer versions of android, use the new decodeBitMap method
                val source: ImageDecoder.Source = ImageDecoder.createSource(context!!.contentResolver, photoUri!!)
                ImageDecoder.decodeBitmap(source)
            } else{
                //support older versions of Android by using getBitmap
                MediaStore.Images.Media.getBitmap(context!!.contentResolver, photoUri)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return image
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (data != null && requestCode == PICK_PHOTO_CODE2) {
            val photoUri: Uri? = data.data

            //Load the image located at photoURI into selectedImage
            val selectedImage = loadFromIri(photoUri)

            //Load the selected image into a preview
            val ivPreview = view?.findViewById(R.id.profile_image) as ImageView
            ivPreview.setImageBitmap(selectedImage)
            ivPreview.visibility = View.VISIBLE

            val bos = ByteArrayOutputStream()
            selectedImage?.let {
                it.compress(
                    Bitmap.CompressFormat.JPEG,
                    100,
                    bos
                ) // can use something 70 in case u want to compress the image
                val scaledData: ByteArray = bos.toByteArray()

                // Save the scaled image to Parse

                // Save the scaled image to Parse
                photoFile = ParseFile("eventPic.jpg", scaledData)
            }
        }

    }

    // fun onSave() {          }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }


}




