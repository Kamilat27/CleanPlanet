package com.example.ecojem.models

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.ecojem.Event
import com.parse.ParseQuery




class EventListViewModel : ViewModel() {
    val emptyList = mutableListOf<Event>()

    fun getEvent() {
        val query = ParseQuery.getQuery(Event::class.java)
        query.findInBackground{eventsList, e ->
            if (e == null) {
                // (Event, Event, Event)
                for (event in eventsList) {
                    emptyList.add(event)


                }

            } else {
                Log.e("EventListViewModel", "Unable to query events", e)
            }
        }

    }
}