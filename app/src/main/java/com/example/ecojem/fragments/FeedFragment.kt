package com.example.ecojem.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecojem.Event
import com.example.ecojem.R
import com.example.ecojem.models.EventListViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.parse.ParseQuery
import com.parse.ParseUser
import java.text.DateFormat
import java.text.SimpleDateFormat

class FeedFragment : Fragment() {
    interface Callbacks {
        fun onEventSelected(clickedEvent: Event)
        fun onFloatingButtonSelected()
    }

    private var callbacks: Callbacks? = null

    private var registered: Boolean = false

    private lateinit var eventRecyclerView: RecyclerView
    private lateinit var tableLayout: TabLayout
    private lateinit var floatingButton: FloatingActionButton
    private var adapter: EventAdapter? = EventAdapter(emptyList())

    private val eventListViewModel: EventListViewModel by lazy {
        ViewModelProviders.of(this).get(EventListViewModel::class.java)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Total events: ${eventListViewModel.emptyList.size}")
    }

    companion object{
        fun newInstance() : EventListViewModel {
            return EventListViewModel()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.feed_screen_fragment, container, false)

        tableLayout = view.findViewById(R.id.table_layout) as TabLayout
        floatingButton = view.findViewById(R.id.floating_action_button) as FloatingActionButton
        eventRecyclerView = view.findViewById(R.id.event_recycler_view) as RecyclerView
        eventRecyclerView.layoutManager = LinearLayoutManager(context)

        floatingButton.setOnClickListener{
            callbacks?.onFloatingButtonSelected()
        }

        tableLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let { clickedTab ->
                    if (clickedTab.position == 0)  {
                        registered = false
                        val query = ParseQuery.getQuery(Event::class.java)
                        query.findInBackground {eventsList, e ->
                            if (e == null) {

                                val adapterEventList = mutableListOf<Event>()
                                //put all events inside empty list
                                for (event in eventsList) {
                                    adapterEventList.add(event)
                                }
                                //set this new data on recyclerview
                                val myadapter = EventAdapter(adapterEventList)
                                eventRecyclerView.setAdapter(null);
                                eventRecyclerView.setLayoutManager(null);
                                eventRecyclerView.setAdapter(myadapter);
                                eventRecyclerView.layoutManager = LinearLayoutManager(context)
                                myadapter.notifyDataSetChanged();
                            } else {
                                Log.e("EventListViewModel", "Unable to query events", e)
                            }
                        }
                    } else if (clickedTab.position == 1) {
                        registered = true
                        //query to get all events for a user
                        val currentUser = ParseUser.getCurrentUser().fetchIfNeeded()
                        //get all of its event relations
                        val eventRelation = currentUser.getRelation<Event>("myEvents")

                        eventRelation.query.findInBackground{ list_of_events, error ->
                            if(error == null) {
                                //if no error we got list of events
                                //loop throught each event and add to eventList
                                val eventList = mutableListOf<Event>()
                                for (event in list_of_events) {
                                    eventList.add(event)
                                }
                                //set these new events on adapter and recyclerView
                                val myadapter = EventAdapter(eventList)
                                eventRecyclerView.setAdapter(null);
                                eventRecyclerView.setLayoutManager(null);
                                eventRecyclerView.setAdapter(myadapter);
                                eventRecyclerView.layoutManager = LinearLayoutManager(context)
                                myadapter.notifyDataSetChanged();
                            }
                        }

                        //user clicked on my events

                        //make query to get get all events that user is registered in

                    }
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                Log.i("S", "Do nothing")

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                Log.i("S", "Do nothing")
            }
        })

        getEvent()

        val events = eventListViewModel.emptyList
        adapter = EventAdapter(events)
        eventRecyclerView.adapter = adapter
        return view

    }

    fun getEvent() {
        val query = ParseQuery.getQuery(Event::class.java)
        query.findInBackground{eventsList, e ->
            if (e == null) {
                // (Event, Event, Event)
                for (event in eventsList) {
                   eventListViewModel.emptyList.add(event)
                }
                adapter?.notifyDataSetChanged()

            } else {
                Log.e("EventListViewModel", "Unable to query events", e)
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    private inner class EventHolder(view:View) :RecyclerView.ViewHolder(view), View.OnClickListener {
        private lateinit var event: Event

        private val title: TextView = itemView.findViewById(R.id.event_title)
        private val location: TextView = itemView.findViewById(R.id.event_location)
        private val date: TextView = itemView.findViewById(R.id.event_date)
        private val image: ImageView = itemView.findViewById(R.id.event_image)
        private val organizer: TextView = itemView.findViewById(R.id.event_organizer)
        private val registerButton: Button = itemView.findViewById(R.id.register_button)

        init {
            itemView.setOnClickListener(this)

        }

        fun bind(event: Event) {
            this.event = event


            title.text = event.getTitle()


            val eventCreator = event.getCreator()?.fetchIfNeeded()
            if(eventCreator != null) {
                organizer.text =
                    eventCreator.getString("firstName") + " " + eventCreator.getString("lastName")
            }


            if (event.getLocationAddress() != null) {
                location.text = "Address: ${event.getLocationAddress()}"
            }

            date.text = event.getDate().toString()


            if (event.getImage() != null) {
                Glide.with(context!!).load(event.getImage()!!.url).into(image)
            }else {
                image.setImageDrawable(context!!.getDrawable(R.drawable.subbotnik))
            }

            registerButton.visibility = if(registered) View.GONE else View.VISIBLE

            registerButton.setOnClickListener{

                val user = ParseUser.getCurrentUser()
                val relation = user.getRelation<Event>("myEvents")
                relation.add(event)

                user.saveInBackground{
                    if(it == null) {
                        Toast.makeText(context, "You have successfully registered for this event!", Toast.LENGTH_SHORT)
                            .show()
                        registered = true
                    } else {
                        Toast.makeText(context, "Failed to register for the event!", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }

        override fun onClick(v: View?) {

            callbacks?.onEventSelected(event)
        }
    }

    private inner class EventAdapter(var events: List<Event>) : RecyclerView.Adapter<EventHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventHolder {

            val view = layoutInflater.inflate(R.layout.list_item_event, parent, false)
            return EventHolder(view)
        }

        override fun getItemCount() = events.size

        override fun onBindViewHolder(holder: EventHolder, position: Int) {
            val event = events[position]
            holder.bind(event)

        }

        override fun getItemId(position: Int) = position.toLong()
        override fun getItemViewType(position: Int) = position

    }

}