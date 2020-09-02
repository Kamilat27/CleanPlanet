package com.example.ecojem

import android.location.Geocoder
import android.provider.ContactsContract
import com.parse.*
import java.io.File
import java.io.Serializable
import java.util.*


@ParseClassName("Event")
class Event : ParseObject(), Serializable {

    fun getId(): String? {
        return getString("objectId")
    }

    fun getTitle(): String? {
        return getString("title")
    }
    fun setTitle(value: String) {
        put("title", value)
    }

    fun getDescription(): String? {
        return getString("description")
    }

    fun setDescription(value: String) {
        put("description", value)
    }

    fun getLocation(): ParseGeoPoint? {
         return getParseGeoPoint("location")
    }

    fun setLocation(value: ParseGeoPoint) {
        put("location", value)
    }

    fun getImage() : ParseFile? {
        return getParseFile("image")
    }

    fun setImage(value: ParseFile) {
        put("image", value)
    }

    fun getOrganizer(): ParseRelation<ParseUser> {
        return getRelation("organizer")
    }

    fun setOrganizer(value: ParseRelation<ParseUser>) {
        put("organizer", value)
    }

    fun getDate() : Date? {
        return getDate("date")
    }

    fun setDate(value: Date) {
        put("date", value)
    }

    fun getCreator() : ParseUser? {
        return getParseUser("creator")
    }

    fun setCreator(value: ParseUser) {
        put("creator", value)
    }

    fun getLocationAddress() : String? {
        return getString("locationAddress")
    }

    fun setLocationAddress(value: String){
        put("locationAddress", value)
    }
}