package com.example.ecojem

import android.app.Application
import com.google.android.libraries.places.api.Places
import com.parse.Parse
import com.parse.ParseObject
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor


class ParseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize the SDK
        val apiKey = "AIzaSyBNb5lmCWYXzV8GNiXv2rqbuBc1yY-CZh8"
        Places.initialize(applicationContext, apiKey)

        // Create a new PlacesClient instance
        val placesClient = Places.createClient(this)


        // Use for troubleshooting -- remove this line for production
        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG)

        // Use for monitoring Parse OkHttp traffic
        // Can be Level.BASIC, Level.HEADERS, or Level.BODY
        // See http://square.github.io/okhttp/3.x/logging-interceptor/ to see the options.
        val builder = OkHttpClient.Builder()
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        builder.networkInterceptors().add(httpLoggingInterceptor)

        ParseObject.registerSubclass(Event::class.java)

        // set applicationId, and server server based on the values in the back4app settings.
        // any network interceptors must be added with the Configuration Builder given this syntax
        Parse.initialize(Parse.Configuration.Builder(this)
            .applicationId("lD4jrCqLUZQaecZF2RaTRS9swBAdDgagmbLkJAK2") // should correspond to Application ID env variable
            .clientKey("WwYNHEadsZLh57gP3JiFXo5LhqdMGtyDjHVuh1bV")  // should correspond to Client key env variable
                .server("https://parseapi.back4app.com").build());



        // New test creation of object below
        // New test creation of object below


    }

}