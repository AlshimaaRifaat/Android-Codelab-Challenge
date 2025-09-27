package com.sap.codelab.service

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat

/**
 * Manager class for starting and stopping location services.
 */
object ServiceManager {

    /**
     * Starts the location service to monitor for memo reminders.
     */
    fun startLocationService(context: Context) {
        val serviceIntent = Intent(context, LocationService::class.java)
        ContextCompat.startForegroundService(context, serviceIntent)
    }

    /**
     * Stops the location service.
     */
    fun stopLocationService(context: Context) {
        val serviceIntent = Intent(context, LocationService::class.java)
        context.stopService(serviceIntent)
    }
}
