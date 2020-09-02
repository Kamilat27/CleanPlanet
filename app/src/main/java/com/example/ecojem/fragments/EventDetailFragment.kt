package com.example.ecojem.fragments

import android.os.Bundle
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
import com.parse.ParseUser

private const val CLICKED_EVENT = "event_clicked"

class EventDetailFragment : Fragment() {
    private lateinit var image: ImageView
    private lateinit var title: TextView
    private lateinit var description: TextView
    private lateinit var organizer: TextView
    private lateinit var date: TextView
    private lateinit var location: TextView
    private lateinit var registerButton: Button
    private var passedEvent: Event? = null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        passedEvent = arguments?.getSerializable(CLICKED_EVENT) as Event?



    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.event_detail_screen_fragment, container, false)
        image = view.findViewById(R.id.event_image) as ImageView
        title = view.findViewById(R.id.event_title) as TextView
        description = view.findViewById(R.id.event_description) as TextView
        organizer = view.findViewById(R.id.event_organizer) as TextView
        date = view.findViewById(R.id.event_date) as TextView
        location = view.findViewById(R.id.event_location) as TextView
        registerButton = view.findViewById(R.id.register_button) as Button

        title.text = passedEvent?.getTitle().toString()
        date.text = passedEvent?.getDate().toString()

        if (passedEvent?.getDescription() != null) {
                description.text = passedEvent?.getDescription().toString()
        } else {
            description.text = " "
        }

        val eventCreator = passedEvent?.getCreator()?.fetchIfNeeded()
        if(eventCreator != null) {
            organizer.text =
                eventCreator.getString("firstName") + " " + eventCreator.getString("lastName")
        }

        if (passedEvent?.getLocationAddress() != null) {
            location.text = "Address meropriyatie: ${passedEvent?.getLocationAddress()}"
        }



        if (passedEvent?.getImage() != null) {
            Glide.with(context!!).load(passedEvent?.getImage()!!.url).into(image)
        }else {
            image.setImageDrawable(context!!.getDrawable(R.drawable.subbotnik))
        }

        registerButton.setOnClickListener{

            val user = ParseUser.getCurrentUser()
            val relation = user.getRelation<Event>("myEvents")
            relation.add(passedEvent)

            user.saveInBackground{
                if(it == null) {
                    Toast.makeText(context, "Вы успешно зарегистрировались на это мероприятие!", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(context, "Failed to register for the event!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        return view
    }

    companion object {
        fun newInstance(clickedEvent: Event): EventDetailFragment {
            val args = Bundle().apply {
                putSerializable(CLICKED_EVENT, clickedEvent)
            }
            return EventDetailFragment().apply {
                arguments = args
            }
        }

    }
}


