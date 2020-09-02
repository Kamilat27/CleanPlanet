package com.example.ecojem.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.icu.util.Calendar
import android.location.Location
import android.media.Image
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.ButtonBarLayout
import androidx.fragment.app.Fragment
import com.example.ecojem.Event
import com.example.ecojem.R
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.textfield.TextInputEditText
import com.parse.Parse
import com.parse.ParseFile
import com.parse.ParseGeoPoint
import com.parse.ParseUser
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog.newInstance


import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotlinx.android.synthetic.main.compose_screen_fragment.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*


const val PICK_PHOTO_CODE = 1046
private val AUTOCOMPLETE_REQUEST_CODE = 1



class ComposeEventFragment : Fragment(), com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    interface Callbacks{
        fun onSaveButtonSelected()
    }

    private var callbacks: Callbacks? = null


    private lateinit var title: TextInputEditText
    private lateinit var description: TextInputEditText
    private lateinit var date: EditText
    private lateinit var dateWrapper: TextInputEditText
    private lateinit var location: TextInputEditText
    private lateinit var saveButton: Button
    private lateinit var photoButton: Button
    private lateinit var eventDate: TextInputEditText
    private lateinit var eventTime: TextInputEditText
    private var photoFile: ParseFile? = null
    private var pickedLocationGeoPoint: ParseGeoPoint? = null
    private var locationAddressName: String? = null
    @RequiresApi(Build.VERSION_CODES.N)
    private var pickedTimeAndDate: Calendar = Calendar.getInstance()


    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?

    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.compose_screen_fragment, container, false)

        title = view.findViewById(R.id.compose_title) as TextInputEditText
        description = view.findViewById(R.id.compose_description) as TextInputEditText
        location = view.findViewById(R.id.compose_location) as TextInputEditText
        saveButton = view.findViewById(R.id.save_button) as Button
        photoButton = view.findViewById(R.id.photo) as Button
        eventDate = view.findViewById(R.id.compose_date) as TextInputEditText
        eventTime = view.findViewById(R.id.compose_time) as TextInputEditText

        location.setOnClickListener{
            // Set the fields to specify which types of data to
            // return after the user has made a selection.
            val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)
            // Start the autocomplete intent.
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                .build(context!!)
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)



        }


        eventDate.setOnClickListener{
            val now = Calendar.getInstance()
            val dpd: com.wdullaer.materialdatetimepicker.date.DatePickerDialog = newInstance(
                this,
                now[Calendar.YEAR],
                now[Calendar.MONTH],
                now[Calendar.DAY_OF_MONTH]
            )
            dpd.show(activity!!.fragmentManager, "Datepickerdialog")        }

        eventTime.setOnClickListener{
            val now = Calendar.getInstance()
            val dpd: TimePickerDialog = TimePickerDialog.newInstance(
                this,
                now[Calendar.HOUR],
                now[Calendar.MINUTE],
                true
            )
            dpd.show(activity!!.fragmentManager, "Timepickerdialog")
        }

        photoButton.setOnClickListener {
            onPickPhoto(it)
        }




        saveButton.setOnClickListener {
            saveButton.isClickable = false
            val composeTitle = title.text.toString()
            val composeDescription = description.text.toString()
            val composeLocation = location.text.toString()

            val createEvent = Event()

            val user = ParseUser.getCurrentUser()

            val pickedDate = pickedTimeAndDate.time


            with(createEvent) {
                setTitle(composeTitle)
                setDescription(composeDescription)
                setDate(pickedDate)
                setCreator(user)
                if (photoFile != null) setImage(photoFile!!)
                if (pickedLocationGeoPoint != null) setLocation(pickedLocationGeoPoint!!)
                if (locationAddressName != null) setLocationAddress(locationAddressName!!)



                saveInBackground() { e ->
                 if (e == null) {
                        Log.i(TAG, "Your Event has been saved!")
                        Toast.makeText(context, "Ваше мероприятие сохранено!", Toast.LENGTH_SHORT)
                            .show()
                        callbacks?.onSaveButtonSelected()
                    } else{  Log.i(TAG, "Event has not been saved!")
                        saveButton.isClickable = true
                        Toast.makeText(context, "Event has not been saved!", Toast.LENGTH_SHORT).show()
                }


                }

            }
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
            startActivityForResult(intent, PICK_PHOTO_CODE)
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

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        val place = Autocomplete.getPlaceFromIntent(data)
                        Log.i(TAG, "Place: ${place.name}, ${place.id}, ${place.address} ${place.latLng}")
                        location.setText("${place.name}", TextView.BufferType.EDITABLE);

                        locationAddressName = place.address

                        place.latLng?.let {
                            val latitude = it.latitude
                            val longitude = it.longitude
                            pickedLocationGeoPoint = ParseGeoPoint(latitude, longitude)
                        }
                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    data?.let {
                        val status = Autocomplete.getStatusFromIntent(data)
                        Log.i(TAG, status.statusMessage)
                    }
                }
                Activity.RESULT_CANCELED -> {
                    // The user canceled the operation.
                }
            }
            return
        }

        if (data != null && requestCode == PICK_PHOTO_CODE) {
            val photoUri: Uri? = data.data

            //Load the image located at photoURI into selectedImage
            val selectedImage = loadFromIri(photoUri)

            //Load the selected image into a preview
            val ivPreview = view?.findViewById(R.id.preview_photo) as ImageView
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




    @RequiresApi(Build.VERSION_CODES.N)
    override fun onTimeSet(view: TimePickerDialog?, hourOfDay: Int, minute: Int, second: Int) {
        Log.v("t","works")
        eventTime.setText("$hourOfDay:$minute")

        pickedTimeAndDate.set(Calendar.HOUR_OF_DAY, hourOfDay)
        pickedTimeAndDate.set(Calendar.MINUTE, minute)
        pickedTimeAndDate.set(Calendar.SECOND, second)



    }


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onDateSet(
        view: com.wdullaer.materialdatetimepicker.date.DatePickerDialog?,
        year: Int,
        monthOfYear: Int,
        dayOfMonth: Int
    ) {

        pickedTimeAndDate.set(year, monthOfYear, dayOfMonth)
        val monthOfYear = monthOfYear + 1
        eventDate.setText("$dayOfMonth-$monthOfYear-$year")
    }


}


